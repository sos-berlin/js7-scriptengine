import java
SQLExecutorJob = java.type("com.sos.jitl.jobs.db.SQLExecutorJob")

class JS7Job(js7.Job):
   
    def processOrder(self, js7Step):
        js7Step.getLogger().info("call SQLExecutorJob");
        js7Step.executeJob(SQLExecutorJob);
       