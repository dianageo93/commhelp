import webapp2
from random import choice
from google.appengine.ext import db
import json
from RegisteredUser import RegisteredUser

class RegisterUser(webapp2.RequestHandler):
    def post(self):
        u = RegisteredUser(
            name=choice(["John", "Mihaitza", "Bogdanel"]),
            location = db.GeoPt(lat=45.0, lon=25.0),
            rank=0,
            role="default"
        )
        u.put()

class GetHelp(webapp2.RequestHandler):
    def post(self):
        print "CORPUL MEU",self.request.body
        self.response.headers['Content-Type'] = 'text/plain'
        registered_users = db.GqlQuery(
            "SELECT * FROM RegisteredUser WHERE rank = 0"
        )
        self.response.write("Registered users:\n")
        for u in registered_users:
            self.response.write(str(u)+'\n')

class UpdateUserData(webapp2.RequestHandler):
    def post(self):
        pass

app = webapp2.WSGIApplication([
    ('/registeruser', RegisterUser),
    ('/gethelp', GetHelp),
    ('/updateuserdata', UpdateUserData),
], debug=True)
