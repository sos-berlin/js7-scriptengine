package com.sos.js7.scriptengine.jobs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
    public void testJobPythonInfos() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Python-Infos.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobDeclaredArguments() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-DeclaredArguments.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        // if JS7Job.jobdef uses js7.IncludableArgument.SSH_PROVIDER - set required argument user
        // args.put("user", "from java PythonJobTest");

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("first_map", Collections.singletonMap("submap_1", 1));
        args.put("op_arg_map", map);
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobAllowedOptions() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-AllowedOptions.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        // if JS7Job.jobdef uses js7.IncludableArgument.SSH_PROVIDER - set required argument user
        // args.put("user", "from java PythonJobTest");

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("first_map", Collections.singletonMap("submap_1", 1));
        args.put("op_arg_map", map);
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
        String file = "src/test/resources/jobs/python/custom_modules/JS7Job-mysql.connector.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        // args.put(ARG_NAME_OPTIONS, options.toString()); // as file

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobLogging() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Logging.jobdef";

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobLoggingStdAdapter() throws Exception {
        // ScripJob.createBuilder: builder.out/builder.err should be set if JS7 environment
        // -- is not really testable because of SLFJ logger used by Junit tests
        // but SLFJ logger produces expected output if cripJob.createBuilder: builder.out/builder.err are not set ..
        String file = "src/test/resources/jobs/python/JS7Job-Logging-StdAdapter.jobdef";
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options));

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
        String file = "src/test/resources/jobs/python/JS7Job-Simple-WithConstructor.jobdef";

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

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocess() throws Exception {
        String file = "src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess.jobdef";

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        args.put("python_executable", "D:/Programme/Python/3.13.9/python.exe");
        args.put("python_app", "src/test/resources/apps/python/cpython_app.py");

        // execute(new PythonJob(null), file, args, 5);
        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithJS7ModulesJavaObjectInspector() throws Exception {
        String file = "src/test/resources/jobs/python/custom_modules/JS7Job-js7.modules-java_object_inspector.jobdef";

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args);
    }

    @Ignore
    @Test
    public void testJobWithJS7ModulesJS7Job() throws Exception {
        // See src/test/resporces/modules/python/js7
        // PythonJob.py is a copy of src/resources/PythonJob.jobdef to make it importable as a module
        // Note: currently, the job is loaded twice (once from .jobdef, once as the module)

        String file = "src/test/resources/jobs/python/custom_modules/JS7Job-js7.modules-js7_job_hello_world.jobdef";

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), file, args);
    }
}
