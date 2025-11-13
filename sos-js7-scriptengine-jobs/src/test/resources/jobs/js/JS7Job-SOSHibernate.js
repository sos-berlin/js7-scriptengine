class JS7Job extends js7.Job {

	processOrder(js7Step) {
		var sql = "select TEXT_VALUE from JOC_VARIABLES where NAME='version'";

		var factory = null;
		var session = null;
		try {
			factory = new com.sos.commons.hibernate.SOSHibernateFactory("src/test/resources/hibernate.cfg.xml");
			factory.build();

			session = factory.openStatelessSession();
			var result = session.getSingleValueNativeQuery(sql);

			js7Step.getLogger().info("[" + sql + "]" + result);
		}
		finally {
			if (factory) {
				factory.close(session);
			}
		}
	}
}

