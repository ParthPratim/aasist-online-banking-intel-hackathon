from .dbutils import MySQLdb
import aassist.parallel as paracomp

parapool = paracomp.ParallelCompute()

def ValidateToken(email,token):
    mysqlc_p = MySQLdb()
    mysqlc = mysqlc_p.mysqlc
    if mysqlc == -1:
        return -1
    else:
        parapool.assignnew(mysqlc_p.handle.execute , ("SELECT token , id FROM users WHERE email = %s",(email),)).get()
        result = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
        parapool.assignnew_nowait(mysqlc.close,())
        if result[0] != token:
            return -1
        else:
            return result
