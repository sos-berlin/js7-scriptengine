class JS7Job(js7.Job):
    
    def processOrder(self, js7Step):
        logger = js7Step.getLogger();
        
        # String to command list
        # https://docs.python.org/3/library/shlex.html
        import shlex
        commands = js7Step.getAllArgumentsAsNameValueMap().get("commands")    
        if commands:
            logger.info("Commands ...")
            for command_as_string in commands:
                logger.info(f"    [command_as_string]{command_as_string}")
                logger.info(f"        [command_as_string.split]{command_as_string.split()}")
                logger.info(f"        [shlex.split]{shlex.split(command_as_string)}")
        