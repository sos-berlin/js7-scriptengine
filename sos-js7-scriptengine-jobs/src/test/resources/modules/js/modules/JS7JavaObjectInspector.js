class JS7JavaObjectInspector {
	regExp = new RegExp("equals|toString|hashCode|getClass|notify|notifyAll|wait");

	constructor() { }

	logPublicMethods(logger, title, o) {
		logger.info("---------------Public Methods " + title + "--");
		var pm = com.sos.commons.util.SOSReflection.getAllMethods(o.getClass());
		for (var i in pm) {
			var m = pm[i];
			if (this.regExp.test(m.getName())) {
				continue;
			}
			logger.info(" " + m);
		}
	}

	logArguments(js7Step) {
		// map
		js7Step.getLogger().info("---------------All Arguments--");
		var args = js7Step.getAllArguments();
		js7Step.getLogger().info("getAllArguments:");
		for (var a in args) {
			js7Step.getLogger().info(" " + a + "=" + args[a]);
		}

		// object
		js7Step.getLogger().info("---------------Declared Arguments--");
		args = js7Step.getDeclaredArguments();
		js7Step.getLogger().info("getDeclaredArguments: " + args);

		// list
		args = js7Step.getAllDeclaredArguments();
		js7Step.getLogger().info("getAllDeclaredArguments:");
		for (var a in args) {
			js7Step.getLogger().info(" " + args[a]);
		}
	}
}

// module.exports enables the creation of an instance: e.g., var inspector = new JS7JavaObjectInspector();
module.exports = JS7JavaObjectInspector;