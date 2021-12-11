import signal
import tornado.ioloop
import tornado.process
import aassist.webhandlers as AASSIST
from aassist.banks.sbi.webhandlers import SBI
from aassist.banks.axis.webhandlers import AXIS

PORT = 2018

def signal_handler():
    ioloop = tornado.ioloop.IOLoop.current()
    ioloop.stop();

def aassist_app_server():
    settings = {
        'debug': False,
		'autoreload' : False,
        # other stuff
    }
    application = tornado.web.Application([
        (r"/", AASSIST.AAssistMain),
        (r"/servertest",AASSIST.Test),
        (r"/banks/sbi/", SBI),
        (r"/banks/sbi", SBI),
        (r"/banks/axis/", AXIS),
        (r"/banks/axis", AXIS),
        (r"/regsaccount/",AASSIST.RegisterBank),
        (r"/transacthistory/",AASSIST.TransactHistory),
        (r"/defaultaccount/",AASSIST.AddDefaultAccount),
        (r"/fetchdefaultaccount/", AASSIST.FetchDefaultAccount),
        (r"/enqbalance/", AASSIST.EnquiryBalance),
        (r"/linkedaccounts/", AASSIST.LinkedAcc),
        (r"/pulltransfer/", AASSIST.PullTransfer),
        (r"/fetchpulltransfers/", AASSIST.FetchPullTransfers),
        (r"/linkedaccountswithifsc/", AASSIST.LinkedAccWithIFSC),
        (r"/fundtransfer/",AASSIST.FundTransfer),
        (r"/sectest/",AASSIST.securityTest),
        (r"/quicksend/([a-z]+)",AASSIST.QuickSend)
    ], debug=False, autoreload=False)
    #application.listen(PORT)
    server = tornado.httpserver.HTTPServer(application)
    server.bind(PORT)
    server.start(0)
    print("SERVER STARTED ON PORT : "+PORT)
    tornado.ioloop.IOLoop.current().start()

