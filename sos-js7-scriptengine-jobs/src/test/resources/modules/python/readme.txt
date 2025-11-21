###################################################################################################################
Main Approach
###################################################################################################################

 	1) Write your own modules
 	2) Configure a Python job to use the "python.PythonPath" option pointing to the path containing these modules


###################################################################################################################
Writing Python Jobs as Modules
	Important: 
			Examples here are demonstrations only - this approach is fundamentally possible, 
			but not yet mature enough for productive use
			
			Drawbacks:
				The PythonJob.jobdef script must be duplicated as a .py file to expose it as a Python module
				Changes in the original .jobdef must be manually updated in the .py file
				Currently, the PythonJob is loaded twice (once from .jobdef, once as the module)
###################################################################################################################
  	Problem
  		When defining 
  			------------------------------------------------------------------
  			class JS7Job(js7.Job):
  			------------------------------------------------------------------
  				an error occurs because js7.Job is unknown, i.e., "js7" is an unknown name when using modules.
  	
  	Workaround
  		Make a copy of src/resources/PythonJob.jobdef as a PythonJob.py file to expose it as a Python module
  		
    Example
    	1) Create the job as a module (e.g., js7_job_hello_world.py file)
    		------------------------------------------------------------------
    		from js7.PythonJob import js7

			class JS7Job(js7.Job):
	
				def processOrder(self, js7Step):
					js7Step.getLogger().info("hello world")
					# do some stuff	
			------------------------------------------------------------------
    		
		2) Usage in JS7
			1) one line - direct use if no extension is needed
			------------------------------------------------------------------    		
			from js7.modules.js7_job_hello_world import JS7Job
    	    ------------------------------------------------------------------
    	    
    	    or 
    	    
    	    2) new methods can be added here, or existing ones from HelloWorldJob can be overridden
    	    ------------------------------------------------------------------  		
		    from js7.modules.js7_job_hello_world import JS7Job as HelloWorldJob
			
			class JS7Job(HelloWorldJob):
				def processOrder(self, js7Step):
					super().processOrder(js7Step)
		        	js7Step.getLogger().info("Additional custom logic")        
    	    ------------------------------------------------------------------    		
			
    		  
			
