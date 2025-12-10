# Example of calling a CPython application that cannot be loaded/executed directly via the GraalVM Polyglot interface.
#
# Variant 1 - wait - not used
# result = subprocess.run(
#    [r"D:\Programme\Python\3.13.9\python.exe", "config/python/my_script.py", "js7Step_argument_1", "js7Step_argument_2"],
#    [python_executable, python_app, "js7Step_argument_1", "js7Step_argument_2"],
#    capture_output=True,
#    text=True
# ) 
# result = subprocess.run(['echo', '123'], stdout=open('file.txt', 'w'), capture_output=True, text=True) 
# logger.info(f"[subprocess][returncode={result.returncode}][stderr={result.stderr.strip()}]{result.stdout.strip()}")
# logger.info(f"[subprocess][returncode={result.returncode}][stderr={result.stderr}]{result.stdout}")   
#
# For the variant below, in principle, a new js7Step method (e.g., js7Step.executeProcess(...)) can be introduced
# - which internally creates a Java process, handling stdout/stderr and 
#   supporting cancelable resource management.
class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        python_executable = js7Step.getAllArgumentsAsNameValueMap().get("python_executable")
        python_app = js7Step.getAllArgumentsAsNameValueMap().get("python_app")
      
        #######################################################################################################
        # Step 1 - Pre-Processing - calculate python_app arguments       
        python_app_args = ["js7Step_argument_1", "js7Step_argument_2"]  # calculation result, here hard-coded
        #######################################################################################################
        
        logger = js7Step.getLogger()
        logger.info(f"[subprocess]start...") 
        
        #######################################################################################################
        # Step 2 - Processing - call python_app      
        #
        # live stdout/stderr, -u - unbuffered
        import subprocess
        process = subprocess.Popen(
            [python_executable, "-u" , python_app] + python_args_arguments,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1
        )
        js7Step.setCancelableResource(process)
        
        # Note:
        # This is a simple example showing how to read stdout/stderr.
        # Warning: reading stdout like this can block reading stderr.
        # For a robust solution that avoids blocking, see ManagedProcess examples using threading.
        for line in process.stdout:
            logger.info(f"[LIVE STDOUT]{line.rstrip()}")            

        for line in process.stderr:
            logger.error(f"[LIVE STDERR]{line.rstrip()}")

        returncode = process.wait()
        
        #######################################################################################################
        # Step 3 - Post-Processing - evaluate return code/stderr etc. to set the js7Step outcome      
        
        logger.info(f"[subprocess]returncode={returncode}")
        # js7Step.getOutcome().set returnCode
        
    def onProcessOrderCanceled(self, js7Step):
        #######################################################
        process = js7Step.getCancelableResource()
        #######################################################
        
        js7Step.getLogger().info(f"onProcessOrderCanceled={process}")
        if process is not None:
            # process.terminate()
            process.kill()
