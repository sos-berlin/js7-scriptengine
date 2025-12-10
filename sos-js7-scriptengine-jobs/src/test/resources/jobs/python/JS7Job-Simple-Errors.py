#import non_existing_module # Polyglot GuestException

class JS7Job(js7.Job):
    
    def processOrder(self, js7Step):
        print("hello world")
        
        # Polyglot GuestException: ZeroDivisionError: division by zero
        #x = 1 / 0  
        
        # Syntax error with details
        #print("hello world")details...
        
        # Syntax error with details
        print("hello world"))        
                
        # Syntax error without details
        #a= 
 
        # Polyglot GuestException: NameError("name 'b' is not defined")
        #a=b