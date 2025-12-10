from java.lang import Thread, Runnable
from java.io import BufferedReader, InputStreamReader
import sys

"""
Kills not only the main subprocess but also any child processes it may have spawned, using Java's ProcessBuilder.

Note: 
    This is an example script. It should be implemented in actual Java, not via Python -> Java.

Problem when calling a Python executable without the "-u" argument:
    Some "live" Python stdout/stderr messages do not appear immediately but are delayed,
    because Python buffers its output and there is no flag available comparable to the Python subprocess option "-u" for unbuffered mode.
"""


class ManagedProcess:
    
    @staticmethod
    def is_windows():
       return sys.platform == "win32"
    
    @staticmethod
    def _create_stream_reader(logger, is_stderr, stream):

        class _ReaderRunnable(Runnable):

            # The run method must be implemented because of Runnable.
            # Reading is performed using BufferedReader.
            def run(self):
                reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
                try:
                    line = reader.readLine()
                    while line is not None:
                        if is_stderr:
                            logger.error(line)
                        else:
                            logger.info(line)
                        line = reader.readLine()
                finally:
                    try:
                        reader.close()
                    except Exception:
                        pass

            # run method (renamed)
            # Reads byte-wise instead of using BufferedReader.
            #
            # Same issue as above: Python output is still delayed because of Python's own buffering if -u is not set. 
            def runReadBytes(self):
                from java.io import InputStreamReader

                reader = InputStreamReader(stream)
                buf = ''
                while True:
                    c = reader.read()
                    if c == -1:  # EOF
                        break
                    char = chr(c)
                    buf += char
                    if char == '\n':  # line is complete → loggen
                        line = buf.rstrip()
                        if is_stderr:
                            logger.error(line)
                        else:
                            logger.info(line)
                        buf = ''
                # last line without \n
                if buf:
                    if is_stderr:
                        logger.error(buf)
                    else:
                        logger.info(buf)

        return Thread(_ReaderRunnable())
    
    @staticmethod
    def run(js7Step, command_args):
        from java.lang import ProcessBuilder
        
        if command_args is None:
            command_args = []
            
        if not ManagedProcess.is_windows():
            # Prepend "setsid" on Unix to start the subprocess in a new process group,
            # so that the main process and all its children can later be terminated together.
            command_args = ["setsid"] + command_args

        pb = ProcessBuilder(*command_args)        
        process = pb.start()
        js7Step.setCancelableResource(process)
        
        stdout_thread = ManagedProcess._create_stream_reader(js7Step.getLogger(), False, process.getInputStream())
        stderr_thread = ManagedProcess._create_stream_reader(js7Step.getLogger(), True, process.getErrorStream())  
        stdout_thread.start()
        stderr_thread.start()

        rc = process.waitFor()
        
        stdout_thread.join()
        stderr_thread.join()
        return rc
 
    @staticmethod
    def cancel(js7Step):
        if js7Step is None:
            return
            
        process = js7Step.getCancelableResource()
        if process is None:
            return
        
        from java.lang import ProcessBuilder
        try:
            if process.isAlive():
                js7Step.getLogger().info(f"[ManagedProcess.cancel]{process}...") 
                pid = str(process.pid())
                if ManagedProcess.is_windows():
                    command_args = ["taskkill", "/PID", pid, "/T", "/F"]
                else:
                    command_args = ["kill", "-SIGKILL", "-" + pid]
                    
                rc = ProcessBuilder(*command_args).inheritIO().start().waitFor()
                js7Step.getLogger().info(f"[ManagedProcess.cancel]rc={rc}") 
                
        except Exception as e:
            js7Step.getLogger().error(f"[ManagedProcess.cancel][{process}]{e}") 
    
    
class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        
        #######################################################################################################
        # Step 1 - Pre-Processing - calculate python_app arguments       
        python_executable = js7Step.getAllArgumentsAsNameValueMap().get("python_executable")
        python_app = js7Step.getAllArgumentsAsNameValueMap().get("python_app")
        
        command_args = [python_executable, "-u", python_app]
         # calculation result, here hard-coded
        command_args.append("js7Step_argument_1")
        command_args.append("js7Step_argument_2")

        js7Step.getLogger().info(f"[ManagedProcess]run...")
        #######################################################################################################
        # Step 2 - Processing - call python_app      
        returncode = ManagedProcess.run(js7Step, command_args)      
        #######################################################################################################
        # Step 3 - Post-Processing - evaluate return code/stderr, etc. to set the js7Step outcome: js7Step.ge      
        js7Step.getLogger().info(f"[ManagedProcess]returncode={returncode}")
        js7Step.getOutcome().setReturnCode(returncode);
        js7Step.getOutcome().putVariable("outcome_var_name", "outcome_var_value");
        
    def onProcessOrderCanceled(self, js7Step):
         ManagedProcess.cancel(js7Step)
