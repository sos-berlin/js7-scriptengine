from js7.PythonJob import js7

class JS7Job(js7.Job):
	
	def processOrder(self, js7Step):
		js7Step.getLogger().info("hello world")
		# do some stuff