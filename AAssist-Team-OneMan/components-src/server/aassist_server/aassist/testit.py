import aassist.dbutils as db
import sys, os

def testit():
    db.MySQLdb().connect()
    print(os.getcwd())

