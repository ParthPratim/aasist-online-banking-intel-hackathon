import tornado.web

class AXIS(tornado.web.RequestHandler):
    def get(self):
        self.write("axis.html")
        #self.render("index.html") INDEX.PHP here to be rendered
