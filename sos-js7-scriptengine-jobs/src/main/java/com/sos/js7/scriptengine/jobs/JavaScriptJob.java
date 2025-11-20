package com.sos.js7.scriptengine.jobs;

import java.util.Map;

import org.graalvm.polyglot.Value;

import com.sos.js7.job.JobArgument;

/** Polyglot API 25.0.1<br/>
 * - Context options:<br/>
 * -- See https://www.graalvm.org/latest/reference-manual/js/Options/<br/>
 * -- JS7: js7_options.graalvm.js<br/>
 */
public class JavaScriptJob extends ScriptJob {

    private static final String LANGUAGE = "js";
    private static final String JOB_DEFINITION_RESOURCE_NAME = JavaScriptJob.class.getSimpleName() + ".jobdef";

    public JavaScriptJob(JobContext jobContext) {
        super(jobContext, LANGUAGE, JOB_DEFINITION_RESOURCE_NAME);
    }

    @Override
    public Object tryApplyArgumentDefaultValueFromMembers(JobArgument<?> arg, Value value) {
        arg.setClazzType(Map.class);
        return null;
    }

    @Override
    protected Boolean isMethodOverridden(Value job, String methodName) {
        try {
            Value instanceMethod = job.getMember(methodName);
            Value baseMethod = job.getMember("__proto__").getMember(methodName);
            return instanceMethod != null && !instanceMethod.equals(baseMethod);
        } catch (Exception e) {
            return null;
        }
    }

}
