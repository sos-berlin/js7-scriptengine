# variant 1 - one line - direct use if no extension is needed
#from js7.modules.js7_job_hello_world import JS7Job

# variant 2 - extend the imported job if customization is needed
from js7.modules.js7_job_hello_world import JS7Job as HelloWorldJob
#class JS7Job(HelloWorldJob):
#    pass 	# 'pass' is required because the class body cannot be empty.
			# New methods can be added here, or existing ones from HelloWorldJob can be overridden.  

class JS7Job(HelloWorldJob):
	def processOrder(self, js7Step):
		super().processOrder(js7Step)
		js7Step.getLogger().info("Additional custom logic")             