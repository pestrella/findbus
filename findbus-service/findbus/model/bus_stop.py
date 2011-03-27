#!/usr/bin/env python

from google.appengine.ext import db
from geo.geomodel import GeoModel

class BusStop(GeoModel):
    code = db.StringProperty()
    name = db.StringProperty()
    routes = db.StringListProperty()