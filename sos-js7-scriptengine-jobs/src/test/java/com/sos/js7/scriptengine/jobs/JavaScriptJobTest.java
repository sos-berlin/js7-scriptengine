package com.sos.js7.scriptengine.jobs;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.sos.commons.util.SOSPath;
import com.sos.js7.scriptengine.jobs.commons.ScriptJobTest;

public class JavaScriptJobTest extends ScriptJobTest {

    private static final String ARG_NAME_OPTIONS = "js7_options.graalvm.js";

    @Ignore
    @Test
    public void testJobDeclaredArguments() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-DeclaredArguments.js");

        Map<String, Object> args = new HashMap<>();
        args.put("my_arg1", "xyz");
        args.put("my_arg2", "xyz");

        execute(new JavaScriptJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobNodeJS() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-NODEJS.js");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, "src/test/resources/jobs/js/ScriptJobOptions.json");

        execute(new JavaScriptJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithCredentialStore() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-CredentialStore.js");

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);

        execute(new JavaScriptJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithJOCApiExecutor() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-JOCApiExecutor.js");

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new JavaScriptJob(null), script, args);
    }

    @Ignore
    @Test
    public void testJobWithCancelableSSHProvider() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-Cancelable-SSHProvider.js");

        Map<String, Object> args = new HashMap<>();
        addCredentialStoreArguments(args);
        addSSHProviderArguments(args);

        execute(new JavaScriptJob(null), script, args, 5);
    }

    @Ignore
    @Test
    public void testJobWithCancelableSOSHibernate() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/JS7Job-Cancelable-SOSHibernate-SQLExecutor.js");

        Map<String, Object> args = new HashMap<>();

        execute(new JavaScriptJob(null), script, args, 5);
    }

    @Ignore
    @Test
    public void testJobWithJS7ModulesJavaObjectInspector() throws Exception {
        Path script = Paths.get("src/test/resources/jobs/js/custom_modules/JS7Job-js7.modules-java_object_inspector.js");

        Path options = Paths.get("src/test/resources/jobs/js/ScriptJobOptions.json");

        Map<String, Object> args = new HashMap<>();
        args.put(ARG_NAME_OPTIONS, SOSPath.readFile(options)); // as string

        execute(new JavaScriptJob(null), script, args);
    }

}
