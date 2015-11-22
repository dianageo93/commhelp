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
            gender=jsonobject["gender"] if "gender" in jsonobject else None,
            phone_number=db.PhoneNumber(jsonobject["phone_number"])
                if "phone_number" in jsonobject else None,
            lat = float(jsonobject["lat"]),
            lon = float(jsonobject["lon"]),
            rank=0,
            role=jsonobject["role"]
        )
        u.put()

# JSON has uid, lat, lon
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

        if not registered_users:
            return

        ng = NotificationGroup(jsonobject["uid"])
        for u in registered_users:
            ng.uids.append(u.uid)
        ng.put()

        self.response.write("Registered users:\n")
        for u in registered_users:
            self.response.write(str(u)+'\n')

# JSON has uid, victim_uid
class GiveHelp(webapp2.RequestHandler):
    def post(self):
        jsonobject = json.loads(self.request.body)
        ng = NotificationGroup.all().filter("victim=", jsonobject["victim_uid"])

        if not ng:
            return

        for u in ng.uids:
            if u != jsonobject["uid"]
                # TODO: send message to all the volunteers telling them that they
                # are not needed any more
                pass

        ng.delete()


# JSON has uid, lat, lon
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
        name = str(cgi.escape(self.request.get('name')))
        lat = str(cgi.escape(self.request.get('lat')))
        lng = str(cgi.escape(self.request.get('lng')))
        data = {
                "data": {
                    "name": name,
                    "lat": lat,
                    "lng": lng
                    },
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
    ('/givehelp', GiveHelp),
    ('/updateuser', UpdateUserData),
    ('/notify', Notifier),
], debug=True)
