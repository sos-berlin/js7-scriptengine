#  ManagedProcess - see script_includes/Python-ManagedProcess.py


class JS7Job(js7.Job):

    def processOrder(self, js7Step):
        python_executable = js7Step.getAllArgumentsAsNameValueMap().get("python_executable")
        python_app = js7Step.getAllArgumentsAsNameValueMap().get("python_app")
        
        #######################################################################################################
        # Step 1 - Pre-Processing - calculate python_app arguments       
        python_app_args = ["js7Step_argument_1", "js7Step_argument_2"]  # calculation result, here hard-coded

        js7Step.getLogger().info(f"[ManagedProcess]run...")
        #######################################################################################################
        # Step 2 - Processing - call python_app      
        # "-u" flag: forces the Python subprocess to run in unbuffered mode,
        #    so stdout and stderr are flushed immediately, enabling real-time logging.
        #    Note: this flag only has an effect when running Python scripts; it is ignored or may cause errors for other programs.
        returncode = ManagedProcess.run(js7Step, [python_executable, "-u", python_app] + python_app_args)      
        #######################################################################################################
        # Step 3 - Post-Processing - evaluate return code/stderr, etc. to set the js7Step outcome: js7Step.ge      
        js7Step.getLogger().info(f"[ManagedProcess]returncode={returncode}")
        js7Step.getOutcome().setReturnCode(returncode)
        js7Step.getOutcome().putVariable("outcome_var_name", "outcome_var_value")
        
    def onProcessOrderCanceled(self, js7Step):
         ManagedProcess.cancel(js7Step)
