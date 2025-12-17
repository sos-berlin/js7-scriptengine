class ManagedProcess:

    @staticmethod
    def _stream_reader(logger, is_stderr, stream):
        for line in iter(stream.readline, ''):
            line = line.rstrip()
            if is_stderr:
                logger.error(line)
            else:
                logger.info(line)
    
    @staticmethod
    def run(js7Step, command_args):
        """
        Runs a subprocess with live stdout/stderr logging using threads.
        
        Notes:
            - This can run any program, not just Python scripts.
            - Python-specific flags (e.g., "-u" for unbuffered output) should be included directly in 'command_args' if needed.
            - stdout and stderr are read concurrently in separate threads to avoid blocking.
            - Raises ValueError if 'command_args' is None or empty.
        """
        
        if not command_args:
            raise ValueError("No command specified: 'command_args' must be a non-empty list")
         
        import subprocess, threading

        process = subprocess.Popen(
            command_args,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1
        )
        js7Step.setCancelableResource(process)

        # non-blocking reading: stdout and stderr are read concurrently using threads,
        # so reading one does not block the other.
        stdout_thread = threading.Thread(
            target=ManagedProcess._stream_reader,
            args=(js7Step.getLogger(), False, process.stdout),
            daemon=False
        )
        stderr_thread = threading.Thread(
            target=ManagedProcess._stream_reader,
            args=(js7Step.getLogger(), True, process.stderr),
            daemon=False
        )
        stdout_thread.start()
        stderr_thread.start()      

        # wait for the process to finish
        rc = process.wait()
        
        # ensure threads have finished reading
        stdout_thread.join()
        stderr_thread.join()

        return rc

    @staticmethod
    def cancel(js7Step):
        """
        Kills the subprocess if still running.
        Note: This only kills the main subprocess itself and does not terminate any child processes it may have spawned.
            In principle, a group kill (terminating all child processes) would be possible, 
            but under GraalVM Polyglot 'process.pid' always returns 1 instead of the actual PID, 
            so identifying and killing child processes reliably is not possible (at least under Windows in the tests).
        """
        
        if js7Step is None:
            return
            
        process = js7Step.getCancelableResource()
        if process is None:
            return
        
        try:
            if process.poll() is None:
                js7Step.getLogger().info(f"[ManagedProcess.cancel][kill]{process}...") 
                process.kill()
        except Exception as e:
            js7Step.getLogger().error(f"[ManagedProcess.cancel][{process}]{e}") 
