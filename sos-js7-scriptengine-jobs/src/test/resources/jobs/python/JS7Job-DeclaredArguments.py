from pathlib import Path
from java.io import File
import re

class JS7JobArguments:
    """Job arguments definition."""
    
    def __init__(self):
        self.declared_path_with_python_default = js7.JobArgument("declared_path_with_python_default", False, Path("path.txt"))
        self.declared_path_with_python_type = js7.JobArgument("declared_path_with_python_type", False, type=Path)
        self.declared_path_with_python_default_and_type = js7.JobArgument("declared_path_with_python_default_and_type", False,Path("path.txt"), type=Path)
        
        self.declared_file_with_java_default = js7.JobArgument("declared_file_with_java_default", False, File("file.txt"))
        self.declared_file_with_java_type = js7.JobArgument("declared_file_with_java_type", False, type=File)
        
        self.declared_string_with_python_type = js7.JobArgument("declared_string_with_python_type", False, type=str)
        self.declared_boolean_with_python_type = js7.JobArgument("declared_boolean_with_python_type", False, type=bool)
        self.declared_int_with_python_type = js7.JobArgument("declared_int_with_python_type", False, type=int)
        self.declared_int_with_python_type_and_default_string = js7.JobArgument("declared_int_with_python_type_and_default_string", False, "123", type=int)
        self.declared_float_with_python_type = js7.JobArgument("declared_float_with_python_type", False, type=float)
        self.declared_list_with_python_type = js7.JobArgument("declared_list_with_python_type", False, type=list)
        self.declared_map_with_python_type = js7.JobArgument("declared_map_with_python_type", False, type=dict)
        self.declared_set_with_python_type = js7.JobArgument("declared_set_with_python_type", False, type=set)
        
        
        #self.my_arg3 = js7.JobArgument("my_arg3", True, "my_arg3 default", js7.DisplayMode.UNMASKED)
        #self.my_arg5 = js7.JobArgument("op_arg_string", False)
        #self.my_arg6 = js7.JobArgument("op_arg_numeric_1", False, 1)
        #self.my_arg7 = js7.JobArgument("op_arg_numeric_2", False, 1.23)
        #self.my_arg8 = js7.JobArgument("op_arg_boolean", False)
        #self.my_arg9 = js7.JobArgument("op_arg_list", False)
        #self.my_arg10 = js7.JobArgument("op_arg_map", False)
  
        #self.includedArguments = [js7.IncludableArgument.CREDENTIAL_STORE, js7.IncludableArgument.SSH_PROVIDER]
        
        # dev tests - not for use
        #self.my_arg9 = js7.JobArgument("op_arg_list_", False, ())
        #self.my_arg10 = js7.JobArgument("op_arg_map_", False, {})
        #self.my_arg11 = js7.JobArgument("op_arg_map_with_default", False, {'a':'b'})
        #self.my_arg12 = js7.JobArgument("op_arg_set_with_default", False, {'a','b'})
        
  
class JS7Job(js7.Job):
    """Job definition."""
    
    def __init__(self):
        self.declaredArguments = JS7JobArguments()
        
        self.hello_from_python="Hello from Python Job" 
    
    def processOrder(self, js7Step):
        js7Step.getLogger().info(f"[onOrderProcess][self.hello_from_python]{self.hello_from_python}")
        js7Step.getLogger().info(f"[onOrderProcess][getJobEnvironment]{self.getJobEnvironment()}")
        js7Step.getLogger().info(f"[onOrderProcess][getJobEnvironment.getSystemEncoding]{self.getJobEnvironment().getSystemEncoding()}")
       
        
        args = js7Step.getAllDeclaredArguments()
        for arg in args:
            v = arg.getValue()
            js7Step.getLogger().info(f"  {arg.getName()}={v}={type(v).__module__}.{type(v).__name__}")
            if v is None:
                js7Step.getLogger().info("      Python None")
            elif isinstance(v, list):
                js7Step.getLogger().info("      Python List")
            elif isinstance(v, dict):
                js7Step.getLogger().info("      Python Dict")
        
        
        arg = js7Step.getAllArguments().get("my_arg_map");
        if arg is not None:
            v = arg.getValue()  
            js7Step.getLogger().info(f"MAP ARG  {arg.getName()}={v}={type(v).__module__}.{type(v).__name__}")
            
            if isinstance(v, dict):
                js7Step.getLogger().info("      Python Dict")
        
        
        js7Step.getOutcome().setReturnCode(100)
        js7Step.getOutcome().putVariable("var_1", "var_1_value")
        #js7Step.getOutcome().setFailed()

