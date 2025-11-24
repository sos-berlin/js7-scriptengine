class JS7JobArguments {
	my_arg1 = new js7.JobArgument("my_arg1", false, new java.io.File("x.txt"));
	my_arg2 = new js7.JobArgument("my_arg2", true, "x", js7.DisplayMode.UNMASKED);
	my_arg3 = new js7.JobArgument("op_arg_final", false);
	my_arg4 = new js7.JobArgument("op_arg_string", false);
	my_arg5 = new js7.JobArgument("op_arg_numeric", false);
	my_arg6 = new js7.JobArgument("op_arg_boolean", false);
	my_arg7 = new js7.JobArgument("op_arg_list", false);

	//includedArguments = [js7.IncludableArgument.CREDENTIAL_STORE, js7.IncludableArgument.SSH_PROVIDER];
}

class JS7Job extends js7.Job {
	declaredArguments = new JS7JobArguments();

	processOrder(js7Step) {
		js7Step.getLogger().info("[onOrderProcess]Hallo from My Job");
		js7Step.getLogger().info("[onOrderProcess][getJobEnvironment]" + this.getJobEnvironment());
		js7Step.getLogger().info("[onOrderProcess][getJobEnvironment.getSystemEncoding]" + this.getJobEnvironment().getSystemEncoding());

		//java.lang.Thread.sleep(5*1000);
		var da = js7Step.getDeclaredArgument(this.declaredArguments.my_arg2.name);
		js7Step.getLogger().info("[onOrderProcess][declaredArgument=" + da.getName() + "]" + da.getValue());
		js7Step.getLogger().info("[onOrderProcess][declaredArgumentValue]" + (typeof js7Step.getDeclaredArgumentValue(this.declaredArguments.my_arg2.name)));

		js7Step.getOutcome().setReturnCode(100);
		js7Step.getOutcome().putVariable("var_1", "var_1_value");
		//js7Step.getOutcome().setFailed();
	}
}

