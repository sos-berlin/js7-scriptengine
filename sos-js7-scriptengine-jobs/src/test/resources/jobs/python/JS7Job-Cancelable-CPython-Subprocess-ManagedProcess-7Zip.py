#  ManagedProcess - see script_includes/Python-ManagedProcess.py


class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        basic_cmd = js7Step.getAllArgumentsAsNameValueMap().get("basic_cmd")
        target = js7Step.getAllArgumentsAsNameValueMap().get("target")
        source = js7Step.getAllArgumentsAsNameValueMap().get("source")
        
        cmd = []
        if basic_cmd is None:
            cmd = [
                r"C:\Program Files\7-Zip\7z.exe",  # Full path to 7z.exe
                "a",  # Add / create archive
                "-tzip",  # Archive type ZIP
                "-mx=9",  # Maximum compression level
                "-r",  # Include files recursively
            ]
        else:
            import shlex
            cmd = shlex.split(basic_cmd)
        
        # cmd.append(fr"{target}")
        # cmd.append(fr"{source}")
        
        cmd.append(target)
        cmd.append(source)
        
        # Run the program.
        js7Step.getLogger().info(f"[ManagedProcess][run...]{cmd}")
        returncode = ManagedProcess.run(js7Step, cmd)      
        js7Step.getLogger().info(f"[ManagedProcess]returncode={returncode}")
        
        js7Step.getOutcome().setReturnCode(returncode)
        
    def onProcessOrderCanceled(self, js7Step):
        ManagedProcess.cancel(js7Step)
