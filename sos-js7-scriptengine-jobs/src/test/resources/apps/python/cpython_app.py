import sys
import mysql.connector
import time

def main():
    # sys.argv[0] der Script-Name
    args = sys.argv[1:]

    if not args:
        print("No Arguments")
        return

    print("Arguments:")
    for i, arg in enumerate(args, start=1):
        print(f"{i}: {arg}")
        
    
    print("MySQL connect...")
    conn = mysql.connector.connect(
        host="localhost", 
        user="root",
        passwd="root", 
        database="js7_x"
    )
    print("MySQL connected")

    cursor = conn.cursor()
    cursor.execute("SELECT * FROM JOC_VARIABLES")
    rows = cursor.fetchall()

    column_names = [i[0] for i in cursor.description]
    print("Columns:", column_names)

    print("Rows:")
    for row in rows:
        print(row)

    cursor.close()
    conn.close()

    print("MySQL disconnected")
    
    time_sleep=10
    print(f"sleep {time_sleep} seconds. cancel me...")
    # process.terminate()
    # process.kill()
    time.sleep(time_sleep)
        

if __name__ == "__main__":
    main()