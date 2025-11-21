package com.sos.js7.scriptengine.jobs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.js7.scriptengine.jobs.commons.ScriptJobTest;

public class JavaScriptJobTest extends ScriptJobTest {

    @Ignore
    @Test
    public void testJobDeclaredArguments() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-DeclaredArguments.js";

        Map<String, Object> args = new HashMap<>();
        args.put("my_arg1", "xyz");
        args.put("my_arg2", "xyz");

        execute(new JavaScriptJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobNodeJS() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-NODEJS.js";

        Map<String, Object> args = new HashMap<>();
        args.put("js7_options.graalvm.js", "src/test/resources/jobs/js/ScriptJobOptions.json");

        execute(new JavaScriptJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithCredentialStore() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-CredentialStore.js";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);

        execute(new JavaScriptJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithSSHProvider() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-SSHProvider.js";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new JavaScriptJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithJOCApiExecutor() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-JOCApiExecutor.js";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new JavaScriptJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithSOSHibernate() throws Exception {
        String file = "src/test/resources/jobs/js/JS7Job-SOSHibernate.js";

        Map<String, Object> args = new HashMap<>();

        execute(new JavaScriptJob(null), file, args);
    }

}
