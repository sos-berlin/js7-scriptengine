package com.sos.js7.scriptengine.jobs.exceptions;

import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.SourceSection;

import com.sos.commons.exception.SOSException;

public class ScriptJobException extends SOSException {

    private static final long serialVersionUID = 1L;

    private StringBuilder message = null;

    public ScriptJobException(String language, String message) {
        this.message = new StringBuilder();
        this.message.append("[").append(language).append("]");
        if (message != null) {
            this.message.append(message);
        }
    }

    public ScriptJobException(String language, String message, Exception e) {
        this(language, message);
        this.initCause(e);
    }

    public ScriptJobException(String language, int jobDefinitionLinesCount, PolyglotException e) {
        this(language, null, e);

        String polyglotMessage = e.getMessage();
        polyglotMessage = polyglotMessage == null ? "" : polyglotMessage;
        if (e.isSyntaxError()) {

            SourceSection section = e.getSourceLocation();
            if (section == null) {
                message.append(polyglotMessage);
            } else {
                CharSequence cs = section.getCharacters();
                String details = cs == null ? "" : cs.toString().trim();
                boolean detailsNotEmpty = !details.isEmpty();

                message.append("[SyntaxError]");
                if (detailsNotEmpty) {
                    message.append("[");
                }
                message.append(getSourceSectionMessage(jobDefinitionLinesCount, section));
                if (detailsNotEmpty) {
                    message.append("]");
                }
                if (polyglotMessage.startsWith("IndentationError:")) { // Python
                    if (detailsNotEmpty) {
                        message.append("[");
                    }
                    message.append(getIndentationError(polyglotMessage));
                    if (detailsNotEmpty) {
                        message.append("]");
                    }
                }
                if (detailsNotEmpty) {
                    message.append(details);
                }
            }
        } else if (e.isGuestException()) { // runtime error, e.g. Python ModuleNotFoundError
            // String type = e.getGuestObject().getMetaObject().getMetaSimpleName();

            // the lines informations is not used for ModuleNotFoundError - polyglot provides lines e.g. 1305 - which do not exist...
            if (!polyglotMessage.startsWith("ModuleNotFoundError:")) {
                SourceSection section = e.getSourceLocation();
                if (section != null) {
                    message.append("[" + getSourceSectionMessage(jobDefinitionLinesCount, section) + "]");
                }
            }
            message.append(polyglotMessage);
        } else {
            message.append(polyglotMessage);
        }
    }

    private static StringBuilder getSourceSectionMessage(int jobDefinitionLinesCount, SourceSection section) {
        int startLine = getLine(jobDefinitionLinesCount, section.getStartLine());
        int endLine = getLine(jobDefinitionLinesCount, section.getEndLine());

        StringBuilder sb = new StringBuilder();
        if (startLine == endLine) {
            sb.append("line=").append(startLine);
            sb.append(", startColumn=").append(section.getStartColumn());
            sb.append(", endColumn=").append(section.getEndColumn());
        } else {
            sb.append("startLine=").append(getLine(jobDefinitionLinesCount, section.getStartLine()));
            sb.append(", startColumn=").append(section.getStartColumn());
            sb.append(", endLine=").append(getLine(jobDefinitionLinesCount, section.getEndLine()));
            sb.append(", endColumn=").append(section.getEndColumn());
        }
        return sb;
    }

    /** Removed '(Unnamed, line n)' because the line number is "incorrect" - see getLine()
     * 
     * @param polyglotMessage, e.g.: IndentationError: unindent does not match any outer indentation level (Unnamed, line 42)
     * @return */
    private static String getIndentationError(String polyglotMessage) {
        int indx = polyglotMessage.indexOf('(');
        if (indx > 0) {
            return polyglotMessage.substring(0, indx).trim();
        }
        return polyglotMessage;
    }

    /** Recalculates the provided polyglot line because it is based on a combined script (job definition + user-defined job)
     * 
     * @param jobDefinitionLinesCount
     * @param polyglotLine
     * @return */
    private static int getLine(int jobDefinitionLinesCount, int polyglotLine) {
        if (polyglotLine <= jobDefinitionLinesCount) {
            return polyglotLine;
        }
        return polyglotLine - jobDefinitionLinesCount;
    }

    @Override
    public String getMessage() {
        return message == null ? null : message.toString();
    }

    @Override
    public String toString() {
        String result = super.toString();

        return result;
    }

}
