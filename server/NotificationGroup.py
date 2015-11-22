import datetime
from google.appengine.ext import db
from google.appengine.api import users

class NotificationGroup(db.Model):
    victim = db.StringProperty(required=True)
    uids = db.ListProperty(str)

