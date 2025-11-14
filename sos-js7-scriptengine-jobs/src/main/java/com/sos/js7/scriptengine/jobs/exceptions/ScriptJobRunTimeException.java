package com.sos.js7.scriptengine.jobs.exceptions;

import com.sos.js7.scriptengine.jobs.ScriptJob;

public class ScriptJobRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScriptJobRunTimeException(ScriptJob job, String message, Exception e) {
        super("[" + job.getClass().getSimpleName() + "]" + message, e);
    }

}
