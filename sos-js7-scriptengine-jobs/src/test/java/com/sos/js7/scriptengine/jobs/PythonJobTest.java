package com.sos.js7.scriptengine.jobs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.commons.util.SOSPath;
import com.sos.js7.scriptengine.jobs.commons.ScriptJobTest;

public class PythonJobTest extends ScriptJobTest {

    private static final String ARG_NAME_OPTIONS = "js7_options.graalvm.python";

    @Ignore
    @Test
    public void testJobDeclaredArguments() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-DeclaredArguments.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        // if JS7Job.jobdef uses js7.IncludableArgument.SSH_PROVIDER - set required argument user
        // args.put("user", "from java PythonJobTest");
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobHelloWorld() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-HelloWorld.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options));
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobImportCustomModule() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-ImportCustomModule-mysql.connector.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        // args.put(ARG_NAME_OPTIONS, options.toString()); // as file

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
    public void testJobSimpleWithConstructor() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Simple-WithConsctructor.jobdef";

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithCredentialStore() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-DeclaredArguments-IncludedArguments-CredentialStore.jobdef";

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithSSHProvider() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Cancelable-SSHProvider.jobdef";

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
        String file = "src/test/resources/jobs/python/JS7Job-Cancelable-SOSHibernate-SQLExecutor.jobdef";

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithExecuteJobSQLExecutorJob() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-ExecuteJob-SQLExecutorJob.jobdef";

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithCancelablePythonObject() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Cancelable-PythonObject.jobdef";

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args, 5);
    }
}
