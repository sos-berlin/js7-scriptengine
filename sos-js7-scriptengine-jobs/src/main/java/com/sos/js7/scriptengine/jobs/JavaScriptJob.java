package com.sos.js7.scriptengine.jobs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Context.Builder;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import com.sos.commons.credentialstore.CredentialStoreArguments;
import com.sos.commons.util.SOSShell;
import com.sos.commons.util.SOSString;
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
import com.sos.js7.scriptengine.json.GraalJSScriptEngineOptions;

public class JavaScriptJob extends Job<JobArguments> {

    public static final String JS7_GRAALVM_JS_OPTION = "js7_options.graalvm.js";
    private static final String SCRIPT_ENGINE_NAME = "Graal.js";
    private static final String GRAALVM_SCRIPT_ENGINE_NAME = "js";

    private static final String BASIC_SCRIPT_RESOURCE = JavaScriptJob.class.getSimpleName() + ".js";
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

    private static volatile String BASIC_SCRIPT;

    private String script;
    private JobArguments declaredArguments;

    public JavaScriptJob(JobContext jobContext) {
        super(jobContext);
        if (jobContext != null) {
            script = jobContext.asScala().executable().script();
        }
    }

    @Override
    public void onStart() throws Exception {
        setBasicScript();

        ScriptEngine engine = createScriptEngine();
        engine.eval(BASIC_SCRIPT + "\n" + script);
        Invocable invocable = (Invocable) engine;

        Object job = invocable.invokeFunction(FUNCTION_NAME_GET_JOB, getJobEnvironment());
        declaredArguments = getDeclaredArguments(invocable.invokeMethod(job, JOB_METHOD_GET_DECLARED_ARGUMENTS));
    }

    @Override
    public JobArguments onCreateJobArguments(List<JobArgumentException> exceptions, final OrderProcessStep<JobArguments> step) {
        return declaredArguments;
    }

    @Override
    public void processOrder(OrderProcessStep<JobArguments> step) throws Exception {
        ScriptEngine engine = createGraalJSScriptEngine(step);
        engine.eval(BASIC_SCRIPT + "\n" + script);
        Invocable invocable = (Invocable) engine;

        Object job = invocable.invokeFunction(FUNCTION_NAME_GET_JOB, getJobEnvironment());
        invocable.invokeMethod(job, JOB_METHOD_PROCESS_ORDER, step);
    }

    /** com.sos.js7.job.Job - [cancel/kill][job name=javascript_job][onOrderProcessCancel]<br/>
     * java.lang.IllegalStateException: <br/>
     * Multi threaded access requested by thread Thread[#46,JS7 blocking job 46,5,main] but is not allowed for language(s) js.<br/>
     * at com.oracle.truffle.polyglot.PolyglotEngineException.illegalState(PolyglotEngineException.java:135) ~[org.graalvm.truffle:?]<br>
     * ... */
    // @Override
    // public void onOrderProcessCancel(OrderProcessStep<JobArguments> step) throws Exception {

    // }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JobArguments getDeclaredArguments(Object args) throws Exception {
        if (args == null) {
            return new JobArguments();
        }

        Map<String, Object> m = (Map) args;
        List<JobArgument<?>> l = new ArrayList<>();
        List<ASOSArguments> included = new ArrayList<>();
        m.entrySet().stream().forEach(e -> {
            if (e.getKey().equals("includedArguments") && e.getValue() instanceof List) {
                List<String> vl = (List<String>) e.getValue();
                for (String n : vl) {
                    try {
                        included.add((ASOSArguments) Class.forName(INCLUDABLE_ARGUMENTS.get(n)).getDeclaredConstructor().newInstance());
                    } catch (Throwable e1) {
                    }
                }
            } else if (e.getValue() instanceof Map) {
                Map<String, Object> v = (Map) e.getValue();
                if (v.containsKey("name") && v.containsKey("required") && v.containsKey("defaultValue") && v.containsKey("displayMode")) {
                    Object name = v.get("name");
                    Object required = v.get("required");
                    Object defaultValue = v.get("defaultValue");
                    Object displayMode = v.get("displayMode");

                    JobArgument ja = new JobArgument<>(name.toString(), Boolean.parseBoolean(required.toString()));
                    if (defaultValue != null) {
                        ja.setDefaultValue(defaultValue);
                    }
                    if (displayMode != null) {
                        ja.setDisplayMode(DisplayMode.valueOf(displayMode.toString().toUpperCase()));
                    }
                    ja.setIsDirty(false);
                    l.add(ja);
                }
            }
        });

        JobArguments jas = null;
        // TODO to Array etc ..
        switch (included.size()) {
        case 1:
            jas = new JobArguments(included.get(0));
            break;
        case 2:
            jas = new JobArguments(included.get(0), included.get(1));
            break;
        default:
            jas = new JobArguments();
            break;
        }

        jas.setDynamicArgumentFields(l);
        return jas;
    }

    private ScriptEngine createScriptEngine() throws Exception {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(SCRIPT_ENGINE_NAME);
        if (engine == null) {
            throw new Exception("ScriptEngine " + SCRIPT_ENGINE_NAME + " not found");
        }
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowHostClassLookup", true);
        bindings.put("polyglot.js.allowIO", true);
        return engine;
    }

    private ScriptEngine createGraalJSScriptEngine(OrderProcessStep<JobArguments> step) throws Exception {
        // .allowIO(IOAccess.ALL)
        Builder builder = Context.newBuilder(GRAALVM_SCRIPT_ENGINE_NAME).allowIO(true).allowHostAccess(HostAccess.ALL).allowHostClassLookup(
                className -> true).allowExperimentalOptions(true);

        Map<String, String> options = getGraalJSScriptEngineOptions(step);
        if (options != null) {
            builder.options(options);
        }
        return GraalJSScriptEngine.create(null, builder);
    }

    private synchronized void setBasicScript() throws Exception {
        if (BASIC_SCRIPT == null) {
            BASIC_SCRIPT = inputStreamToString(this.getClass().getClassLoader().getResourceAsStream(BASIC_SCRIPT_RESOURCE));
        }
    }

    // TODO read one time - requirement: agent creates the files 1 time instead of per step
    private Map<String, String> getGraalJSScriptEngineOptions(OrderProcessStep<JobArguments> step) throws Exception {
        String p = step.getAllArgumentsAsNameValueMap().entrySet().stream().filter(e -> e.getKey().equalsIgnoreCase(JS7_GRAALVM_JS_OPTION)).map(e -> e
                .getValue().toString()).findFirst().orElse(null);

        String method = "getGraalJSScriptEngineOptions";
        Map<String, String> options = null;
        if (SOSString.isEmpty(p)) {
            String jh = SOSShell.getJavaHome();
            if (SOSString.isEmpty(jh)) {
                if (step.getLogger().isDebugEnabled()) {
                    step.getLogger().debug(String.format("[%s][java.home]is empty", method));
                }
            } else {
                Path np = null;
                try {
                    np = Paths.get(jh).resolve("bin").resolve("node_modules");
                    if (np.toFile().exists()) {
                        options = new HashMap<>();
                        options.put("js.commonjs-require", "true");
                        options.put("js.commonjs-require-cwd", np.toString());
                        if (step.getLogger().isDebugEnabled()) {
                            step.getLogger().debug(String.format("[%s]options=%s", method, options));
                        }
                    } else {
                        if (step.getLogger().isDebugEnabled()) {
                            step.getLogger().debug(String.format("[%s][java.home=%s][%s]node_modules not found", method, jh, np));
                        }
                    }
                } catch (Throwable e) {
                    step.getLogger().warn(String.format("[%s][java.home=%s][%s]%s", method, jh, np, e.toString()), e);
                }
            }
        } else {
            File f = new File(p);
            if (f.exists()) {
                GraalJSScriptEngineOptions o = JobHelper.OBJECT_MAPPER.readValue(f, GraalJSScriptEngineOptions.class);
                if (o != null && o.getOptions() != null) {
                    options = o.getOptions();
                }
                if (step.getLogger().isDebugEnabled()) {
                    step.getLogger().debug(String.format("[%s][%s]options=%s", method, p, options));
                }
            } else {
                step.getLogger().warn(String.format("[%s][%s=%s]file not found", method, JS7_GRAALVM_JS_OPTION, p));
            }
        }
        return options;
    }

    private String inputStreamToString(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

}
