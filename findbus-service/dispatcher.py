#!/usr/bin/env python

from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app
from findbus.handler.bus_stop_handler import BusStopController

application = webapp.WSGIApplication([
    ('/bus_stop', BusStopController),
    (r'/bus_stop/(%40location)/([^/]+)', BusStopController),
    (r'/bus_stop/(%40location)/([^/]+)/(.+)', BusStopController),
    (r'/bus_stop/(.+)', BusStopController)
], debug=True)

def main():
    run_wsgi_app(application)

if __name__ == "__main__":
    main()