class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        logger = js7Step.getLogger()
        
        ############################################################
        # Test allowNativeAccess(false)
        ############################################################
        self.test_allow_native_access_java(logger)          # false     - works
        
        
        ############################################################
        # Test allowCreateProcess(false)
        ############################################################
        self.test_allow_create_process_python(logger)       # false     - [Errno 1] Operation not permitted: 'file.txt'
                                                            # true      - the same error if ioBuilder.allowHostFileAccess=false
        
        ############################################################
        # Test allowCreateThread(false)
        ############################################################
        self.test_allow_create_thread_python(logger)        # false     - PolyglotException: java.lang.IllegalStateException: Creating threads is not allowed.
                                                            # true      - works (also if ioBuilder.allowHostFileAccess=false)
        
        
        ############################################################
        # Test allowEnvironmentAccess(EnvironmentAccess.NONE)
        ############################################################
        self.test_allow_environment_access_python(logger)   # NONE      - no exceptions, returns None e.g., for PATH
                                                            # INHERIT   - works, returns env PATH value
        
        
        ############################################################
        # Test allowExperimentalOptions ??? How to test ???
        ############################################################
        self.test_allow_experimental_options_python(logger)
        
        ############################################################
        # Test allowPolyglotAccess(PolyglotAccess.NONE)
        ############################################################
        self.test_allow_polyglot_access_python(logger)      # NONE      - [Exception] polyglot access is not allowed
        
        
        ############################################################
        # Test allowValueSharing(false)
        ############################################################
        self.test_allow_value_sharing_python(js7Step)       # false|true is irrelevant here, because only a single context is created by the ScripJob and immediately closed, so no values are ever shared between contexts.
               
        
        ############################################################
        # Test allowHostClassLoading(false)
        ############################################################
        self.test_allow_host_class_loading_python(logger)  # false
            
        
        
        ############################################################
        # Test ioBuilder.allowHostFileAccess(false)
        ############################################################
        self.test_allow_host_file_access_python(logger)     # false     - throws Python exception [Errno 1] Operation not permitted: ... 
        self.test_allow_host_file_access_java(logger)       # false     - works 
        
        ############################################################
        # Test ioBuilder.allowHostSocketAccess(false)
        ############################################################
        #self.test_allow_host_socket_access_python(logger)   # false    - throws PolyglotException(a RuntimeException): socket was excluded
        #self.test_allow_host_socket_access_java(logger)     # false    - works 

    #############################################################################################################
    def test_allow_native_access_java(self, logger):
        import java
        from java.lang import Exception as JavaException
        SOSShell = java.type("com.sos.commons.util.SOSShell")
        try:
            logger.info(f"[test_allow_native_access_java]{SOSShell.executeCommand('echo 123')}")
        except JavaException as e:
            logger.error(f"[test_allow_native_access_java][JavaException] {e}") 
    
    def test_allow_create_process_python(self, logger):
        try:
            import subprocess
            
            result = subprocess.run([r"C:\Windows\System32\cmd.exe", "/c", "echo", "123"]) 
            logger.info(f"[test_allow_create_process_python][echo]works") 
            
            #result = subprocess.run(['echo', '123'], stdout=open('file.txt', 'w'), capture_output=True, text=True) 
            #logger.info(f"[test_allow_create_process_python][file][returncode={result.returncode}][stderr={result.stderr.strip()}]{result.stdout.strip()}")   
        except Exception as e:
            logger.error(f"[test_allow_create_process_python][Exception] {e}")    
    
    
    def test_allow_create_thread_python(self, logger):
        try:
            import threading
            
            t = threading.Thread(target=lambda: logger.info("thread running"))
            t.start()
            t.join()
            
            logger.info(f"[test_allow_create_thread_python]works") 
            
        except Exception as e:
            logger.error(f"[test_allow_create_thread_python][Exception] {e}")    
    
    
    def test_allow_environment_access_python(self, logger):
        try:
            import os
            
            logger.info(f"[test_allow_environment_access_python][PATH]{os.environ.get("PATH")}") 
        except Exception as e:
            logger.error(f"[test_allow_environment_access_python][Exception] {e}") 
  
    def test_allow_experimental_options_python(self, logger):
        try:
            import polyglot
            
            polyglot.option("python.ForceAsync", True)
            
            logger.info(f"[test_allow_experimental_options_python]works") 
        except Exception as e:
            logger.error(f"[test_allow_experimental_options_python][Exception] {e}") 
    
    def test_allow_polyglot_access_python(self, logger):
        try:
            import polyglot
            
            js = polyglot.eval(language="js", string='1+2')
            
            logger.info(f"[test_allow_polyglot_access_python][language=js]{js}") 
        except Exception as e:
            logger.error(f"[test_allow_polyglot_access_python][language=js][Exception] {e}") 
    
        try:
            import polyglot
            
            p = polyglot.eval(language="python", string='1+2')
            
            logger.info(f"[test_allow_polyglot_access_python][language=python]{p}") 
        except Exception as e:
            logger.error(f"[test_allow_polyglot_access_python][language=python][Exception] {e}") 
    
    def test_allow_value_sharing_python(self, js7Step):
        logger = js7Step.getLogger()
        
        # Test 1 - host object(js7Step) and python script - works if false
        rc = 999
        js7Step.getOutcome().setReturnCode(rc)
        #js7Step.getOutcome().putVariable("var_1", "var_1_value")    
        logger.info(f"[test_allow_value_sharing_python][java<->python]js7Step.getOutcome().setReturnCode({rc})") 
    
        # Test 2 - new context - not really - eval() only ...
        try:
            import polyglot
            p = polyglot.eval(language="python", string='1+2')
            logger.info(f"[test_allow_value_sharing_python][new context language=python]{p}") 
        except Exception as e:
            logger.error(f"[test_allow_value_sharing_python][new context language=python][Exception] {e}") 
        
    def test_allow_host_class_loading_python(self, logger):
        try:
            # Standardklassen funktionieren immer
            from java.io import File  
            f = File("test.txt")
            logger.info(f"[test_allow_host_class_loading_python][standard java class]{f}") 
        except Exception as e:
            logger.error(f"[test_allow_host_class_loading_python][standard java class][Exception] {e}") 
            
        try:
            # Dynamische Klasse, die vorher nicht geladen wurde
            import java, sys
            sys.path.append("D:/_Workspace/install/JS7/js7.x/agent/install/lib/sos/sos-jitl-jobs-2.8.2-SNAPSHOT.jar")
            f = java.type("com.sos.jitl.jobs.db.SQLExecutorJob")  
            logger.info(f"[test_allow_host_class_loading_python][custom class first load java]{f}") 
        except Exception as e:
            logger.error(f"[test_allow_host_class_loading_python][custom class first load java][Exception] {e}") 
    
    def test_allow_host_file_access_python(self, logger):
        from pathlib import Path as PythonPath
        python_path = PythonPath("path.txt")
        try:
            with python_path.open("r") as f:
                print(f.read())
        except Exception as e:
            logger.error(f"[test_allow_host_file_access_python][Exception] {e}")
                
        try:
            import subprocess
            subprocess.run(['echo', '123'], stdout=open('file.txt', 'w')) 
        except Exception as e:
            logger.error(f"[test_allow_host_file_access_python][Exception] {e}")    
    
    def test_allow_host_file_access_java(self, logger):
        from java.nio.file import Paths
        from java.nio.file import Files
        from java.nio.charset import StandardCharsets
        from java.io import IOException

        try:
            Files.readAllLines(Paths.get("file.txt"), StandardCharsets.UTF_8)
            logger.error(f"[test_allow_host_file_access_java]works")
        except IOException as e:
            logger.error(f"[test_allow_host_file_access_java][IOException] {e}")    
                
    
    def test_allow_host_socket_access_python(self, logger):
        import socket
        try:
            s = socket.socket()
            s.connect(("google.com", 80))
            s.close()
            logger.info("python socket connected/closed")
        except Exception as e:
            logger.error(f"[test_allow_host_socket_access_python][Exception] {e}")
    
    def test_allow_host_socket_access_java(self, logger):
        from java.net import Socket
        from java.lang import Exception as JavaException
        try:
            s = Socket("google.com", 80)
            s.close()
            logger.info("java socket opened/closed")
        except JavaException as e:
            logger.error(f"[test_allow_host_socket_access_java][JavaException] {e}")   
    
        
        
        