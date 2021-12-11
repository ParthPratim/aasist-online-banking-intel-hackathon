import tornado.web

class SBI(tornado.web.RequestHandler):
    def get(self):
        self.write("sbi.html")
        #self.render("index.html") INDEX.PHP here to be rendered

