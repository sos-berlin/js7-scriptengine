package com.sos.js7.scriptengine.jobs.commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sos.commons.util.SOSPath;
import com.sos.commons.util.SOSReflection;
import com.sos.js7.job.Job;
import com.sos.js7.job.JobArguments;
import com.sos.js7.job.UnitTestJobHelper;
import com.sos.js7.job.exception.JobException;

import js7.data_for_java.order.JOutcome;

public abstract class ScriptJobTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptJobTest.class);

    public static void setAgentProperties() {
        Path privateConfPath = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources");
        System.setProperty("js7.config-directory", privateConfPath.toString());
        System.setProperty("JS7_AGENT_CONFIG_DIR", privateConfPath.toString());
    }

    public void execute(Job<JobArguments> job, String file, Map<String, Object> args) throws Exception {
        execute(job, file, args, 0);
    }

    public void execute(Job<JobArguments> job, String file, Map<String, Object> args, int cancelAfterSeconds) throws Exception {
        String script = "";

        if (file != null) {
            script = SOSPath.readFile(Paths.get(file));
        }
        UnitTestJobHelper<JobArguments> h = new UnitTestJobHelper<>(job);
        SOSReflection.setDeclaredFieldValue(h.getJob(), "script", script);
        if (cancelAfterSeconds > 0) {
            h.getStepConfig().setCancelAfterSeconds(cancelAfterSeconds);
        }
        boolean started = false;
        try {
            h.onStart(args);
            started = true;
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg == null) {
                msg = e.toString();
            }
            LOGGER.error("[onStart]" + msg, new JobException("[onStart]" + msg, e));
        }
        if (started) {
            JOutcome.Completed result = h.processOrder(args);
            LOGGER.info("###############################################");
            LOGGER.info(String.format("[RESULT]%s", result));

            h.onStop();
        }
    }

    public void addCredentialStoreArguments(Map<String, Object> args) {
        args.put("credential_store_file", "kdbx-p.kdbx");
        args.put("credential_store_password", "test");
        args.put("credential_store_entry_path", "/server/SFTP/localhost");
    }

    public void addSSHProviderArguments(Map<String, Object> args) {
        args.put("host", "localhost");
        args.put("port", 22);
        args.put("auth_method", "password");
        args.put("user", "sos");
        args.put("password", "sos");
    }
}
