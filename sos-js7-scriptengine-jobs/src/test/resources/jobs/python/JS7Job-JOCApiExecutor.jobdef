import java
ApiExecutor = java.type("com.sos.js7.job.jocapi.ApiExecutor")

class JS7Job(js7.Job):
    
    def processOrder(self, js7Step):
        api_executor = ApiExecutor(js7Step)
        access_token = None
        
        try:
            access_token = api_executor.login().getAccessToken()
            js7Step.getLogger().info(f"[access_token] {access_token}")

            controller_id = js7Step.getControllerId()
            body = f'{{"controllerId":"{controller_id}"}}'
            response = api_executor.post(access_token, "/monitoring/controllers", body)

            js7Step.getLogger().info(f"[response.getResponseBody] {response.getResponseBody()}")
       
        finally:
            # 1) with logout exception handling
            #if access_token is not None:
            #    try:
            #        api_executor.logout(access_token)
            #    except Exception as e:
            #        js7Step.getLogger().error(f"[logout failed] {e}")
            #api_executor.close()
            
            # 2) a possible logout exception is not thrown, but logged internally at error level    
            api_executor.closeQuietly(access_token)
      