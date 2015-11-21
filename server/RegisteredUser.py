import datetime
from google.appengine.ext import db
from google.appengine.api import users

class RegisteredUser(db.Model):
    uid = db.StringProperty(required=True)
    name = db.StringProperty(required=True)
    sex = db.StringProperty()
    phone_number = db.PhoneNumberProperty()
    lat = db.FloatProperty(required=True)
    lon = db.FloatProperty(required=True)
    rank = db.IntegerProperty(required=True)
    role = db.StringProperty(
        required=True,
        choices=set(["default", "helper"])
    )

    def __str__(self):
        return "; ".join(
            [self.uid, self.name, str(self.lat), str(self.lon), str(self.rank), self.role]
        )

