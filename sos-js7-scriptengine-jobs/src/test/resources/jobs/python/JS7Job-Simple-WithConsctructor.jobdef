class JS7Job(js7.Job):
    
    ##########################################################################################################    
    # custom constructor that skips super().__init__();
    # declaredArguments remains unset, but ScriptJob handles it by adding the missing property on the fly.
    ##########################################################################################################    
    def __init__(self):
        self.hello_from_python="Hello from Python Job" 
    
    def processOrder(self, js7Step):
        
        js7Step.getLogger().info(f"{self.hello_from_python}")
        