class JS7Job extends js7.Job {

	processOrder(js7Step) {
		var apiExecutor = new com.sos.js7.job.jocapi.ApiExecutor(js7Step);
		var accessToken = null;
		try {
			accessToken = apiExecutor.login().getAccessToken();
			js7Step.getLogger().info("[accessToken]" + accessToken);

			var response = apiExecutor.post(accessToken, "/monitoring/controllers", '{"controllerId":"' + js7Step.getControllerId() + '"}');
			js7Step.getLogger().info("[response.getResponseBody]" + response.getResponseBody());
		}
		finally {
			//  1) with logout exception handling
			//if (accessToken) {
			//	try {
			//		apiExecutor.logout(accessToken);
			//	} catch (e) {
			//		js7Step.getLogger().error(`[logout failed] ${e}`);
			//	}
			//}
			//apiExecutor.close();

			// 2) a possible logout exception is not thrown, but logged internally at error level    
			apiExecutor.closeQuietly(accessToken)
		}
	}
}

