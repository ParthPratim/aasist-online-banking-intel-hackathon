import tornado.web as webserver
import tornado.httpclient
import urllib
import firebase_admin
from firebase_admin import messaging
from firebase_admin import credentials
from .dbutils import MySQLdb
from .jsonutils import Json
from aassist.banks.sbi import apihandlers as SBI
from aassist.banks.axis import apihandlers as AXIS
from .validatetoken import ValidateToken
from aassist.banks.getbank import getBank
from aassist.banks.ifsc import map
import aassist.parallel as thpool
import datetime
from datetime import timedelta
parapool = thpool.ParallelCompute() # THREAD POOL EXECUTOR HANDLE
GARBAGE_VALUE = "***GARBAGE#VALUE***"
ERROR_0000 = "All required information was not sent by you. Please retry."
ERROR_0001 = "Internal Database Connection Error. Check if database is up and running"
ERROR_0002 = 'Authentication failure. Invalid email or access token sent.'
ERROR_0003 = 'This internet banking was not registered. Please register you Internet Banking account for this bank.'


cred = credentials.Certificate("aassist/firebase/config/google-services.json")
firebase_admin.initialize_app(cred)

class AAssistMain(webserver.RequestHandler):

    @webserver.asynchronous
    def get(self):
        self.write("Index.html")
        self.finish()
        self.flush()

class RegisterBank(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        bankid = self.get_argument("bankid")
        buser = self.get_argument("buser")
        bpasswd = self.get_argument("bpasswd")
        if not email or not ltoken or not bankid or not buser or not bpasswd:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        details = getBank(bankid)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if mysqlc == -1:
            self.write(Json().encode({'status' : 'failure', 'errcode' : ERROR_0001}))
        else:
            result = ValidateToken(email,ltoken);
            if result == -1:
                self.write(Json().encode({'status': 'failure', 'errcode':ERROR_0002}))
            else:
                if details[0] == "sbi":
                    safe_token = SBI.LoginUser(buser,bpasswd)
                elif details[0] == "axis":
                    safe_token = AXIS.LoginUser(buser,bpasswd)

                if safe_token == -1:
                    self.write(Json().encode({'status' : 'failure', 'errcode': 'Invalid Bank credentials. Please retry'}))
                else:
                    check_avail = "SELECT token FROM accounts WHERE bankid = %s AND username = %s"
                    parapool.assignnew(mysqlc_p.handle.execute ,(check_avail,(bankid,buser))).get()
                    avail = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
                    ut_params = None
                    if avail == None:
                        update_token = "INSERT INTO accounts(userid,bankid,username,passwd,token) VALUES((SELECT id FROM users WHERE email = %s) , %s , %s , %s , %s)"
                        ut_params = (email,bankid,buser,bpasswd,safe_token)
                    else :
                        update_token = "UPDATE accounts SET token = %s WHERE userid = %s AND bankid = %s"
                        ut_params = (safe_token,result[1],bankid)
                    try:
                        print(update_token)
                        parapool.assignnew(mysqlc_p.handle.execute , (update_token,)).get()
                        parapool.assignnew(mysqlc.commit ,())
                        self.write(Json().encode({'status': 'success','login_token':safe_token,'bank':details[1]}))
                    except:
                        self.write(Json().encode({'status': 'failure', 'errcode': 'There was something wrong with the Bank\'s response. Please retry.'}))
                        mysqlc.rollback()
        if mysqlc != -1:
            mysqlc.close()
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()

class EnquiryBalance(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        bankid = self.get_argument("bankid")
        accno = self.get_argument("accno")
        if not email or not ltoken or not bankid or not accno:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        details = getBank(bankid)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if mysqlc == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0001}))
        else:
            result = ValidateToken(email, ltoken);
            if result == -1:
                self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
            else:
                str_cd = "SELECT username , token FROM accounts WHERE bankid = %s AND userid = (SELECT id FROM users WHERE email = %s AND token = %s)"
                parapool.assignnew(mysqlc_p.handle.execute , (str_cd,(bankid,email,ltoken),) ).get()
                binfo = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
                if binfo == None:
                    self.write(Json().encode({'status': 'failure', 'errcode': 'This account was not registered. Please register first.'}))
                    self.endconn()
                    return
                if details[0] == "sbi":
                    bank_response = SBI.CheckBalance(binfo[0],binfo[1],accno)
                elif details[0] == "axis":
                    bank_response = AXIS.CheckBalance(binfo[0],binfo[1],accno)
                if bank_response == -1:
                    self.write(Json().encode({'status':'failure','errcode':'Bank did not respong as expeccted. Please retry.'}))
                else:
                    self.write(Json().encode({'status': 'success', 'balance':bank_response}))
        mysqlc.close()
        self.endconn()
    def endconn(self):
        self.finish()
        self.flush

class LinkedAcc(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        bankid = self.get_argument("bankid")
        if not email or not ltoken or not bankid:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        details = getBank(bankid)
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            parapool.assignnew(mysqlc_p.handle.execute , ("SELECT username , token FROM accounts WHERE bankid = %s AND userid = (SELECT id FROM users WHERE email = %s AND token = %s )",(bankid,email,ltoken),)).get()
            binfo = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            if binfo == None:
                self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0003}))
                self.endconn()
                return
            if details[0] == "sbi":
                bank_response = SBI.FetchAccounts(binfo[0], binfo[1])
            elif details[0] == "axis":
                bank_response = AXIS.FetchAccounts(binfo[0], binfo[1])
            if bank_response == -1:
                self.write(Json().encode({'status':'failure','errcode':"No accounts found"}))
            else:
                self.write(Json().encode({'status':'success','accounts':bank_response}))
        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()

class LinkedAccWithIFSC(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        bankid = self.get_argument("bankid")
        if not email or not ltoken or not bankid:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        details = getBank(bankid)
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            parapool.assignnew(mysqlc_p.handle.execute , ("SELECT username , token FROM accounts WHERE bankid = %s AND userid = (SELECT id FROM users WHERE email = %s AND token = %s)",(bankid,enail,ltoken),)).get()
            binfo = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            if binfo == None:
                self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0003}))
                self.endconn()
                return
            if details[0] == "sbi":
                bank_response = SBI.FetchAccountsWithIFSC(binfo[0], binfo[1])
            elif details[0] == "axis":
                bank_response = AXIS.FetchAccountsWithIFSC(binfo[0], binfo[1])
            if bank_response == -1:
                self.write(Json().encode({'status':'failure','errcode':"No accounts were found"}))
            else:
                self.write(Json().encode({'status':'success','accounts':bank_response}))
        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()


class FundTransfer(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        bankid = self.get_argument("bankid")
        defacc = self.get_argument("def_acc",False) # SEND FROM DEFAULT ACCC
        semail = self.get_argument("b_email","None") # RECIEVER EMAIL ID
        qsend = self.get_argument("qsend","None") # QUICKSEND CONTACT
        accno = self.get_argument("r_accno","None")
        amount = self.get_argument("amount","None")
        transtype = self.get_argument("trans_type")
        regsname = None
        mysqlc_p = MySQLdb()
        mysqlc  = mysqlc_p.mysqlc
        if semail != "None" and semail != "" and semail != GARBAGE_VALUE:
            parapool.assignnew(mysqlc_p.handle.execute ,("SELECT id , name FROM users WHERE email = %s ",(semail),)).get()
            details = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            regsname = details[1]
            parapool.assignnew(mysqlc_p.handle.execute ,("SELECT accno,ifsc FROM defaultaccs WHERE userid = %s ",(details[0]),)).get()
            accdetails = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            ifsc = accdetails[1]
            bid1 = getBank(bankid)[0]
            bid2 = map(ifsc)
            if bid1 == bid2:
                dest = "INTRA_BANK"
            else:
                dest = "INTER_BANK"
            benefacc = accdetails[0]
            ifsc = accdetails[1]
        elif qsend != "None" and qsend != "" and qsend != GARBAGE_VALUE:
            print(qsend)
            parapool.assignnew(mysqlc_p.handle.execute ,("SELECT acc , ifsc  FROM quicksend WHERE LOWER(name) = %s AND createdby = (SELECT id FROM users WHERE email = %s)",(qsend.lower(),email),)).get()
            accdetails = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            if accdetails is None:
                self.write(Json().encode({'status': 'failure', 'errcode': 'You have no quick send contact with name '+qsend}))
                self.endconn()
            ifsc = accdetails[1]
            benefacc = accdetails[0]
            bid1 = getBank(bankid)[0]
            bid2 = map(ifsc)
            dest = "None"
            if bid1 == bid2:
                dest = "INTRA_BANK"
            else:
                dest = "INTER_BANK"
        else :
            ifsc = self.get_argument("b_ifsc","None")
            benefacc = self.get_argument("b_accno","None")
            bid1 = getBank(bankid)[0]
            bid2 = map(ifsc)
            dest="None"
            if bid1 == bid2:
                dest = "INTRA_BANK"
            else:
                dest = "INTER_BANK"
            if not email or not ltoken  or not bankid or not accno  or not benefacc or not amount:
                self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0001}))
                self.endconn()
        if defacc != "False":
            parapool.assignnew(mysqlc_p.handle.execute ,("SELECT accno,ifsc FROM defaultaccs WHERE userid = (SELECT id FROM users WHERE email = %s)",(email),)).get()
            dec = (parapool.assignnew(mysqlc_p.handle.fetchone ,())).get()
            accno = dec[0]
            bid2 = map(dec[1])
            if bid2 == "sbi":
                bankid = "2018001"
            else:
                bankid = "2018002"
            bid1 = getBank(bankid)[0]

            if bid1 == bid2:
                dest = "INTRA_BANK"
            else:
                dest = "INTER_BANK"

        details = getBank(bankid)
        result = ValidateToken(email, ltoken)
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            parapool.assignnew(mysqlc_p.handle.execute,("SELECT username , token FROM accounts WHERE bankid = %s AND userid = (SELECT id FROM users WHERE email = %s AND token = %s)",(bankid,email,ltoken),)).get()
            binfo = parapool.assignnew(mysqlc_p.handle.fetchone ,()).get()
            if binfo == None:
                self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0003}))
                self.endconn()
                return
            if transtype == "NEFT" or transtype == "RTGS":
                if details[0] == "sbi":
                    bank_response = SBI.NEFT_RTGS(binfo[0], binfo[1],accno,ifsc,benefacc,amount,dest)
                elif details[0] == "axis":
                    bank_response = AXIS.NEFT_RTGS(binfo[0], binfo[1],accno,ifsc,benefacc,amount,dest)
                if bank_response[0] == 1:
                    self.write(Json().encode({'status':'failure','errcode':'You do not have enough balance. You have only '+str(bank_response[1])+' rupees in you account.Use it wisely.'}))
                elif bank_response[0] == -1:
                    self.write(Json().encode({'status': 'failure', 'errcode': 'Unknown error was recorded on our end. Please try again and check if correct beneficiary credentials were submitted'}))
                else:
                    register_trans = "INSERT INTO transactions(userid,remitbank,transtype,remitacc,benefifsc,benefacc,transamt) VALUES((SELECT id FROM users WHERE email = %s) , %s , %s , %s , %s , %s , %s )"
                    parapool.assignnew(mysqlc_p.handle.execute, (register_trans,(email,bankid,dest,accno,ifsc,benefacc,amount))).get()
                    try:
                        parapool.assignnew_nowait(mysqlc.commit, ())
                        self.write(Json().encode(
                            {'status': 'success', 'current_balance': bank_response[0], 'bankid': bankid,
                             'account': accno, 'recorded': 'true'}))
                    except:
                        parapool.assignnew_nowait(mysqlc.rollback, ())
                        self.write(Json().encode(
                            {'status': 'success', 'current_balance': bank_response[0], 'bankid': bankid,
                             'account': accno, 'recorded': 'false', 'beneficiary': regsname}))
        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()
class QuickSend(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self,action):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if action == "create":
            accno = self.get_argument("accno")
            ifsc = self.get_argument("ifsc")
            name = self.get_argument("name")
            if not email or not ltoken  or not accno or not ifsc or not name:
                self.write(Json().encode({'status': 'failure', 'errcode': '0000'}))
                self.endconn()
            result = ValidateToken(email, ltoken)
            if result == -1:
                self.write(Json().encode({'status': 'failure', 'errcode': '0001'}))
            else:
                addcontact = "INSERT INTO quicksend(name,acc,ifsc,createdby) VALUES('"+name+"' , '"+accno+"' , '"+ifsc+"' ,(SELECT id FROM users WHERE email = '"+email+"'))"
                try:
                    parapool.assignnew(mysqlc_p.handle.execute,(addcontact,)).get()
                    parapool.assignnew_nowait(mysqlc_p.mysqlc.commit,())

                    self.write(Json().encode({'status':'success','contact':name}))
                except:
                    parapool.assignnew_nowait(mysqlc_p.mysqlc.rollback,())
                    self.write(Json().encode({'status': 'failure', 'errno': '0002'}))
            mysqlc.close()
            self.endconn()
        elif action == "fetchall":
            result = ValidateToken(email, ltoken)
            if result == -1:
                self.write(Json().encode({'status': 'failure', 'errcode': '0002'}))
            else:
                fetch_all = "SELECT name , acc ,ifsc FROM quicksend WHERE createdby = (SELECT id FROM users WHERE email = '"+email+"')"
                parapool.assignnew(mysqlc_p.handle.execute,(fetch_all,)).get()
                records = parapool.assignnew(mysqlc_p.handle.fetchall,()).get()
                l_records = []
                cd = 0;
                for i in records:
                    l_records.append(list(i))
                    l_records[cd][2] = map(l_records[cd][2])
                    cd+=1
                print(l_records)
                
                self.write(Json().encode({"status":"success","quicksendcontacts":l_records}))
                mysqlc.close()
                self.endconn()


    def endconn(self):
        self.finish()
        self.flush()

		
class TransactHistory(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        timespan = self.get_argument("timespan")
        spantype = self.get_argument("spantype")
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if not email or not ltoken or not timespan or not spantype:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0000}))
            self.endconn()
        result = ValidateToken(email, ltoken)
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            sparams=()
            query = "SELECT * FROM transactions WHERE userid = (SELECT id FROM users WHERE email = %s)"
            sparams = sparams + (email,)
            if spantype == "days":
                print("s")
                ts_today = str(datetime.datetime.now()).split('.')[0]
                ts_ndaysback = (str(datetime.datetime.now() - timedelta(days=(int(timespan)+1)))).split('.')[0]
                query+=' AND timestamp BETWEEN %s AND %s '
                sparams = sparams + (ts_ndaysback,ts_today,)
            elif spantype == "months":
                ts_today = str(datetime.datetime.now()).split('.')[0]
                ts_ndaysback = (str(datetime.datetime.now() - timedelta(days=int(timespan)*30))).split('.')[0]
                query+=' AND timestamp BETWEEN %s AND %s '
                sparams = sparams + (ts_ndaysback,ts_today,)
            elif spantype == "years":
                ts_today = str(datetime.datetime.now()).split('.')[0]
                ts_ndaysback = (str(datetime.datetime.now() - timedelta(days=(int(timespan)*365)))).split('.')[0]
                query+=' AND timestamp BETWEEN "'+ts_ndaysback+'" AND "'+ts_today+'")'
            print(query)
            print(sparams)
            parapool.assignnew(mysqlc_p.handle.execute,(query,(sparams),)).get()
            history = parapool.assignnew(mysqlc_p.handle.fetchall,()).get()
            txns = len(history)
            intrab_txns = 0
            interb_txns = 0
            total_amt = 0
            avg_amt = 0
            for record in history:
                if record[3] == "INTRA_BANK":
                    intrab_txns+=1
                else:
                    interb_txns+=1
                total_amt += record[8]

            avg_amt = float("%.2f" % (total_amt / txns))
            self.write(Json().encode({"status":"success","Total Transactions":str(txns),"Total Intra Bank Transactions":str(intrab_txns),"Total Inter Bank Transactions":str(interb_txns),"Total amount":str(total_amt),"Average Amount":str(avg_amt)}))
        self.finish()
        self.flush()
                    
class AddDefaultAccount(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        accno = self.get_argument("sda_accno")
        ifsc = self.get_argument("ifsc")
        if not email or not ltoken or not accno or not ifsc :
            print("err 0 ")
            self.write(Json().encode({'status': 'failure', 'errcode': '0000'}))
            self.endconn()
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            print("err 1 ")
            self.write(Json().encode({'status': 'failure', 'errcode': '0001'}))
        else:
            parapool.assignnew(mysqlc_p.handle.execute,("SELECT id FROM users WHERE email = %s",(email),))
            userid = parapool.assignnew(mysqlc_p.handle.fetchone,())
            parapool.assignnew(mysqlc_p.handle.execute, ("SELECT accno FROM defaultaccs WHERE userid = %s ",(userid),))
            find_rows = parapool.assignnew(mysqlc_p.handle.fetchone,())
            sparams = ()
            if find_rows == None:
                def_query = "INSERT INTO defaultaccs(userid,accno,ifsc) VALUES(%s,%s,%s)"
                sparams = sparams + (userid,accno,ifsc)
            else:
                def_query = "UPDATE defaultaccs SET accno = %s , ifsc = %s WHERE userid  = %s"
                sparams = sparams + (accno,ifsc,userid)
            try:
                parapool.assignnew(mysqlc_p.handle.execute,(def_query,(sparams),))
                parapool.assignnew_nowait(mysqlc.commit)
                
                self.write(Json().encode({"status":'success',"accno":accno}))
            except:
                print(ifsc+" " +str(len(ifsc)))
                print(def_query)
                self.write(Json().encode({"status": 'failure','errcode':'0002'}))
                parapool.assignnew_nowait(mysqlc.rollback)
        parapool.assignnew_nowait(mysqlc.close)
        self.endconn()


    def endconn(self):
        self.finish()
        self.flush()


class Test(webserver.RequestHandler):
    regsname = None
    @webserver.asynchronous
    def get(self):
        self.write(Json().encode({"status":"running"}));
        self.finish()
        self.flush()

class PullTransfer(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        toemail = self.get_argument("toemail")
        amount = self.get_argument("amount")
        if not email or not ltoken or not toemail:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            qtok = "SELECT token FROM users WHERE email = %s ";
            parapool.assignnew(mysqlc_p.handle.execute,(qtok,(toemail),)).get()
            tok = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
            query = "INSERT INTO pull_transfer(fromemail,toemail,amount) VALUES(%s , %s , %s)"
            parapool.assignnew(mysqlc_p.handle.execute,(query,(email,toemail,amount),)).get()
            try:
                parapool.assignnew_nowait(mysqlc.commit , ())
                if tok:
                    message = messaging.Message(
                        data={
                            'fromemail' : email,
                            'toemail' : toemail,
                            'amount' : amount,
                            },
                        topic=str(tok[0])+'pulltransfer',
                        )
                    res = messaging.send(message)
                    print(res)
                self.write(Json().encode({"status":"success","toemail":toemail}))
            except :
                self.write(Json().encode({"status": "failure", "errcode":"Internal error was faced to make the pull transfer. Please try again"}))

        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()

class FetchPullTransfers(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        if not email or not ltoken:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            query = "SELECT * FROM pull_transfer WHERE toemail = '"+email+"' AND fetched = 0"
            parapool.assignnew(mysqlc_p.handle.execute,(query,(email),)).get()
            results = parapool.assignnew(mysqlc_p.handle.fetchall,()).get()
            if not results is None:
                query2 = "UPDATE pull_transfer SET fetched = 1 WHERE fetched = 0 AND toemail = '"+email+"'"
                parapool.assignnew(mysqlc_p.handle.execute,(query2,)).get()
                parapool.assignnew(mysqlc.commit,())
                self.write(Json().encode({"status":"success","pts":results}))
            else:
                self.write(Json().encode({"status": "failure", "errcode":"Internal error was faced to make the pull transfer. Please try again"}))

        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()

class FetchDefaultAccount(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        self.set_header("Content-Type", "application/json")
        email = self.get_argument("email")
        ltoken = self.get_argument("token")
        if not email or not ltoken:
            self.write(Json().encode({'status':'failure','errcode':ERROR_0000}))
            self.endconn()
        result = ValidateToken(email, ltoken)
        mysqlc_p = MySQLdb()
        mysqlc = mysqlc_p.mysqlc
        if result == -1:
            self.write(Json().encode({'status': 'failure', 'errcode': ERROR_0002}))
        else:
            query = "SELECT * FROM defaultaccs WHERE userid = ( SELECT id FROM users WHERE email = %s )"
            parapool.assignnew(mysqlc_p.handle.execute,(query,(email),)).get()
            results = parapool.assignnew(mysqlc_p.handle.fetchone,()).get()
            if not results is None:
                self.write(Json().encode({"status": "success", "accno": results[1], "ifsc": results[2]}))
            else:
                self.write(Json().encode({"status": "failure", "errcode":"Internal error was faced to make the pull transfer. Please try again"}))

        parapool.assignnew_nowait(mysqlc.close,())
        self.endconn()

    def endconn(self):
        self.finish()
        self.flush()


class securityTest(webserver.RequestHandler):
    @webserver.asynchronous
    def post(self):
        
        self.write("DONE")
        self.endconn()
        
    def endconn(self):
        self.finish()
        self.flush()
