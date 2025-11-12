package com.sos.js7.scriptengine.jobs;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;

import com.sos.js7.job.JobArgument;

public class PythonJob extends AScriptJob {

    private static final String LANGUAGE = "python";
    private static final String JOB_DEFINITION_RESOURCE_NAME = PythonJob.class.getSimpleName() + ".jobdef";

    public PythonJob(JobContext jobContext) {
        super(jobContext, LANGUAGE, JOB_DEFINITION_RESOURCE_NAME);
    }

    @Override
    public Object tryApplyArgumentDefaultValueFromMembers(JobArgument<?> arg, Value value) {
        // Python objects
        if (value.getMemberKeys().contains("__fspath__")) {
            return Paths.get(value.invokeMember("__fspath__").asString());
        }

        Object o = null;
        // Python Set
        if (value.hasMember("add") && value.hasMember("discard")) {
            arg.setClazzType(Set.class);
        }
        // else if (value.hasMember("keys") && value.hasMember("values") && value.hasMember("items")) {
        else {
            arg.setClazzType(Map.class);
        }
        return o;
    }

}
