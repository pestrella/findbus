#!/usr/bin/env python

from django.utils import simplejson as json
import urllib
import wsgiref.handlers
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
from google.appengine.ext import db
from google.appengine.api import memcache
from findbus.model.bus_stop import BusStop

class BusStopController(webapp.RequestHandler):
    def post(self):
        code = self.request.get('code')
        name = self.request.get('name')
        route = self.request.get('route')
        longitude = self.request.get('long')
        latitude = self.request.get('lat')

        bus_stop = db.Query(BusStop).filter('code = ', code).get()
        if (bus_stop == None):
            new_bus_stop = BusStop(
                location=db.GeoPt(float(latitude), float(longitude)),
                code=code,
                name=name,
                routes=[route]
            )
            new_bus_stop.update_location()
            new_bus_stop.put()
            self.response.set_status(201)
            return

        # if the bus stop exists just add the route
        if (route not in bus_stop.routes):
            bus_stop.routes.append(route)

        bus_stop.put()
        self.response.set_status(201)

    def get(self, context, coord=None, bus_no=None):
        if (context == '%40location'):
            if (coord == None):
                self.response.set_status(400)
                self.response.out.write('require geopoint')
                return

            geopoint = coord.split('%2C')
            self.response.out.write(self.get_nearest(geopoint[0], geopoint[1], bus_no))
            return

        code = context
        bus_stop = db.Query(BusStop).filter('code = ', code).get()
        if (bus_stop == None):
            self.response.set_status(404)
            self.response.out.write('{}')
            return

        as_json = {}
        as_json['code'] = bus_stop.code
        as_json['name'] = bus_stop.name
        as_json['routes'] = bus_stop.routes
        as_json['latitude'] = bus_stop.location.lat
        as_json['longitude'] = bus_stop.location.lon

        self.response.out.write(json.dumps(as_json, encoding='UTF-8'))

    def get_nearest(self, latitude, longitude, bus_no=None):
        base_query = db.Query(BusStop)
        if (bus_no != None):
            base_query = base_query.filter('routes = ', bus_no)

        bus_stops = BusStop.proximity_fetch(
            base_query,
            db.GeoPt(float(latitude), float(longitude)),
            max_results=10,
            max_distance=8046 # default 5 miles
        )
        all_as_json = []
        for bus_stop in bus_stops:
            as_json = {}
            as_json['code'] = bus_stop.code
            as_json['name'] = bus_stop.name
            as_json['routes'] = bus_stop.routes
            as_json['latitude'] = bus_stop.location.lat
            as_json['longitude'] = bus_stop.location.lon
            all_as_json.append(as_json)

        return json.dumps(all_as_json, "UTF-8")
