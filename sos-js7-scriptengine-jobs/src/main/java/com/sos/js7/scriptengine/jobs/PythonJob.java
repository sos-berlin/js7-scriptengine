package com.sos.js7.scriptengine.jobs;

import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;

import com.sos.js7.job.JobArgument;

/** Polyglot API 25.0.1<br/>
 * - Context options:<br/>
 * -- See https://www.graalvm.org/python/docs/#python-context-options<br/>
 * -- JS7: js7_options.graalvm.python<br/>
 * - Known bugs:<br/>
 * -- IsolateNativeModules=true, see https://github.com/oracle/graalpython/issues/563 */
public class PythonJob extends ScriptJob {

    private static final String LANGUAGE = "python";
    private static final String JOB_DEFINITION_RESOURCE_NAME = PythonJob.class.getSimpleName() + ".jobdef";

    public PythonJob(JobContext jobContext) {
        super(jobContext, LANGUAGE, JOB_DEFINITION_RESOURCE_NAME);
    }

    @Override
    public Object tryApplyArgumentDefaultValueFromMembers(JobArgument<?> arg, Type argumentJavaType, Value defaultValue) {
        // Python objects
        if (defaultValue.getMemberKeys().contains("__fspath__")) {
            return Paths.get(defaultValue.invokeMember("__fspath__").asString());
        }

        Object o = null;
        if (argumentJavaType == null) {
            // Python Set
            if (defaultValue.hasMember("add") && defaultValue.hasMember("discard")) {
                arg.setClazzType(Set.class);
            }
            // else if (defaultValue.hasMember("keys") && defaultValue.hasMember("values") && defaultValue.hasMember("items")) {
            else {
                arg.setClazzType(Map.class);
            }
        }
        return o;
    }

    @Override
    protected Boolean isMethodOverridden(Value job, String methodName) {
        try {
            Value instanceMethod = job.getMember(methodName);
            Value baseMethod = job.getMember("__class__").getMember("__bases__").getArrayElement(0).getMember(methodName);
            return instanceMethod != null && !instanceMethod.equals(baseMethod);
        } catch (Exception e) {
            return null;
        }
    }

}
