import java, re, polyglot


class JS7JavaObjectInspector:
    """JS7 Java Object Inspector"""
    
    def __init__(self):
        self.regExp = re.compile(r"equals|toString|hashCode|getClass|notify|notifyAll|wait|^__.*")

    def log_public_methods(self, logger, title, obj):
        logger.info(f"--------------- Public Methods {title} ---------------")
        pm = java.type("com.sos.commons.util.SOSReflection").getAllMethods(obj.getClass())
        pm = sorted(pm, key=lambda m: m.getName())
        for m in pm:
            if self.regExp.search(m.getName()):
                continue
            logger.info(f" {m.toGenericString()}")

    def log_arguments(self, js7Step):
        logger = js7Step.getLogger()
        
        # Python Dict
        logger.info("--------------- All Arguments ---------------")
        args = js7Step.getAllArguments()
        logger.info(f"js7Step.getAllArguments() : {self.describe_object(args)}")
        for name, arg in args.items():
            logger.info(f" {name}={arg}")
            logger.info(f"     {name}.getValue() Type={self.describe_object(arg.getValue(), True)}")

	    # Java Object
        logger.info("--------------- Declared Arguments ---------------")
        args = js7Step.getDeclaredArguments()
        logger.info(f"js7Step.getDeclaredArguments() : {args} {self.describe_object(args)}")
        
        # Python List
        args = js7Step.getAllDeclaredArguments()
        logger.info(f"js7Step.getAllDeclaredArguments() : {self.describe_object(args)}")
        for arg in args:
            logger.info(f" {arg}")
            logger.info(f"     {arg.getName()}.getValue() Type={self.describe_object(arg.getValue(), True)}")

    def describe_object(self, obj, inspectIfCollection=False):
        """Return a readable string describing obj (type + optional content) for logging."""
        
        if obj is None:
            return "[Python]None"
        
        if isinstance(obj, list):
            if not inspectIfCollection:
                return "[Python]List"

            lines = ["[Python]List:"]
            for v in obj:
                vdesc = self.describe_object(v, inspectIfCollection=False)
                lines.append(f"    List.Value({vdesc}) = {v}")
            return "\n".join(lines)
        
        if isinstance(obj, dict):
            if not inspectIfCollection:
                return "[Python]Dict"
            
            lines = ["[Python]Dict:"]
            for k, v in obj.items():
                kdesc = self.describe_object(k, inspectIfCollection=False)
                vdesc = self.describe_object(v, inspectIfCollection=False)
                lines.append(f"    Dict.Key({kdesc}) = {k}")
                lines.append(f"        Dict.Value({vdesc}) = {v}")
            return "\n".join(lines)
        
        if isinstance(obj, polyglot.ForeignObject):
            try:
                cls = obj.getClass().getName()
                return f"[Java]{cls}"
            except:
                return "[Java]UnknownClass"
        
        t = type(obj)        
        return f"[Python]{t.__module__}.{t.__name__}"
    
    def is_java_object(self, obj):
        if obj is None:
            return False
            
        if isinstance(obj, list):
            for v in obj:
                vv = self.is_java_object(v)
                if vv is True:
                    return True
                
            return False
            
        if isinstance(obj, dict):
            for k, v in obj.items():
                kv = self.is_java_object(k)
                if kv is True:
                    return True
                vv = self.is_java_object(v)
                if vv is True:
                    return True
                
            return False
            
        return isinstance(obj, polyglot.ForeignObject)