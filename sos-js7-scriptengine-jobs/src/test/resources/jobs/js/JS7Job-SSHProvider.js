class JS7JobArguments {

	includedArguments = [js7.IncludableArgument.SSH_PROVIDER];

	// see JS7JobWithCredentialStore
	//includedArguments = [js7.IncludableArgument.SSH_PROVIDER, js7.IncludableArgument.CREDENTIAL_STORE];
}

/** without credential store arguments */
class JS7Job extends js7.Job {
	declaredArguments = new JS7JobArguments();

	processOrder(js7Step) {
		var args = js7Step.getIncludedArguments(js7.IncludableArgument.SSH_PROVIDER);
		var sshProvider = null;
		
		try {
			sshProvider = com.sos.commons.vfs.ssh.SSHProvider.createInstance(js7Step.getLogger(), args);
			//log sshProvider public methods
			//var lh = new LogHelper();
			//lh.logPublicMethods(js7Step.getLogger(), "sshProvider", sshProvider);
			sshProvider.connect();

			js7Step.getLogger().info("[sshProvider.getServerInfo]" + sshProvider.getServerInfo());
		}
		finally {
			if (sshProvider) {
				sshProvider.disconnect();
			}
		}
	}
}


/** with included credential store arguments 
 * 1 - activate includedArguments = [js7.IncludableArgument.SSH_PROVIDER, js7.IncludableArgument.CREDENTIAL_STORE]
 * 2 - rename the existing JS7Job class above to another name 
 * 3 - rename JS7JobWithIncludedCredentialStore class to JS7Job
*/
class JS7JobWithIncludedCredentialStore extends js7.Job {
	declaredArguments = new JS7JobArguments();

	processOrder(js7Step) {
		var ssha = js7Step.getIncludedArguments(js7.IncludableArgument.SSH_PROVIDER);
		var csa = js7Step.getIncludedArguments(js7.IncludableArgument.CREDENTIAL_STORE);

		var sshProvider = new com.sos.commons.vfs.ssh.SSHProvider(ssha, csa);
		sshProvider.connect();

		sshProvider.disconnect();

	}
}


class LogHelper {
	regExp = new RegExp("equals|toString|hashCode|getClass|notify|notifyAll|wait");

	logPublicMethods(logger, title, o) {
		logger.info("---------------Public Methods " + title + "--");
		var pm = com.sos.commons.util.SOSReflection.getAllMethods(o.getClass());
		for (var i in pm) {
			var m = pm[i];
			if (this.regExp.test(m.getName()) || java.lang.reflect.Modifier.isAbstract(m.getModifiers())) {
				continue;
			}
			logger.info(" " + m);
		}
	}
}