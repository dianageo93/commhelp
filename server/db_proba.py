import datetime
from google.appengine.ext import db
from google.appengine.api import users

class User(db.Model):
  name = db.StringProperty(required=True)
  location = db.GeoPtProperty(required=True)
  rank = db.IntegerProperty(required=True)
  role = db.StringProperty(
    required=True,
    choices=set(["default", "helper"])
  )

  def __str__(self):
    return "; ".join(
      [self.name, str(self.location), str(self.rank), self.role]
    )

