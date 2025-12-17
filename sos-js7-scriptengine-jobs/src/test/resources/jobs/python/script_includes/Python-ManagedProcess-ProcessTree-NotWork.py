import subprocess, threading
import sys, os, signal

"""
Attempts to kill not only the main subprocess but also any child processes it may have spawned.

Note: This is currently not functional, because under GraalVM Polyglot 'process.pid' always returns 1 instead of the actual PID. 
As a result, reliably identifying and terminating child processes is not possible, at least under Windows based on our tests.

Alternative: Java's ProcessBuilder could be used instead, because it provides the actual system PID.
    See JS7Job-Cancelable-CPython-Subprocess-ManagedProcessJava.py
"""


class ManagedProcess:
    
    @staticmethod
    def is_windows():
       return sys.platform == "win32"

    @staticmethod
    def _stream_reader(logger, is_stderr, stream):
        for line in iter(stream.readline, ''):
            if not line:
                break  # EOF
            
            line = line.rstrip()
            if is_stderr:
                logger.error(line)
            else:
                logger.info(line)

    @staticmethod
    def run(js7Step, command_args):
        if not command_args:
            raise ValueError("No command specified: 'command_args' must be a non-empty list")

        if ManagedProcess.is_windows():
            creationflags = getattr(subprocess, "CREATE_NEW_PROCESS_GROUP", 0)
            preexec_fn = None
        else:
            creationflags = 0
            preexec_fn = os.setsid

        process = subprocess.Popen(
            command_args,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            bufsize=1,
            creationflags=creationflags,
            preexec_fn=preexec_fn
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
        if js7Step is None:
            return
            
        process = js7Step.getCancelableResource()
        if process is None:
            return
        
        try:
            pid = process.pid  # always 1
            if process.poll() is None:
                if pid == 1:
                    js7Step.getLogger().error(f"[ManagedProcess.cancel]Invalid subprocess PID=1. Cannot reliably identify the subprocess children. Kill only subprocess...") 
                    process.kill()
                else:
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
            js7Step.getLogger().error(f"[ManagedProcess.cancel]{e}") 
