from aassist.dbutils import MySQLdb
from aassist.uniq_token import gen_token
import aassist.banks.ifsc as IFSC
import time
import aassist.parallel as paracomp

parapool = paracomp.ParallelCompute()


api_bearer = {
    "BANK_NAME" : "AXIS BANK",
    "DB_NAME" : "axis"
}

def LoginUser(user,passwd):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc;
    parapool.assignnew(mysqlc_p.handle.execute , ("SELECT passwd FROM users WHERE username = %s",(user),)).get()
    apasswd = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
    if apasswd[0] == passwd:
        safe_token = gen_token(user+str(time.time()))
        add_token = "UPDATE users SET token = %s WHERE username = %s"
        try:
            parapool.assignnew(mysqlc_p.handle.execute,(add_token,(safe_token,user),)).get()
            parapool.assignnew_nowait(mysqlc.commit, ())
            parapool.assignnew_nowait(mysqlc.close, ())
            return safe_token
        except:
            parapool.assignnew_nowait(mysqlc.rollback(), ())
            parapool.assignnew_nowait(mysqlc.close, ())
            return -1
    else:
        parapool.assignnew_nowait(mysqlc.close, ())
        return -1

def CheckBalance(user,token,accno):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc;
    parapool.assignnew(mysqlc_p.handle.execute , ("SELECT token , cifno FROM users WHERE username = %s",(user),)).get()
    udata = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
    if  udata[0] == token:
        parapool.assignnew(mysqlc_p.handle.execute,("SELECT balance FROM accounts WHERE accno = %s AND cifno = %s",(accno,udata[1]),)).get()
        balance = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
        parapool.assignnew_nowait(mysqlc.close,())
        return balance[0]
    else:
        parapool.assignnew_nowait(mysqlc.close, ())
        return -1

def FetchAccounts(user,token):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc
    parapool.assignnew(mysqlc_p.handle.execute,("SELECT token , cifno FROM users WHERE username = %s",(user),)).get()
    udata = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
    if udata[0] == token:
        parapool.assignnew(mysqlc_p.handle.execute,
                           ("SELECT accno FROM accounts WHERE cifno = %s",(udata[1]),)).get()
        acclist = parapool.assignnew(mysqlc_p.handle.fetchall,()).get()
        parapool.assignnew_nowait(mysqlc.close,())
        return acclist
    parapool.assignnew_nowait(mysqlc.close, ())
    return -1

def FetchAccountsWithIFSC(user,token):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc
    parapool.assignnew(mysqlc_p.handle.execute,("SELECT token , cifno FROM users WHERE username = %s",(user),)).get()
    udata = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
    if udata != None and udata[0] == token:
        parapool.assignnew(mysqlc_p.handle.execute,("SELECT accno , ifsc FROM accounts WHERE cifno = %s",(udata[1]),)).get()
        acclist = parapool.assignnew(mysqlc_p.handle.fetchall,()).get()
        parapool.assignnew_nowait(mysqlc.close, ())
        return acclist
    parapool.assignnew_nowait(mysqlc.close, ())
    return -1
def AXISCredit(srcacc,srcbank,accno,amount):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc
    succ_code = False
    try:
        parapool.assignnew(mysqlc_p.handle.execute , ("UPDATE accounts SET balance = balance + %s WHERE accno = %s",(amount,accno),)).get()
        parapool.assignnew_nowait(mysqlc.commit, ())
        succ_code = True
    except:
        parapool.assignnew_nowait(mysqlc.rollback, ())
        succ_code = False
    finally:
        parapool.assignnew_nowait(mysqlc.close, ())
    return succ_code

def AXISDebit(accno,amount,credacc,credbank):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc
    succ_code = False
    try:
        parapool.assignnew(mysqlc_p.handle.execute , ("UPDATE accounts SET balance = balance - %s WHERE accno = %s",(int(amount),accno),)).get()
        parapool.assignnew_nowait(mysqlc.commit, ())
        succ_code = True
    except:
        parapool.assignnew_nowait(mysqlc.rollback, ())
        succ_code = False
    finally:
        parapool.assignnew_nowait(mysqlc.close, ())
    return succ_code

def NEFT_RTGS(user,token,accno,ifsc,benefacc,amount,dest):
    mysqlc_p = MySQLdb(api_bearer["DB_NAME"])
    mysqlc = mysqlc_p.mysqlc
    parapool.assignnew(mysqlc_p.handle.execute,("SELECT token , cifno FROM users WHERE username = %s",(user),)).get()
    udata = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
    if udata != None and udata[0] == token:
        parapool.assignnew(mysqlc_p.handle.execute, ("SELECT balance FROM accounts WHERE accno = %s AND cifno = %s",(accno,udata[1]),)).get()
        balance = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
        if dest == "INTRA_BANK":
            response_code = [-1]
            if  int(balance[0]) >= int(amount):
                if AXISDebit(accno, amount, benefacc,"axis"):
                    if AXISCredit(accno, "axis", benefacc, amount):
                        parapool.assignnew(mysqlc_p.handle.execute , ("SELECT balance FROM accounts WHERE accno = %s AND cifno = %s",(accno,udata[1]),)).get()
                        curr_bal = parapool.assignnew(mysqlc_p.handle.fetchone , ()).get()
                        parapool.assignnew_nowait(mysqlc.close,())
                        response_code = [curr_bal[0]]
            else:
                parapool.assignnew_nowait(mysqlc.close, ())
                return [1,balance[0]]
            return response_code
        elif dest == "INTER_BANK":
            bcode = IFSC.map(ifsc)
            if int(balance[0]) >= int(amount):
                if bcode == "axis":
                    from aassist.banks.sbi.apihandlers import SBICredit
                    response_code = [-1]
                    if AXISDebit(accno, amount, benefacc,bcode):
                        if SBICredit(accno,"sbi",benefacc,amount):
                            parapool.assignnew(mysqlc_p.handle.execute,("SELECT balance FROM accounts WHERE accno = %s AND cifno = %s",(accno,udata[1]),)).get()
                            curr_bal = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
                            parapool.assignnew_nowait(mysqlc.close,())
                            response_code = curr_bal[0]
                    return response_code
                else :
                    return [-1]
            else:
                parapool.assignnew_nowait(mysqlc.close, ())
                return [1,balance[0]]
    else:
        parapool.assignnew_nowait(mysqlc.close, ())
        return -1

