class JS7Job extends js7.Job {

	processOrder(js7Step) {
		var sql = "SELECT SLEEP(15)";

		var factory = null;
		var session = null;
		try {
			factory = new com.sos.commons.hibernate.SOSHibernateFactory("src/test/resources/hibernate.cfg.xml");
			factory.build();

			session = factory.openStatelessSession();
			js7Step.setCancelableResource(session)

			session.getSQLExecutor().execute(sql);
		}
		finally {
			if (factory) {
				factory.close(session);
			}
		}
	}

	onProcessOrderCanceled(js7Step) {
		this.cancelHibernate(js7Step, js7Step.getCancelableResource());
	}

}

