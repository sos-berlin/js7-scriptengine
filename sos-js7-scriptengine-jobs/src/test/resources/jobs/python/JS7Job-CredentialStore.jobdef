class JS7JobArguments:
    """Job arguments definition. Include credential store arguments."""
    
    def __init__(self):
        self.includedArguments = [js7.IncludableArgument.CREDENTIAL_STORE]

class JS7Job(js7.Job):
    """Job with IncludableArgument."""
     
    def __init__(self):
        self.declaredArguments = JS7JobArguments()
  
    def processOrder(self, js7Step):
        csa = js7Step.getIncludedArguments(js7.IncludableArgument.CREDENTIAL_STORE)

        if csa.getFile() is not None and csa.getFile().getValue() is not None:
            resolver = csa.newResolver()
            title = resolver.resolve("cs://@title")
            url = resolver.resolve("cs://@url")
            js7Step.getLogger().info(f"title={title}, url={url}")

class JS7JobWithoutIncludedArguments(js7.Job):
    """Job without IncludableArgument."""
   
    def processOrder(self, js7Step):
        csa = CredentialStoreArguments()
        csa.setFile("database.kdbx")
        csa.setPassword("password")
        csa.setEntryPath("/server/SFTP/localhost")
        # csa.setKeyFile("database.key")  # optional

        resolver = csa.newResolver()
        title = resolver.resolve("cs://@title")
        url = resolver.resolve("cs://@url")
        js7Step.getLogger().info(f"title={title}, url={url}")
