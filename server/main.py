import webapp2
from random import choice
from google.appengine.ext import db
from db_proba import User

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write('Hello, World!\n')
        u = User(
          name=choice(["John", "Mihaitza", "Bogdanel"]),
          location = db.GeoPt(lat=45.0, lon=25.0),
          rank=0,
          role="default"
        )
        u.put()

        registered_users = db.GqlQuery(
          "SELECT * FROM User WHERE rank = 0"
        )
        self.response.write("Registered users:\n")
        for u in registered_users:
          self.response.write(str(u)+'\n')

app = webapp2.WSGIApplication([
    ('/', MainPage),
], debug=True)
