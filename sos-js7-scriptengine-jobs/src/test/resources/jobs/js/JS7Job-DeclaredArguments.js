class JS7JobArguments {
	declared_file_with_java_default = new js7.JobArgument("declared_file_with_java_default", false, new java.io.File("x.txt"))
	declared_file_with_java_type = new js7.JobArgument("declared_file_with_java_type", false, null, js7.DisplayMode.UNMASKED, Java.type("java.io.File"))

	declared_string_with_javascript_type = new js7.JobArgument("declared_string_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, String)
	declared_boolean_with_javascript_type = new js7.JobArgument("declared_boolean_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, Boolean)

	declared_number_with_javascript_type = new js7.JobArgument("declared_number_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, Number)
	declared_number_with_javascript_type_and_default_string = new js7.JobArgument("declared_number_with_javascript_type_and_default_string", false, "123", js7.DisplayMode.UNMASKED, Number)

	// JavaScript - only Numeric is enabled - no Integer/Double types..
	declared_double_with_java_type = new js7.JobArgument("declared_double_with_java_type", false, null, js7.DisplayMode.UNMASKED, Java.type("java.lang.Double"))
	declared_list_with_javascript_type = new js7.JobArgument("declared_list_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, Array)
	declared_map_with_javascript_type = new js7.JobArgument("declared_map_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, Map)
	declared_set_with_javascript_type = new js7.JobArgument("declared_set_with_javascript_type", false, null, js7.DisplayMode.UNMASKED, Set)

	//my_arg1 = new js7.JobArgument("my_arg1", false, new java.io.File("x.txt"));
	//my_arg2 = new js7.JobArgument("my_arg2", true, "x", js7.DisplayMode.UNMASKED);
	//my_arg3 = new js7.JobArgument("op_arg_final", false);
	//my_arg4 = new js7.JobArgument("op_arg_string", false);
	//my_arg5 = new js7.JobArgument("op_arg_numeric", false);
	//my_arg6 = new js7.JobArgument("op_arg_boolean", false);
	//my_arg7 = new js7.JobArgument("op_arg_list", false);

	//includedArguments = [js7.IncludableArgument.CREDENTIAL_STORE, js7.IncludableArgument.SSH_PROVIDER];

}

class JS7Job extends js7.Job {
	declaredArguments = new JS7JobArguments();

	processOrder(js7Step) {
		js7Step.getLogger().info("[onOrderProcess]Hallo from My Job");
		js7Step.getLogger().info("[onOrderProcess][getJobEnvironment]" + this.getJobEnvironment());
		js7Step.getLogger().info("[onOrderProcess][getJobEnvironment.getSystemEncoding]" + this.getJobEnvironment().getSystemEncoding());

		//java.lang.Thread.sleep(5*1000);

		js7Step.getOutcome().setReturnCode(100);
		js7Step.getOutcome().putVariable("var_1", "var_1_value");
		//js7Step.getOutcome().setFailed();
	}
}

