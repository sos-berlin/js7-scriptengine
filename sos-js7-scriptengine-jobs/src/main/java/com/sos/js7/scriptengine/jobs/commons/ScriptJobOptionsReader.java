package com.sos.js7.scriptengine.jobs.commons;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.PolyglotAccess;

import com.sos.commons.util.SOSCollection;
import com.sos.commons.util.SOSString;
import com.sos.js7.job.JobArgument;
import com.sos.js7.job.JobArguments;
import com.sos.js7.job.JobHelper;
import com.sos.js7.job.OrderProcessStep;
import com.sos.js7.scriptengine.jobs.JavaScriptJob;
import com.sos.js7.scriptengine.jobs.PythonJob;
import com.sos.js7.scriptengine.jobs.ScriptJob;

/** GraalVM Context options support:
 * <ul>
 * <li>JavaScriptJob: see {@link JavaScriptJob}</li>
 * <li>PythonJob: see {@link PythonJob}</li>
 * </ul>
 */
public class ScriptJobOptionsReader {

    /** JOB_ARGUMENT_OPTIONS_NAME_PREFIX + language<br/>
     * Examples: js7_options.graalvm.js | js7_options.graalvm.python<br/>
     * Usage: see {@link ScriptJob#createBuilder(ScriptJobOptionsReader)} */
    private static final String JOB_ARGUMENT_OPTIONS_NAME_PREFIX = "js7_options.graalvm.";

    /** Examples: js7.polyglot.allowCreateProcess<br/>
     * Usage:<br/>
     * see {@link ScriptJob#createBuilder(ScriptJobOptionsReader)},<br/>
     * see {@link ScriptJob#configureFromOptions(ScriptJobOptionsReader, org.graalvm.polyglot.Context.Builder)} */
    private static final String POLYGLOT_OPTION_NAME_PREFIX = "js7.polyglot.";

    private final String language;

    private ScriptJobOptionsResult result;

    public ScriptJobOptionsReader(String language) {
        this.language = language;
    }

    public void readFromPreAssignedArgumentValue(OrderProcessStep<JobArguments> step) throws Exception {
        Object v = step.getPreAssignedArgumentValue(getArgumentOptionsName());
        readArgumentOptions(step, v == null ? null : v.toString());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void read(OrderProcessStep<JobArguments> step) throws Exception {
        JobArgument<?> argOptionsResolved = step.getAllArguments().get(getArgumentOptionsResolvedName());
        if (argOptionsResolved != null && !argOptionsResolved.isEmpty()) {
            if (step.getLogger().isTraceEnabled()) {
                step.getLogger().trace("[read][" + argOptionsResolved.getName() + "]" + argOptionsResolved.getValue());
            }
            result = new ScriptJobOptionsResult((Map) argOptionsResolved.getValue());
            return;
        }

        JobArgument<?> argOptions = step.getAllArguments().get(getArgumentOptionsName());
        if (argOptions != null && !argOptions.isEmpty()) {
            readArgumentOptions(step, (String) argOptions.getValue());
        }
    }

    public JobArgument<String> createArgumentOptions() {
        return new JobArgument<>(getArgumentOptionsName(), false);
    }

    public JobArgument<Map<String, String>> createArgumentOptionsResolved() {
        if (!getResult().hasOptions) {
            return null;
        }

        JobArgument<Map<String, String>> arg = new JobArgument<>(getArgumentOptionsResolvedName(), false, getResult().options);
        arg.setIsDirty(true);
        return arg;
    }

    public boolean getBooleanPolyglotOption(String optionName) {
        boolean value = true;
        if (!getResult().hasPolyglotOptions) {
            return value;
        }
        String val = getResult().polyglotOptions.get(POLYGLOT_OPTION_NAME_PREFIX + optionName);
        if (SOSString.isBoolean(val)) {
            value = Boolean.parseBoolean(val);
        }
        return value;
    }

    public Predicate<String> getPolyglotOptionallowHostClassLookup(String optionName) {
        Predicate<String> value = className -> true;
        if (!getResult().hasPolyglotOptions) {
            return value;
        }
        String val = getResult().polyglotOptions.get(POLYGLOT_OPTION_NAME_PREFIX + optionName);
        if (val == null) {
            return value;
        }
        if (SOSString.isBoolean(val)) {
            if (!Boolean.valueOf(val)) {
                value = className -> false;
            }
        } else {
            final Pattern pattern;
            final boolean isNegative;

            if (val.startsWith("!")) {
                pattern = Pattern.compile(val.substring(1));
                isNegative = true;
            } else {
                pattern = Pattern.compile(val);
                isNegative = false;
            }

            return cls -> {
                boolean matches = pattern.matcher(cls).matches();
                return isNegative ? !matches : matches;
            };
        }
        return value;
    }

    public EnvironmentAccess getPolyglotOptionEnvironmentAccess(String optionName) {
        EnvironmentAccess value = EnvironmentAccess.INHERIT;
        if (!getResult().hasPolyglotOptions) {
            return value;
        }
        String val = getResult().polyglotOptions.get(POLYGLOT_OPTION_NAME_PREFIX + optionName);
        if (val == null) {
            return value;
        }

        if (SOSString.isBoolean(val)) {
            if (!Boolean.valueOf(val)) {
                value = EnvironmentAccess.NONE;
            }
        } else {
            switch (val.toUpperCase()) {
            case "NONE":
                value = EnvironmentAccess.NONE;
                break;
            }
        }
        return value;
    }

    public PolyglotAccess getPolyglotOptionPolyglotAccess(String optionName) {
        PolyglotAccess value = PolyglotAccess.ALL;
        if (!getResult().hasPolyglotOptions) {
            return value;
        }
        String val = getResult().polyglotOptions.get(POLYGLOT_OPTION_NAME_PREFIX + optionName);
        if (val == null) {
            return value;
        }

        if (SOSString.isBoolean(val)) {
            if (!Boolean.valueOf(val)) {
                value = PolyglotAccess.NONE;
            }
        } else {
            switch (val.toUpperCase()) {
            case "NONE":
                value = PolyglotAccess.NONE;
                break;
            }
        }
        return value;
    }

    private void readArgumentOptions(OrderProcessStep<JobArguments> step, String argumentOptionsValue) throws Exception {
        if (argumentOptionsValue == null) {
            result = new ScriptJobOptionsResult(null);
            return;
        }

        Map<String, String> options = null;
        if (argumentOptionsValue.startsWith("{")) {
            ScriptJobOptionsJson o = JobHelper.OBJECT_MAPPER.readValue(argumentOptionsValue, ScriptJobOptionsJson.class);
            if (o != null && o.getOptions() != null) {
                options = o.getOptions();
            }
        } else {
            File f = new File(argumentOptionsValue);
            if (f.exists()) {
                ScriptJobOptionsJson o = JobHelper.OBJECT_MAPPER.readValue(f, ScriptJobOptionsJson.class);
                if (o != null && o.getOptions() != null) {
                    options = o.getOptions();
                }
            } else {
                if (step.getLogger().isDebugEnabled()) {
                    step.getLogger().debug(String.format("[read][%s=%s]file not found", getArgumentOptionsName(), argumentOptionsValue));
                }
            }
        }
        result = new ScriptJobOptionsResult(options);
    }

    public ScriptJobOptionsResult getResult() {
        if (result == null) {
            result = new ScriptJobOptionsResult(null);
        }
        return result;
    }

    private String getArgumentOptionsName() {
        return JOB_ARGUMENT_OPTIONS_NAME_PREFIX + language;
    }

    private String getArgumentOptionsResolvedName() {
        return getArgumentOptionsName() + ".resolved";
    }

    public class ScriptJobOptionsResult {

        private final Map<String, String> options;
        private final boolean hasOptions;

        private Map<String, String> polyglotOptions;
        private Map<String, String> languageOptions;

        private boolean hasPolyglotOptions;

        private ScriptJobOptionsResult(Map<String, String> options) {
            this.options = options;
            this.hasOptions = !SOSCollection.isEmpty(this.options);
            if (hasOptions) {
                polyglotOptions = new LinkedHashMap<>();
                languageOptions = new LinkedHashMap<>();
                for (Map.Entry<String, String> e : this.options.entrySet()) {
                    String name = e.getKey();
                    if (name.startsWith(POLYGLOT_OPTION_NAME_PREFIX)) {
                        polyglotOptions.put(name, e.getValue());
                    } else {
                        languageOptions.put(name, e.getValue());
                    }
                }
                hasPolyglotOptions = !SOSCollection.isEmpty(this.polyglotOptions);
            }
        }

        public boolean hasLanguageOptions() {
            return !SOSCollection.isEmpty(languageOptions);
        }

        public Map<String, String> getLanguageOptions() {
            return languageOptions;
        }
    }

}
