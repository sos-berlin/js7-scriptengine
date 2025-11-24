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
			sshProvider.connect();
			js7Step.setCancelableResource(sshProvider)

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

	onProcessOrderCanceled(js7Step) {
		this.cancelSSHProvider(js7Step, js7Step.getCancelableResource());
	}
}
