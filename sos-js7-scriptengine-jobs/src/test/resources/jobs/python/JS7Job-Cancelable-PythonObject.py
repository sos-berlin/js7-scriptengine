import java
TimeUnit = java.type("java.util.concurrent.TimeUnit");

class MyCancelableObject():
    def __init__(self, logger):
        self.logger = logger
        self.state = "init"
    
    def cancel(self):
        self.logger.info(f"[{self.state}]MyCancelableObject cancel...")
              
   
class JS7Job(js7.Job):
    #def __init__(self):
    #    super().__init__()
                
    def processOrder(self, js7Step):
        my_cancelable_object = MyCancelableObject(js7Step.getLogger())
        #######################################################
        js7Step.setCancelableResource(my_cancelable_object)
        #######################################################
        
        my_cancelable_object.state = "process"
        
        js7Step.getLogger().info("sleep 10 seconds. cancel me ...")
        TimeUnit.SECONDS.sleep(10);
        
        
    def onProcessOrderCanceled(self, js7Step):
        #######################################################
        cancelable_object = js7Step.getCancelableResource()
        #######################################################
        
        js7Step.getLogger().info(f"onProcessOrderCanceled={cancelable_object}")
        if cancelable_object is not None:
            cancelable_object.cancel()
