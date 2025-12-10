import sys, os, site, pkgutil

class JS7Job(js7.Job):
	
    def processOrder(self, js7Step):
		
        js7Step.getLogger().info("#### Python Base Info ##################################")
        js7Step.getLogger().info(f"[sys.version]{sys.version}")
        js7Step.getLogger().info(f"[sys.prefix (PythonHome)]{sys.prefix}")
        js7Step.getLogger().info(f"[sys.exec_prefix]{sys.exec_prefix}")
        js7Step.getLogger().info(f"[sys.base_prefix]{sys.base_prefix}")
        js7Step.getLogger().info(f"[sys.executable]{sys.executable}")
        js7Step.getLogger().info(f"[sys.path]{sys.path}")

        js7Step.getLogger().info("")
        js7Step.getLogger().info("#### Python Cache-Folder ##################################")
        js7Step.getLogger().info(f"[site.getsitepackages()]{site.getsitepackages()}")
        js7Step.getLogger().info(f"[site.getusersitepackages()]{site.getusersitepackages()}")
        
        
        try:
            import ssl
            
            js7Step.getLogger().info("")
            js7Step.getLogger().info(f"[SSL module path]{ssl.__file__}")
        except:
            pass
        
        try:
            import pip
            
            js7Step.getLogger().info("")
            js7Step.getLogger().info(f"[pip.__version__]{pip.__version__}")
        except:
            js7Step.getLogger().info("")
            js7Step.getLogger().info("[pip]NOT available")
        
        js7Step.getLogger().info("")
        js7Step.getLogger().info("#### Python Modules ##################################")
        js7Step.getLogger().info("[sys.modules]:")
        modules_items = sorted(sys.modules.items())
        for name, module in sorted(sys.modules.items()):
            js7Step.getLogger().info(f"    {name} -> {module}")
        js7Step.getLogger().info(f"  [total]{len(modules_items)}")
        
        js7Step.getLogger().info("")
        js7Step.getLogger().info("[sys.modules.keys()]:")
        modules_keys = sorted(sys.modules.keys())
        for name in modules_keys:
            js7Step.getLogger().info(f"    {name}")
        js7Step.getLogger().info(f"  [total]{len(modules_keys)}")
        
        js7Step.getLogger().info("")
        js7Step.getLogger().info("[sys.builtin_module_names]:")
        builtin_modules = sorted(sys.builtin_module_names)
        for name in builtin_modules:
            js7Step.getLogger().info(f"    {name}")
        js7Step.getLogger().info(f"  [total]{len(builtin_modules)}")
        
        js7Step.getLogger().info("")
        js7Step.getLogger().info("[pkgutil.iter_modules()]:")
        available_modules = sorted(pkgutil.iter_modules(), key=lambda m: m.name)
        for module in available_modules:
            js7Step.getLogger().info(f"    {module.name}")
        js7Step.getLogger().info(f"  [total]{len(available_modules)}")
        #print(os.listdir(sys.path[0]))
		