package com.sos.js7.scriptengine.jobs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Python-Infos.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobDeclaredArguments() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-DeclaredArguments.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        // if JS7Job-DeclaredArguments.py uses js7.IncludableArgument.SSH_PROVIDER - set required argument user
        // args.put("user", "from java PythonJobTest");

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("first_map", Collections.singletonMap("submap_1", 1));
        args.put("op_arg_map", map);
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobAllowedOptions() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-AllowedOptions.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();

        Map<String, Map<String, Integer>> map = new HashMap<>();
        map.put("first_map", Collections.singletonMap("submap_1", 1));
        args.put("op_arg_map", map);
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobHelloWorld() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-HelloWorld.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options));
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobImportCustomModule() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/custom_modules/JS7Job-mysql.connector.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        // args.put(ARG_NAME_OPTIONS, options.toString()); // as file

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobLogging() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Logging.py");

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobLoggingStdAdapter() throws Exception {
        // ScripJob.createBuilder: builder.out/builder.err should be set if JS7 environment
        // -- is not really testable because of SLFJ logger used by Junit tests
        // but SLFJ logger produces expected output if cripJob.createBuilder: builder.out/builder.err are not set ..
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Logging-StdAdapter.py");
        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options));

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobSimpleErrors() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Simple-Errors.py");

        Map<String, Object> args = new HashMap<>();
        // args.put("my_arg1", "xyz");
        // args.put("my_arg2", "xyz");

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobSimpleWithConstructor() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Simple-WithConstructor.py");

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithCredentialStore() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-DeclaredArguments-IncludedArguments-CredentialStore.py");

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithSSHProvider() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-SSHProvider.py");

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithJOCApiExecutor() throws Exception {
        setAgentProperties();

        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-JOCApiExecutor.py");

        Map<String, Object> args = new HashMap<>();
        // addCredentialStoreArguments(args);
        // addSSHProviderArguments(args);

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithSOSHibernate() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-SOSHibernate-SQLExecutor.py");

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithExecuteJobSQLExecutorJob() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-ExecuteJob-SQLExecutorJob.py");

        Map<String, Object> args = new HashMap<>();

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithCancelablePythonObject() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-PythonObject.py");

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args, 5);
    }

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocess() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess.py");

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        args.put("python_executable", "D:/Programme/Python/3.13.9/python.exe");
        args.put("python_app", "src/test/resources/apps/python/cpython_app.py");

        execute(new PythonJob(null), script, args, -5);
    }

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocessManagedPython() throws Exception {
        List<Path> scriptFiles = new ArrayList<>();
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/script_includes/Python-ManagedProcess.py"));
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess-ManagedProcess.py"));

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        args.put("python_executable", "D:/Programme/Python/3.13.9/python.exe");
        args.put("python_app", "src/test/resources/apps/python/cpython_app.py");

        execute(new PythonJob(null), scriptFiles, args, 0);
    }

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocessManagedPythonProcessTree() throws Exception {
        List<Path> scriptFiles = new ArrayList<>();
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/script_includes/Python-ManagedProcess-ProcessTree-NotWork.py"));
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess-ManagedProcess.py"));

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        args.put("python_executable", "D:/Programme/Python/3.13.9/python.exe");
        args.put("python_app", "src/test/resources/apps/python/cpython_app.py");

        execute(new PythonJob(null), scriptFiles, args, 5);
    }

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocessManagedJava() throws Exception {
        List<Path> scriptFiles = new ArrayList<>();
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/script_includes/Python-ManagedProcess-Java.py"));
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess-ManagedProcess.py"));

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        args.put("python_executable", "D:/Programme/Python/3.13.9/python.exe");
        args.put("python_app", "src/test/resources/apps/python/cpython_app.py");

        execute(new PythonJob(null), scriptFiles, args, 5);
    }

    @Ignore
    @Test
    public void testJobWithCancelableCPythonSubprocessManaged7Zip() throws Exception {
        List<Path> scriptFiles = new ArrayList<>();
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/script_includes/Python-ManagedProcess.py"));
        scriptFiles.add(Paths.get("src/test/resources/jobs/python/JS7Job-Cancelable-CPython-Subprocess-ManagedProcess-7Zip.py"));

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string
        // set basic_cmd - python shlex.split(basic_cmd) is used
        // args.put("basic_cmd", "\"C:\\Program Files\\7-Zip\\7z.exe\" a -tzip -mx=9 -r");
        args.put("target", "src\\test\\resources\\tmp\\python_archive.zip");
        args.put("source", "src/test/resources/jobs/python/*");

        execute(new PythonJob(null), scriptFiles, args, 5);

        SOSPath.deleteIfExists(Paths.get(args.get("target").toString()));
    }

    @Ignore
    @Test
    public void testJobWithJS7ModulesJavaObjectInspector() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/custom_modules/JS7Job-js7.modules-java_object_inspector.py");

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithJS7ModulesJS7Job() throws Exception {
        // See src/test/resporces/modules/python/js7
        // PythonJob.py is a copy of src/resources/PythonJob.jobdef to make it importable as a module
        // Note: currently, the job is loaded twice (once from .jobdef, once as the module)

        Path script = Paths.get("src/test/resources/jobs/python/custom_modules/JS7Job-js7.modules-js7_job_hello_world.py");

        Path options = Paths.get("src/test/resources/jobs/python/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new PythonJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobUtilities() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/python/JS7Job-Utilities.py");

        Map<String, Object> args = new HashMap<>();

        List<String> commands = new ArrayList<>();
        commands.add("python myscript.py --opt \"Hello World\"");
        commands.add("\"C:\\Program Files\\7-Zip\\7z.exe\" a -tzip -mx=9 -r \"D:\\my_output.zip\" \"D:\\source_dir\"");

        args.put("commands", commands);
        execute(new PythonJob(null), script, args);
    }

}
