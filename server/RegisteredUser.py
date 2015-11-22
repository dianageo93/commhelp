import datetime
from google.appengine.ext import db
from google.appengine.api import users

badge_ids = [61091, 61092, 61090, 61089]

class RegisteredUser(db.Model):
    uid = db.StringProperty(required=True)
    name = db.StringProperty(required=True)
    gender = db.StringProperty()
    phone_number = db.PhoneNumberProperty()
    email = db.EmailProperty()
    lat = db.FloatProperty(required=True)
    lon = db.FloatProperty(required=True)
    rank = db.IntegerProperty(required=True)
    count_reviews = db.IntegerProperty(required=True)
    role = db.StringProperty(
        required=True,
        choices=set(["default", "helper"])
    )
    badge_id = db.IntegerProperty(required=True)

    def __str__(self):
        return "; ".join(
            [self.uid, self.name, str(self.lat), str(self.lon), str(self.rank), self.role]
        )

