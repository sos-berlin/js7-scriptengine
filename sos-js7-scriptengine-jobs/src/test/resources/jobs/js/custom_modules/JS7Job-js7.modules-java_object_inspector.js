// See ../ScriptJobOptions.json 
// Path "js.commonjs-require-cwd": "src/test/resources/modules/js" + ...
const JS7JavaObjectInspector = require("./modules/JS7JavaObjectInspector");

class JS7Job extends js7.Job {

	processOrder(js7Step) {
		// 1) JavaScript methods for checking whether a module can be loaded
		//js7Step.getLogger().info(require.cwd()); // require.cwd() - only for NODE.js
		js7Step.getLogger().info("[require]" + require); // output should be: function require() { [native code] }
		js7Step.getLogger().info("[require.resolve(...)]" + require.resolve("./modules/JS7JavaObjectInspector"));

		// 2) Main part -  Module usage
		var inspector = new JS7JavaObjectInspector();
		inspector.logPublicMethods(js7Step.getLogger(), "this.getJobEnvironment()", this.getJobEnvironment());
		inspector.logPublicMethods(js7Step.getLogger(), "js7Step", js7Step);

		inspector.logArguments(js7Step);
	}
}
