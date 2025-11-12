package com.sos.js7.scriptengine.jobs;

import java.util.Map;

import org.graalvm.polyglot.Value;

import com.sos.js7.job.JobArgument;

public class JavaScriptJob extends AScriptJob {

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

}
