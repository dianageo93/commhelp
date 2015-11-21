import cgi
import json
import urllib
import urllib2
import webapp2
from random import choice
from google.appengine.ext import db
import json
from RegisteredUser import RegisteredUser

from optparse import OptionParser
import inspect

class RegisterUser(webapp2.RequestHandler):
    def post(self):
        jsonobject = json.loads(self.request.body)
        u = RegisteredUser(
            name=jsonobject["name"],
            lat = float(jsonobject["lat"]),
            lon = float(jsonobject["lon"]),
            rank=0,
            role=jsonobject["role"]
        )
        u.put()

class GetHelp(webapp2.RequestHandler):

    def post(self):
        RADIUS = 100
        jsonobject = json.loads(self.request.body)
        # XXX: Super ultra mega hack because GQL doesn't allow for multiple
        # filters that use <, >, <= or >=
        registered_users = RegisteredUser.all().filter(
            'lat >=', float(jsonobject["lat"]) - RADIUS
        )
        registered_users = filter(
            lambda u: (u.lat <= float(jsonobject["lat"]) + RADIUS
                and u.lon >= float(jsonobject["lon"]) - RADIUS
                and u.lon <= float(jsonobject["lon"]) + RADIUS),
            registered_users)
#         registered_users = db.GqlQuery(
#             "SELECT * \
#                 FROM RegisteredUser \
#                 WHERE lat >= :1 and lat <= :1 and lon >= :2 and lon <= :2",
#             float(jsonobject["lat"]) - RADIUS,
#             float(jsonobject["lat"]) + RADIUS,
#             float(jsonobject["lon"]) - RADIUS,
#             float(jsonobject["lon"]) + RADIUS
#         )
#         registered_users = db.GqlQuery(
#             "SELECT * \
#                 FROM RegisteredUser \
#                 WHERE lat >= :1",
#             jsonobject["lat"]
#         )
#         print inspect.getmembers(db.GeoPt, predicate=inspect.ismethod)
        self.response.write("Registered users:\n")
        for u in registered_users:
            self.response.write(str(u)+'\n')

class UpdateUserData(webapp2.RequestHandler):
    def post(self):
        pass

class Register(webapp2.RequestHandler):
    def post(self):
        self.response.write(cgi.escape(self.request.get('gcmtoken')))
        self.response.write(cgi.escape(self.request.get('name')))

class Notifier(webapp2.RequestHandler):
    def post(self):
        url = 'http://gcm-http.googleapis.com/gcm/send'
        to = str(cgi.escape(self.request.get('to')))
        message = str(cgi.escape(self.request.get('message')))
        data = {
                "data": {'message': message},
                "to": to
                }
        headers = {
                "Content-Type":"application/json",
                "Authorization":"key=AIzaSyBk3-v9AaKz8s2KYLuImlsIBSl1GF6XGlM"
                }
        req = urllib2.Request(url, json.dumps(data), headers)
        response = urllib2.urlopen(req)

app = webapp2.WSGIApplication([
    ('/registeruser', RegisterUser),
    ('/gethelp', GetHelp),
    ('/updateuserdata', UpdateUserData),
    ('/notify', Notifier),
], debug=True)
