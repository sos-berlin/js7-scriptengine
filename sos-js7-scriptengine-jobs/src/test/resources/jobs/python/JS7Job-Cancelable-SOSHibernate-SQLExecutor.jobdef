import java
SOSHibernateFactory = java.type("com.sos.commons.hibernate.SOSHibernateFactory")

class JS7Job(js7.Job):
   
    def processOrder(self, js7Step):
        sql = "SELECT SLEEP(15)"

        factory = None
        session = None
        try :
            factory = SOSHibernateFactory("config/hibernate.cfg.xml")
            factory.build()
            
            session = factory.openStatelessSession()
            #######################################################
            js7Step.setCancelableResource(session)
            #######################################################

            js7Step.getLogger().info(f"[{sql}]execute ...")
            #######################################################
            # session.getResultListNativeQuery(sql)
            #######################################################
            session.getSQLExecutor().execute(sql)
            
        finally:
            factory and factory.close(session)
    
    ###########################################################################################
    # If 'onProcessOrderCanceled' is implemented, the statements should be executed using
    # 'session.getSQLExecutor()'
    # rather than session methods like 'session.getResultListNativeQuery(sql)',
    # because 'getSQLExecutor()' uses a JDBC Statement that can be cancelled, whereas the
    # Hibernate API does not provide a way to cancel queries executed via its standard methods.
    ###########################################################################################
    def onProcessOrderCanceled(self, js7Step):
        # super() or self
        self.cancelHibernate(js7Step, js7Step.getCancelableResource())
        