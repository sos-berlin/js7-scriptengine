from js7.modules.js7_java_object_inspector import JS7JavaObjectInspector

class JS7JobArguments:
    def __init__(self):
        self.declared_password = js7.JobArgument("declared_password", True, "secret", js7.DisplayMode.MASKED)
        self.declared_boolean = js7.JobArgument("declared_boolean", False)
        self.declared_integer = js7.JobArgument("declared_integer", False)
        self.declared_float = js7.JobArgument("declared_float", False)
        self.declared_string = js7.JobArgument("declared_string", False)

class JS7Job(js7.Job):
    
    def __init__(self):
        self.declaredArguments = JS7JobArguments()
        
    def processOrder(self, js7Step):
        inspector = JS7JavaObjectInspector()
    
        inspector.log_public_methods(js7Step.getLogger(), "self.getJobEnvironment()", self.getJobEnvironment())
        inspector.log_public_methods(js7Step.getLogger(), "js7Step", js7Step)
        
        inspector.log_arguments(js7Step)
        
        js7Step.getLogger().info("-----------------------")
        args = js7Step.getAllArguments()
        args_dict = {key: str(arg.getValue()) for key, arg in args.items()}
        js7Step.getLogger().info(f"All Arguments[Java/Pytnon][Map/Dict] {args}")
        
        args = js7Step.getAllArgumentsAsNameValueMap()
        js7Step.getLogger().info(f"getAllArgumentsAsNameValueMap: {args}")
        for name, value in args.items():
            js7Step.getLogger().info(f"    [{name}]={inspector.describe_object(value, False)}")
            js7Step.getLogger().info(f"        is_java_object={inspector.is_java_object(value)}")        
        
        js7Step.getLogger().info(f"All Arguments[Pytnon][args_dict] {args_dict}")
        js7Step.getLogger().info(f"getAllArgumentsAsNameValueStringMap: {js7Step.getAllArgumentsAsNameValueStringMap()}")
        