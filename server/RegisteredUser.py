import datetime
from google.appengine.ext import db
from google.appengine.api import users

class RegisteredUser(db.Model):
    name = db.StringProperty(required=True)
    lat = db.FloatProperty(required=True)
    lon = db.FloatProperty(required=True)
    rank = db.IntegerProperty(required=True)
    role = db.StringProperty(
        required=True,
        choices=set(["default", "helper"])
    )

    def __str__(self):
        return "; ".join(
            [self.name, str(self.lat), str(self.lon), str(self.rank), self.role]
        )

