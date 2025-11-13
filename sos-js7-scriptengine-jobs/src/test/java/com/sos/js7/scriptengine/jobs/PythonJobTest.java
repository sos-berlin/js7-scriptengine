package com.sos.js7.scriptengine.jobs;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.js7.scriptengine.jobs.commons.ScriptJobTest;

public class PythonJobTest extends ScriptJobTest {

    @Ignore
    @Test
    public void testJob() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job.jobdef";

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobSimple() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Simple.jobdef";

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobSimplePrint() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Simple-Print.jobdef";

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobSimpleErrors() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Simple-Errors.jobdef";

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithCredentialStore() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-CredentialStore.jobdef";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithSSHProvider() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-SSHProvider.jobdef";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithJOCApiExecutor() throws Exception {
        setAgentProperties();

        String file = "src/test/resources/jobs/python/JS7Job-JOCApiExecutor.jobdef";

        Map<String, Object> args = new HashMap<>();
        // addCredentialStoreArguments(args);
        // addSSHProviderArguments(args);

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithSOSHibernate() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-SOSHibernate.jobdef";

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), file, args);
    }

}
