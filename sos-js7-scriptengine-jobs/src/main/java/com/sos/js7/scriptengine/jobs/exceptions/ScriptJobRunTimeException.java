package com.sos.js7.scriptengine.jobs.exceptions;

public class ScriptJobRunTimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ScriptJobRunTimeException(String language, String message, Exception e) {
        super("[" + language + "]" + message, e);
    }

}
