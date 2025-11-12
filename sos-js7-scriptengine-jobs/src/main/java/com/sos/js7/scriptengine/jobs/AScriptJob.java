package com.sos.js7.scriptengine.jobs;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;

import com.sos.commons.credentialstore.CredentialStoreArguments;
import com.sos.commons.util.arguments.base.ASOSArguments;
import com.sos.commons.util.arguments.base.SOSArgument.DisplayMode;
import com.sos.commons.util.keystore.KeyStoreArguments;
import com.sos.commons.util.proxy.ProxyConfigArguments;
import com.sos.commons.vfs.ssh.commons.SSHProviderArguments;
import com.sos.js7.job.Job;
import com.sos.js7.job.JobArgument;
import com.sos.js7.job.JobArguments;
import com.sos.js7.job.JobHelper;
import com.sos.js7.job.OrderProcessStep;
import com.sos.js7.job.exception.JobArgumentException;
import com.sos.js7.scriptengine.jobs.exceptions.ScriptJobException;
import com.sos.js7.scriptengine.jobs.exceptions.ScriptJobRunTimeException;
import com.sos.js7.scriptengine.json.ScriptJobOptions;

public abstract class AScriptJob extends Job<JobArguments> {

    private static final Map<String, String> JOB_DEFINITIONS = new ConcurrentHashMap<>();

    /** JOB_OPTION_NAME_PREFIX + language: js7_options.graalvm.js|js7_options.graalvm.python */
    private static final String JOB_OPTION_NAME_PREFIX = "js7_options.graalvm.";

    private static final String POLYGLOT_WARN_PROPERTY_NAME = "polyglot.engine.WarnInterpreterOnly";

    private static final String FUNCTION_NAME_GET_JOB = "getJS7Job";
    private static final String JOB_METHOD_GET_DECLARED_ARGUMENTS = "getDeclaredArguments";
    private static final String JOB_METHOD_PROCESS_ORDER = "processOrder";

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
    private final String jobDefinitionResourceName;
    private final String jobOptionName;

    private String script;
    private JobArguments declaredArguments;

    static {
        String warnProperty = System.getProperty(POLYGLOT_WARN_PROPERTY_NAME);
        if (warnProperty == null) {
            System.setProperty(POLYGLOT_WARN_PROPERTY_NAME, "false");
        }
    }

    public AScriptJob(JobContext jobContext, String language, String jobDefinitionResourceName) {
        super(jobContext);

        if (jobContext != null) {
            script = jobContext.asScala().executable().script();
        }
        this.language = language;
        this.jobDefinitionResourceName = jobDefinitionResourceName;
        this.jobOptionName = JOB_OPTION_NAME_PREFIX + language;
    }

    protected abstract Object tryApplyArgumentDefaultValueFromMembers(JobArgument<?> arg, Value value);

    @Override
    public void onStart() throws Exception {
        JOB_DEFINITIONS.computeIfAbsent(language, lang -> loadResource(this, jobDefinitionResourceName));

        try (Context context = Context.newBuilder(language).allowAllAccess(true).build()) {
            try {
                context.eval(language, getJobDefinition(this) + "\n" + script);

                Value getJobFunc = context.getBindings(language).getMember(FUNCTION_NAME_GET_JOB);
                Value job = getJobFunc.execute(getJobEnvironment());

                setDeclaredArguments(job.invokeMember(JOB_METHOD_GET_DECLARED_ARGUMENTS));
            } catch (PolyglotException e) {
                throw new ScriptJobException(this, getJobDefinitionLinesCount(this), e);
            }
        }
    }

    @Override
    public JobArguments onCreateJobArguments(List<JobArgumentException> exceptions, final OrderProcessStep<JobArguments> step) {
        return declaredArguments;
    }

    @Override
    public void processOrder(OrderProcessStep<JobArguments> step) throws Exception {
        try (Context context = createBuilder(step).build()) {
            try {
                context.eval(language, getJobDefinition(this) + "\n" + script);

                Value getJobFunc = context.getBindings(language).getMember(FUNCTION_NAME_GET_JOB);
                Value job = getJobFunc.execute(getJobEnvironment());
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
    // @Override
    // public void onOrderProcessCancel(OrderProcessStep<JobArguments> step) throws Exception {

    // }

    private Builder createBuilder(OrderProcessStep<JobArguments> step) throws Exception {
        // .allowIO(IOAccess.ALL)
        Builder builder = Context.newBuilder(language).allowAllAccess(true).allowHostAccess(HostAccess.ALL).allowHostClassLookup(className -> true)
                .allowExperimentalOptions(true);

        Map<String, String> options = getJobOptions(step);
        if (options != null) {
            builder.options(options);
        }
        return builder;
    }

    private void setDeclaredArguments(Value declaredArgs) throws Exception {
        List<JobArgument<?>> declared = new ArrayList<>();
        List<ASOSArguments> included = new ArrayList<>();
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
                    tryApplyArgumentDefaultValue(arg, defaultValue);
                    arg.setIsDirty(false);
                    declared.add(arg);
                }
            }
        }
        declaredArguments = new JobArguments(included.toArray(ASOSArguments[]::new));
        declaredArguments.setDynamicArgumentFields(declared);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object tryApplyArgumentDefaultValue(JobArgument arg, Value value) {
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
                defaultValue = new BigDecimal(value.asDouble()); // see com.sos.js7.job.JobArgument supported types
            } else {
                defaultValue = value.toString();
            }
        } else if (value.isHostObject()) {
            defaultValue = value.asHostObject();
        } else if (value.hasArrayElements()) {// list
            // List<Object> list = new ArrayList<>();
            // for (long i = 0; i < value.getArraySize(); i++) {
            // Object o = tryApplyArgumentDefaultValue(arg, value.getArrayElement(i));
            // if (o != null) {
            // list.add(o);
            // }
            // }
            arg.setClazzType(List.class);
            defaultValue = null;
        } else if (value.hasMembers()) {
            defaultValue = tryApplyArgumentDefaultValueFromMembers(arg, value); // language specific - it can be an object (e.g. python Path or Set/Map)
        } else {
            defaultValue = (Object) value.toString();
        }
        arg.setDefaultValue(defaultValue);
        return defaultValue;
    }

    private Map<String, String> getJobOptions(OrderProcessStep<JobArguments> step) throws Exception {
        String value = step.getAllArgumentsAsNameValueMap().entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(jobOptionName)).map(e -> e
                .getValue().toString()).findFirst().orElse(null);

        Map<String, String> options = null;
        if (value != null) {
            String method = "getJobOptions";
            if (value.startsWith("{")) {
                ScriptJobOptions o = JobHelper.OBJECT_MAPPER.readValue(value, ScriptJobOptions.class);
                if (o != null && o.getOptions() != null) {
                    options = o.getOptions();
                }

                if (step.getLogger().isDebugEnabled()) {
                    step.getLogger().debug(String.format("[%s][%s]options=%s", method, value, options));
                }
            } else {
                File f = new File(value);
                if (f.exists()) {
                    ScriptJobOptions o = JobHelper.OBJECT_MAPPER.readValue(f, ScriptJobOptions.class);
                    if (o != null && o.getOptions() != null) {
                        options = o.getOptions();
                    }
                    if (step.getLogger().isDebugEnabled()) {
                        step.getLogger().debug(String.format("[%s][%s]options=%s", method, value, options));
                    }
                } else {
                    if (step.getLogger().isDebugEnabled()) {
                        step.getLogger().debug(String.format("[%s][%s=%s]file not found", method, jobOptionName, value));
                    }
                }
            }
        }
        return options;
    }

    private static String getJobDefinition(AScriptJob job) throws Exception {
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
    private static int getJobDefinitionLinesCount(AScriptJob job) {
        try {
            String script = getJobDefinition(job);
            return (int) script.lines().count();
        } catch (Exception e) {
            return 0;
        }
    }

    private static String loadResource(AScriptJob job, String resourceName) {
        try (InputStream is = AScriptJob.class.getClassLoader().getResourceAsStream(resourceName)) {
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
