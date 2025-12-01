# ScripJob.createBuilder: builder.out/builder.err should be set
# is not really testable because of SLFJ logger used by Junit tests
       
class JS7Job(js7.Job):
    def processOrder(self, js7Step):
        import logging, sys
        
        js7Step.getLogger().info('[js7Step.getLogger().info]hello world')
        print("[print]hello world")
        
        try:
            from pathlib import Path as PythonPath
            with PythonPath("path.txt").open("r") as f:
                print(f.read())       
        except Exception as e:
            print(f"[print][stdout][Exception] {e}", e)
            print(f"[print][stderr][Exception] {e}", e, file=sys.stderr)
        
        logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)
        logging.info("logging.info")
        logging.debug("logging.debug")
