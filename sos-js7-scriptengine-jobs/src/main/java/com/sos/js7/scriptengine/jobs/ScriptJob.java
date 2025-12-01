package com.sos.js7.scriptengine.jobs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.io.IOAccess;

import com.sos.commons.credentialstore.CredentialStoreArguments;
import com.sos.commons.util.arguments.base.ASOSArguments;
import com.sos.commons.util.arguments.base.SOSArgument.DisplayMode;
import com.sos.commons.util.keystore.KeyStoreArguments;
import com.sos.commons.util.proxy.ProxyConfigArguments;
import com.sos.commons.vfs.ssh.commons.SSHProviderArguments;
import com.sos.js7.job.Job;
import com.sos.js7.job.JobArgument;
import com.sos.js7.job.JobArguments;
import com.sos.js7.job.OrderProcessStep;
import com.sos.js7.job.exception.JobArgumentException;
import com.sos.js7.scriptengine.jobs.commons.ScriptJobOptionsReader;
import com.sos.js7.scriptengine.jobs.exceptions.ScriptJobException;
import com.sos.js7.scriptengine.jobs.exceptions.ScriptJobRunTimeException;

public abstract class ScriptJob extends Job<JobArguments> {

    /** Basic JS7 Job definitions (a maximum of 2 definitions are loaded)<br/>
     * - js: resources/JavaScriptJob.jobdef<br/>
     * - python: resources/PythonJob.jobdef */
    private static final Map<String, String> JOB_DEFINITIONS = new ConcurrentHashMap<>();

    private static final String POLYGLOT_ENGINE_SYSTEM_PROPERTY_WARN_INTERPRETER_ONLY = "polyglot.engine.WarnInterpreterOnly";

    private static final String FUNCTION_NAME_GET_JOB = "getJS7Job";

    private static final String JOB_PROPERY_DECLARED_ARGUMENTS = "declaredArguments";

    private static final String JOB_METHOD_SET_JOB_ENVIRONMENT = "setJobEnvironment";
    private static final String JOB_METHOD_GET_DECLARED_ARGUMENTS = "getDeclaredArguments";
    private static final String JOB_METHOD_PROCESS_ORDER = "processOrder";
    private static final String JOB_METHOD_ON_PROCESS_ORDER_CANCELED = "onProcessOrderCanceled";

    private static final Map<String, String> INCLUDABLE_ARGUMENTS = Stream.of(new String[][] {
            // CredentialStore
            { CredentialStoreArguments.CLASS_KEY, CredentialStoreArguments.class.getName() }
            // Proxy
            , { ProxyConfigArguments.CLASS_KEY, ProxyConfigArguments.class.getName() }
            // Java KeyStore
            , { KeyStoreArguments.CLASS_KEY, KeyStoreArguments.class.getName() }
            // SSHProvider
            , { SSHProviderArguments.CLASS_KEY, SSHProviderArguments.class.getName() }, })
            // toMap
            .collect(Collectors.toMap(data -> data[0], data -> data[1]));

    private final String language;

    private String script;
    private volatile JobArguments declaredArguments;
    private final Object declaredArgumentsLock = new Object();

    static {
        String warnProperty = System.getProperty(POLYGLOT_ENGINE_SYSTEM_PROPERTY_WARN_INTERPRETER_ONLY);
        if (warnProperty == null) {
            System.setProperty(POLYGLOT_ENGINE_SYSTEM_PROPERTY_WARN_INTERPRETER_ONLY, "false");
        }
    }

    public ScriptJob(JobContext jobContext, String language, String jobDefinitionResourceName) {
        super(jobContext);

        if (jobContext != null) {
            script = jobContext.asScala().executable().script();
        }
        this.language = language;
        JOB_DEFINITIONS.computeIfAbsent(language, lang -> loadResource(this, jobDefinitionResourceName));
    }

    protected abstract Object tryApplyArgumentDefaultValueFromMembers(JobArgument<?> arg, Type argumentJavaType, Value defaultValue);

    /** @apiNote currently not used */
    protected abstract Boolean isMethodOverridden(Value job, String methodName);

    @Override
    /** @apiNote The {@code declaredArguments} are set using {@link Job#beforeCreateJobArguments(List, OrderProcessStep)} instead of {@link Job#onStart()}
     *          because, for successful script evaluation, it may be necessary to provide certain Polyglot context options.<br/>
     *          For example, if the script imports JavaScript or Python modules defined in the script header rather than in the processOrder,<br/>
     *          these options need to be available.<br/>
     *          The {@code onStart} method does not support reading from JobResources or handling argument priorities such as {@code lastSucceededOutcome} or
     *          {@code order}. */
    public JobArguments beforeCreateJobArguments(List<JobArgumentException> exceptions, final OrderProcessStep<JobArguments> step) throws Exception {
        if (declaredArguments == null) {
            // Thread-safety is ensured using a private lock (declaredArgumentsLock) rather than {this},
            // preventing external code or subclasses from interfering and isolating synchronization to this initialization.
            synchronized (declaredArgumentsLock) {
                if (declaredArguments == null) {
                    ScriptJobOptionsReader reader = new ScriptJobOptionsReader(language);
                    reader.readFromPreAssignedArgumentValue(step);
                    try (Context context = createBuilder(reader).build()) {
                        try {
                            Value job = createJobFromScript(context);
                            // Retrieve Job declaredArguments
                            setDeclaredArguments(reader.createArgumentOptions(), reader.createArgumentOptionsResolved(), job);
                        } catch (PolyglotException e) {
                            throw new ScriptJobException(this, getJobDefinitionLinesCount(this), e);
                        }
                    }
                }
            }
        }
        return declaredArguments;
    }

    @Override
    public void processOrder(OrderProcessStep<JobArguments> step) throws Exception {
        ScriptJobOptionsReader reader = new ScriptJobOptionsReader(language);
        reader.read(step);

        Builder builder = createBuilder(reader);
        try (Context context = builder.build()) {
            try {
                Value job = createJobFromScript(context);
                // Call job processOrder
                job.invokeMember(JOB_METHOD_PROCESS_ORDER, step);
            } catch (PolyglotException e) {
                throw new ScriptJobException(this, getJobDefinitionLinesCount(this), e);
            }
        }
    }

    /** com.sos.js7.job.Job - [cancel/kill][job name=javascript_job][onOrderProcessCancel]<br/>
     * java.lang.IllegalStateException: <br/>
     * Multi threaded access requested by thread Thread[#46,JS7 blocking job 46,5,main] but is not allowed for language(s) js.<br/>
     * at com.oracle.truffle.polyglot.PolyglotEngineException.illegalState(PolyglotEngineException.java:135) ~[org.graalvm.truffle:?]<br>
     * ... */
    @Override
    public void onProcessOrderCanceled(OrderProcessStep<JobArguments> step) throws Exception {
        ScriptJobOptionsReader reader = new ScriptJobOptionsReader(language);
        reader.read(step);

        Builder builder = createBuilder(reader);
        try (Context context = builder.build()) {
            try {
                Value job = createJobFromScript(context);
                // Call job onProcessOrderCanceled
                job.invokeMember(JOB_METHOD_ON_PROCESS_ORDER_CANCELED, step);
            } catch (PolyglotException e) {
                throw new ScriptJobException(this, getJobDefinitionLinesCount(this), e);
            }
        }
    }

    private Value createJobFromScript(Context context) throws Exception {
        context.eval(language, getJobDefinition(this) + "\n" + script);

        Value getJobFunc = context.getBindings(language).getMember(FUNCTION_NAME_GET_JOB);
        // Instantiate Job (empty constructor)
        Value job = getJobFunc.execute();

        // Set Job JobEnvironment
        job.invokeMember(JOB_METHOD_SET_JOB_ENVIRONMENT, getJobEnvironment());
        return job;
    }

    private void setDeclaredArguments(JobArgument<String> argOptions, JobArgument<Map<String, String>> argOptionsResolved, Value job)
            throws Exception {
        // e.g., Python job with a custom constructor that skips super().__init__();
        // declaredArguments remains unset
        Value propertyDeclaredArguments = job.getMember(JOB_PROPERY_DECLARED_ARGUMENTS);
        if (propertyDeclaredArguments == null) {
            job.putMember(JOB_PROPERY_DECLARED_ARGUMENTS, null);
        }

        List<JobArgument<?>> declared = new ArrayList<>();
        List<ASOSArguments> included = new ArrayList<>();

        Value declaredArgs = job.invokeMember(JOB_METHOD_GET_DECLARED_ARGUMENTS);
        if (declaredArgs.hasMembers()) {
            for (String key : declaredArgs.getMemberKeys()) {
                Value member = declaredArgs.getMember(key);

                // List - IncludableArguments
                if (member.hasArrayElements()) {
                    for (long i = 0; i < member.getArraySize(); i++) {
                        Value elem = member.getArrayElement(i);
                        if (elem.isString()) {
                            String includedArg = elem.asString();
                            try {
                                included.add((ASOSArguments) Class.forName(INCLUDABLE_ARGUMENTS.get(includedArg)).getDeclaredConstructor()
                                        .newInstance());
                            } catch (Exception e) {
                                throw new JobArgumentException("[IncludableArgument][allowed=" + INCLUDABLE_ARGUMENTS.keySet()
                                        + "][a new instance cannot be created for=" + includedArg + "]" + e.toString(), e);
                            }
                        }
                    }
                }
                // JobArgument
                else {
                    Value nameValue = member.getMember("name");
                    if (nameValue == null) {
                        continue;
                    }
                    Value requiredValue = member.getMember("required");
                    if (requiredValue == null) {
                        continue;
                    }

                    String name = nameValue.asString();
                    boolean required = requiredValue.asBoolean();
                    Value defaultValue = member.getMember("defaultValue");
                    Value displayModeValue = member.getMember("displayMode");
                    Value type = member.getMember("type");

                    JobArgument<?> arg = new JobArgument<>(name, required);
                    if (displayModeValue != null) {
                        String displayMode = displayModeValue.asString();
                        try {
                            arg.setDisplayMode(DisplayMode.valueOf(displayMode.toUpperCase()));
                        } catch (Exception e) {
                            throw new JobArgumentException("[argument=" + name + "][DisplayMode][unknown value=" + displayMode + "]" + e.toString(),
                                    e);
                        }
                    }
                    tryApplyArgumentType(arg, type, defaultValue);
                    arg.setIsDirty(false);
                    declared.add(arg);
                }
            }
        }

        if (argOptions != null) {
            declared.add(argOptions);
        }
        if (argOptionsResolved != null) {
            declared.add(argOptionsResolved);
        }
        declaredArguments = new JobArguments(included.toArray(ASOSArguments[]::new));
        declaredArguments.setDynamicArguments(declared);
    }

    private void tryApplyArgumentType(JobArgument<?> arg, Value typeValue, Value defaultValue) {
        if (typeValue == null || typeValue.isNull()) {
            tryApplyArgumentDefaultValue(arg, null, defaultValue);
            return;
        }

        Type type = null;
        if (typeValue.isHostObject()) {
            Object hostObj = typeValue.asHostObject();
            if (hostObj instanceof Class<?>) { // java types
                type = (Class<?>) hostObj;
            }
        } else if (typeValue.isMetaObject()) {
            // python, javascript types
            type = tryGetJavaTypeFromMetaObject(typeValue);
        }

        tryApplyArgumentDefaultValue(arg, type, defaultValue);

        arg.setClazzType(type == null ? Object.class : type);
    }

    private Type tryGetJavaTypeFromMetaObject(Value typeValue) {
        Type type = null;
        try {
            String typeName = typeValue.getMetaQualifiedName().toLowerCase();
            switch (typeName) {
            case "str":
            case "string": // js
                type = String.class;
                break;
            case "bool":
            case "boolean": // js
                type = Boolean.class;
                break;
            case "int":
            case "long":
            case "number": // js
                type = Long.class;
                break;
            case "float":
            case "double":
                type = Double.class;
                break;
            case "list":
            case "array": // js
                type = List.class;
                break;
            case "dict":
            case "map": // js
            case "object": // js
                type = Map.class;
                break;
            case "set":
                type = Set.class;
                break;
            }
        } catch (Exception e) {

        }
        return type;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object tryApplyArgumentDefaultValue(JobArgument arg, Type argumentJavaType, Value value) {
        if (value == null || value.isNull()) {
            arg.setDefaultValue(null);
            return null;
        }

        Object defaultValue = null;
        if (value.isString()) {
            defaultValue = value.asString();
        } else if (value.isBoolean()) {
            defaultValue = value.asBoolean();
        } else if (value.isNumber()) {
            // if (value.fitsInInt()) {
            // convert to Long instead to Integer because - e.g. if it is a defaultValue - so the greatest value is unknown
            if (value.fitsInLong()) {
                defaultValue = Long.valueOf(value.asLong());
            } else if (value.fitsInDouble()) {
                defaultValue = Double.valueOf(value.asDouble()); // see com.sos.js7.job.JobArgument supported types
            } else {
                defaultValue = value.toString();
            }
        } else if (value.isHostObject()) {
            defaultValue = value.asHostObject();
        } else if (value.hasArrayElements()) {// list
            if (argumentJavaType == null) {
                arg.setClazzType(List.class);
            }
            defaultValue = null;
        } else if (value.hasMembers()) {
            defaultValue = tryApplyArgumentDefaultValueFromMembers(arg, argumentJavaType, value); // language specific - it can be an object (e.g. python Path
                                                                                                  // or
            // Set/Map)
        } else {
            defaultValue = (Object) value.toString();
        }
        arg.setDefaultValue(defaultValue);
        return defaultValue;
    }

    private static String getJobDefinition(ScriptJob job) throws Exception {
        String script = JOB_DEFINITIONS.get(job.language);
        if (script == null) {
            throw new ScriptJobException(job, "job definition not loaded");
        }
        return script;
    }

    /** Returns the number of lines in the predefined job definition script.<br/>
     * This count is used solely during exception handling to translate Polyglot line numbers into user-visible positions.
     * <p>
     * Although caching is possible, recomputing the value on demand is faster and simpler, given that the script is short (~40 lines). */
    private static int getJobDefinitionLinesCount(ScriptJob job) {
        try {
            String script = getJobDefinition(job);
            return (int) script.lines().count();
        } catch (Exception e) {
            return 0;
        }
    }

    /** Creates a new Polyglot Context for executing user scripts.
     * <p>
     * Important permissions:
     * <ul>
     * <li>{@link Builder#allowHostAccess(boolean)}:
     * <ul>
     * <li>- {@code true}
     * <ul>
     * <li>set to {@code true} to allow the script to interact with Java objects explicitly passed into the script bindings.<br/>
     * &nbsp;This enables access to Java methods and fields of these objects:
     * <ul>
     * <li>js7Environment - read(public methods) only</li>
     * <li>js7Step - read and write(outcome, setExitCode)</li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * </li>
     * </ul>
     * <p>
     * Other permissions, including IO, threads, and native access, remain configurable. */
    private Builder createBuilder(ScriptJobOptionsReader optionsReader) throws Exception {
        Builder builder = Context.newBuilder(language);

        // - Necessary -----------------------------------------------------------------------------------------------------
        // Stream redirection:
        // Fundamentally it works, but introduces too much overhead on both sides (Java and e.g., Python).
        // Additionally, it produces STDOUT/STDERR output without respecting the log levels of step.getLogger().
        // builder.out(new ScriptJobStdAdapter(step.getOut()));
        // builder.err(new ScriptJobStdAdapter(step.getErr()));

        // Configures which public constructors, methods or fields of public classes are accessible by guest applications.
        // By default if allowAllAccess(boolean) is false the HostAccess.EXPLICIT policy will be used, otherwise HostAccess.ALL.
        builder.allowHostAccess(HostAccess.ALL);

        // - Not configurable /irrelevant -----------------------------------------------------------------------------------------------------
        // Allows this context to spawn inner contexts that may change option values set for the outer context.
        // var innerContext = Polyglot.newContext({languages: ["python"], allowIO: true});
        // innerContext.eval("python", "open('/tmp/test.txt', 'w')");
        // irrelevant ? - seems to be a JVM/Host-Context-Flag - because only a single Host-Context is created
        builder.allowInnerContextOptions(false);

        // Enables or disables sharing of any value between contexts.
        // Value sharing is enabled by default and is not affected by allowAllAccess(boolean).
        // false|true is irrelevant here, because only a single context is created and immediately closed, so no values are ever shared between contexts.
        builder.allowValueSharing(false);

        // - Configurable -----------------------------------------------------------------------------------------------------
        configureFromOptions(optionsReader, builder);

        return builder;
    }

    private void configureFromOptions(ScriptJobOptionsReader optionsReader, Builder builder) {
        // Sets a filter that specifies the Java host classes that can be looked up by the guest application.
        // If set to null then no class lookup is allowed and relevant language builtins are not available (e.g. Java.type in JavaScript).
        // List<String> allowedPatterns = List.of("^com\\.sos.*", "^org\\.example.*");
        // builder.allowHostClassLookup(className -> {
        // for (String p : allowedPatterns) {
        // if (java.util.regex.Pattern.matches(p, className)) {
        // return true;
        // }
        // }
        // return false;
        // });
        builder.allowHostClassLookup(optionsReader.getPolyglotOptionallowHostClassLookup("allowHostClassLookup"));

        // irrelevant ...
        // If host class loading is enabled, then the guest language is allowed to load new host classes via jar or class files.
        // If all access is set to true, then the host class loading is enabled if it is not disallowed explicitly.
        // For host class loading to be useful, IO operations host class lookup, and the host access policy needs to be configured as well.
        // How to test? true|false ignored ...
        builder.allowHostClassLoading(optionsReader.getBooleanPolyglotOption("allowHostClassLoading"));

        // Allows guest languages to access the native interface.
        // TODO allowNativeAccess = true?
        // false Python - loading C-extensions like NumPy will fail, but it not works also with true
        // false JavaScript - false: not really relevant
        // false Java - SOSShell.executeCommand works
        builder = builder.allowNativeAccess(optionsReader.getBooleanPolyglotOption("allowNativeAccess"));

        // If true, allows guest language to execute external processes. Default is false.
        // If all access is set to true, then process creation is enabled if not denied explicitly.
        builder.allowCreateProcess(optionsReader.getBooleanPolyglotOption("allowCreateProcess"));

        // If true, allows guest languages to create new threads. Default is false.
        // If all access is set to true, then the creation of threads is enabled if not allowed explicitly.
        // Threads created by guest languages are closed, when the context is closed.
        builder.allowCreateThread(optionsReader.getBooleanPolyglotOption("allowCreateThread"));

        // Allow environment access using the provided policy.
        // If all access is true then the default environment access policy is EnvironmentAccess.INHERIT, otherwise EnvironmentAccess.NONE.
        // The provided access policy must not be null.
        // EnvironmentAccess.NONE: Python - no exceptions, returns None for a specific ENV variable
        builder.allowEnvironmentAccess(optionsReader.getPolyglotOptionEnvironmentAccess("allowEnvironmentAccess"));

        // Allow experimental options to be used for language options.
        // Do not use experimental options in production environments.
        // If set to false (the default), then passing an experimental option results in an IllegalArgumentException when the context is built.
        // TODO unclear - how to test
        builder.allowExperimentalOptions(optionsReader.getBooleanPolyglotOption("allowExperimentalOptions"));

        IOAccess.Builder ioBuilder = IOAccess.newBuilder();
        // If true, it allows the GUEST language (not Java) unrestricted access to files on the host system.
        // false Python - "open/or subprocess.run" will fail with PermissionError: [Errno 1] Operation not permitted: 'path.txt'
        // -- with Path("path.txt").open("r") as f:
        // -- print(f.read())
        // false Java - io Files.readAllLines(path, StandardCharsets.UTF_8) will work because of allowHostAccess/allowHostClassLookup
        ioBuilder.allowHostFileAccess(optionsReader.getBooleanPolyglotOption("IOAccess.allowHostFileAccess"));

        // If true, it allows the guest language unrestricted access to host system sockets.
        // false Python - PolyglotException(a RuntimeException): socket was excluded
        // false Java - works
        ioBuilder.allowHostSocketAccess(optionsReader.getBooleanPolyglotOption("IOAccess.allowHostSocketAccess"));
        // ioBuilder.fileSystem(null);
        builder.allowIO(ioBuilder.build());

        // Allow polyglot access using the provided policy.
        // If all access is true then the default polyglot access policy is PolyglotAccess.ALL, otherwise PolyglotAccess.NONE.
        // The provided access policy must not be null.
        // e.g., import polyglot
        // js = polyglot.eval(language="js", string='1+2')
        // PolyglotAccess.NONE Python - [Exception] polyglot access is not allowed
        builder.allowPolyglotAccess(optionsReader.getPolyglotOptionPolyglotAccess("allowPolyglotAccess"));

        if (optionsReader.getResult().hasLanguageOptions()) {
            builder.options(optionsReader.getResult().getLanguageOptions());
        }
    }

    private static String loadResource(ScriptJob job, String resourceName) {
        try (InputStream is = ScriptJob.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalArgumentException("[" + resourceName + "]resource not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            throw new ScriptJobRunTimeException(job, "[" + resourceName + "][loading resource] " + e.toString(), e);
        }
    }

}
