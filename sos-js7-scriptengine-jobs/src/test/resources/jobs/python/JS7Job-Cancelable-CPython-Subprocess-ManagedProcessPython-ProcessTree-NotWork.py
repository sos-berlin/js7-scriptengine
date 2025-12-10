import subprocess, threading
import sys, os, signal


class ManagedProcess:
    
    @staticmethod
    def is_windows():
       return sys.platform == "win32"

    @staticmethod
    def _stream_reader(js7Step, is_stderr, stream):
        for line in iter(stream.readline, ''):
            if is_stderr:
                js7Step.getLogger().error(line.rstrip())
            else:
                js7Step.getLogger().info(line.rstrip())

    @staticmethod
    def run(js7Step, executable, app, app_args=None):
        if app_args is None:
            app_args = []

        if ManagedProcess.is_windows():
            creationflags = getattr(subprocess, "CREATE_NEW_PROCESS_GROUP", 0)
            preexec_fn = None
        else:
            creationflags = 0
            preexec_fn = os.setsid

        process = subprocess.Popen(
            [executable, "-u", app] + app_args,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1,
            creationflags=creationflags,
            preexec_fn=preexec_fn
        )
        js7Step.setCancelableResource(process)

        threading.Thread(target=ManagedProcess._stream_reader, args=(js7Step, False, process.stdout), daemon=True).start()
        threading.Thread(target=ManagedProcess._stream_reader, args=(js7Step, True, process.stderr), daemon=True).start()        

        return process.wait()

    @staticmethod
    def cancel(js7Step):
        if js7Step is None:
            return
            
        process = js7Step.getCancelableResource()
        if process is None:
            return
        
        try:
            pid = process.pid  # always 1
            if process.poll() is None:
                if ManagedProcess.is_windows():
                    js7Step.getLogger().info(f"[ManagedProcess.cancel]{pid}...")
                    p = subprocess.run(
                        ["taskkill", "/PID", str(pid), "/T", "/F"],
                        stdout=subprocess.DEVNULL,
                        stderr=subprocess.DEVNULL
                    )
                    js7Step.getLogger().info(f"[ManagedProcess.cancel]{p}")
                else:
                    os.killpg(os.getpgid(pid), signal.SIGKILL)
        except Exception as e:
            js7Step.getLogger.error(f"[ManagedProcess.cancel]{e}") 

    
class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        python_executable = js7Step.getAllArgumentsAsNameValueMap().get("python_executable")
        python_app = js7Step.getAllArgumentsAsNameValueMap().get("python_app")
        
        #######################################################################################################
        # Step 1 - Pre-Processing - calculate python_app arguments       
        python_app_args = ["js7Step_argument_1", "js7Step_argument_2"]  # calculation result, here hard-coded

        js7Step.getLogger().info(f"[ManagedProcess]run...")
        #######################################################################################################
        # Step 2 - Processing - call python_app      
        returncode = ManagedProcess.run(js7Step, python_executable, python_app, python_app_args)      
        #######################################################################################################
        # Step 3 - Post-Processing - evaluate return code/stderr, etc. to set the js7Step outcome: js7Step.ge      
        js7Step.getLogger().info(f"[ManagedProcess]returncode={returncode}")
        js7Step.getOutcome().setReturnCode(returncode);
        js7Step.getOutcome().putVariable("outcome_var_name", "outcome_var_value");
        
    def onProcessOrderCanceled(self, js7Step):
         ManagedProcess.cancel(js7Step)
