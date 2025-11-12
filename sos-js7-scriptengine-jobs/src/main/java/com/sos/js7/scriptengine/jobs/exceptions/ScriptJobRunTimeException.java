package com.sos.js7.scriptengine.jobs.exceptions;

import com.sos.js7.scriptengine.jobs.AScriptJob;

public class ScriptJobRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScriptJobRunTimeException(AScriptJob job, String message, Exception e) {
        super("[" + job.getClass().getSimpleName() + "]" + message, e);
    }

}
