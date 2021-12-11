import pymysql as mysql
import yaml, os
import aassist.parallel as thpool

class MySQLdb:
    def connect(self,database=None):
        try:
            with open(os.getcwd()+"/aassist/config.yml", "r") as config:
                cfg = yaml.safe_load(config)
            mysql_cfg = cfg['mysql']
            if database == None:
                database = mysql_cfg["db"]
            connection = mysql.connect(mysql_cfg["host"], mysql_cfg["user"], mysql_cfg["passwd"],database)
            self.mysqlc = connection
            self.handle = connection.cursor()
            return connection
        except mysql.err.OperationalError as err:
            if err.args[0] == 1045:
                print("Failed Connection to Mysql Database")
                return -1
    def __init__(self,database=None):
        self.connect(database)
