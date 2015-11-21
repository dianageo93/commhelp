import cgi
import json
import urllib
import urllib2
import webapp2
from random import choice
from google.appengine.ext import db
import json
from RegisteredUser import RegisteredUser

class RegisterUser(webapp2.RequestHandler):
    def post(self):
        self.response.write(cgi.escape(self.request.get('gcmtoken')))
        self.response.write(cgi.escape(self.request.get('name')))

        jsonobject = json.loads(self.request.body)

        # Check if the uid is unique
        curr_uid = RegisteredUser.all(keys_only=True).filter(
            'uid', jsonobject["uid"]
        ).get()
        if curr_uid:
            raise Exception('UID must have a unique value!')

        # Add user to datastore
        u = RegisteredUser(
            uid=jsonobject["uid"],
            name=jsonobject["name"],
            sex=jsonobject["sex"] if "sex" in jsonobject else None,
            phone_number=db.PhoneNumber(jsonobject["phone_number"])
                if "phone_number" in jsonobject else None,
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
        registered_users = RegisteredUser.all().filter("role =", "helper")
        registered_users = filter(
            lambda u: (u.lat >= float(jsonobject["lat"]) - RADIUS
                and u.lat <= float(jsonobject["lat"]) + RADIUS
                and u.lon >= float(jsonobject["lon"]) - RADIUS
                and u.lon <= float(jsonobject["lon"]) + RADIUS),
            registered_users)
        self.response.write("Registered users:\n")
        for u in registered_users:
            self.response.write(str(u)+'\n')

class UpdateUserData(webapp2.RequestHandler):
    def post(self):
        jsonobject = json.loads(self.request.body)
        users = RegisteredUser.all()
        curr_user = filter(
            lambda x: x,
            [u if u.uid == jsonobject["uid"] else None for u in users]
        )
        if len(curr_user) != 1:
            raise Exception("User not in DB!")

        curr_user = curr_user[0]
        curr_user.lat = float(jsonobject["lat"])
        curr_user.lon = float(jsonobject["lon"])
        curr_user.put()

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
    ('/updateuser', UpdateUserData),
    ('/notify', Notifier),
], debug=True)
