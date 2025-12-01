package com.sos.js7.scriptengine.jobs.commons;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class ScriptJobStdAdapter extends OutputStream {

    private final Writer writer;

    public ScriptJobStdAdapter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void write(int b) throws IOException {
        writer.write(b);
        flush();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writer.write(new String(b, off, len, java.nio.charset.StandardCharsets.UTF_8));
        flush();
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        // writer.close(); // do not close the parent writer
    }

}
