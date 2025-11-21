import java
import re

class JS7JavaObjectInspector:
    """JS7 Java Object Inspector"""
    
    def __init__(self):
        self.regExp = re.compile(r"equals|toString|hashCode|getClass|notify|notifyAll|wait|^__.*")

    def log_public_methods(self, logger, title, o):
        logger.info(f"---------------Public Methods {title}--")
        pm = java.type("com.sos.commons.util.SOSReflection").getAllMethods(o.getClass())
        for m in pm:
            # m.getName() ist der Methodenname
            if self.regExp.search(m.getName()):
                continue
            logger.info(f" {m}")  # m.toString() wird automatisch aufgerufen

    def log_arguments(self, js7Step):
        logger = js7Step.getLogger()
        
        #dict
        logger.info("---------------All Arguments--")
        args = js7Step.getAllArguments()
        logger.info("getAllArguments:")
        for k, v in args.items():
            logger.info(f" {k}={v}")

	    # object
        logger.info("---------------Declared Arguments--")
        args = js7Step.getDeclaredArguments()
        logger.info(f"getDeclaredArguments: {args}")
        
        # list
        args = js7Step.getAllDeclaredArguments()
        logger.info("getAllDeclaredArguments:")
        for v in args:
            logger.info(f" {v}")
