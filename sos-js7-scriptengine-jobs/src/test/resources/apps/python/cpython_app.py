import sys, os, time
import subprocess

def check_install(package_name: str):
    print("Check if a package is installed; if not, install it via pip:-------------------------------")
    try:
        __import__(package_name)
        print(f"    [{package_name}]is already installed.")
    except ImportError:
        print(f"    [{package_name}]not found. Installing...: {sys.executable} -m pip install {package_name}")
        subprocess.check_call([sys.executable, "-m", "pip", "install", package_name])


def print_args():
    # sys.argv[0] the Script-Name
    args = sys.argv[1:]

    if not args:
        print("No Arguments")
        return

    print("Arguments:-------------------------------")
    for i, arg in enumerate(args, start=1):
        print(f"    [Arguments]{i}: {arg}")
        
def print_envs():
    print("ENV variables:-------------------------------")
    # print all environment variables safely on Windows, handling Unicode characters despite the default CP1252 encoding.
    # e.g. for MY_ENV_VAR=スクリーンショット
    sys.stdout.reconfigure(encoding='utf-8')
    #sys.stderr.reconfigure(encoding='utf-8')
    for env_name, env_value in os.environ.items():
        print(f"    [ENV]{env_name}={env_value}")


def test_mysql_connector():
    check_install("mysql.connector")

    import mysql.connector
    print("    MySQL connect...-------------------------------")
    conn = mysql.connector.connect(
        host="localhost", 
        user="root",
        passwd="root", 
        database="js7_x"
    )
    print("    MySQL connected")

    cursor = conn.cursor()
    cursor.execute("SELECT * FROM JOC_VARIABLES")
    rows = cursor.fetchall()

    column_names = [i[0] for i in cursor.description]
    print("    Columns:", column_names)

    print("    Rows:")
    for row in rows:
        print(f"        {row}")

    cursor.close()
    conn.close()

    print("    MySQL disconnected")
    
def test_numpy():
    check_install("numpy")

    import numpy as np
    print(f"    [numpy]{np.__version__}")

def execute_sleep():
    time_sleep=10
    print(f"[Sleep]{time_sleep} seconds. cancel me...-------------------------------")
    time.sleep(time_sleep)

def main():
    
    print_args()    
    print_envs()
    
    #check_install("xxx")
    test_mysql_connector()
    test_numpy()
        
    execute_sleep()
        

if __name__ == "__main__":
    main()