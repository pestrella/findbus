package com.heychinaski.findbus;

import com.google.android.maps.*;

import com.heychinaski.findbus.*;
import com.heychinaski.findbus.model.*;
import com.heychinaski.findbus.service.*;
import com.heychinaski.findbus.ui.*;

import java.util.*;

import android.graphics.drawable.*;
import android.location.*;
import android.os.*;

/**
 * @author pestrella
 */
public class MapViewActivity extends MapActivity {

  @Override
  @SuppressWarnings("unchecked")
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.map);

    MapView mapView = (MapView) findViewById(R.id.mapview);
    mapView.setBuiltInZoomControls(true);

    List<Overlay> overlays = mapView.getOverlays();
    Drawable busStopIcon = this.getResources().getDrawable(R.drawable.bus_stop);
    BusItemizedOverlay busItemizedOverlay = new BusItemizedOverlay(busStopIcon, this);

    List<Stop> stops = (List<Stop>) ObjectCache.get(R.id.mapview);
    for (Stop stop : stops) {
      GeoPoint geoPoint = new GeoPoint(
        new Double(stop.getLatitude() * 1E6).intValue(),
        new Double(stop.getLongitude() * 1E6).intValue()
      );
      OverlayItem busStopOverlay = new OverlayItem(geoPoint, stop.getName(), listStops(stop.getRoutes()));
      busItemizedOverlay.addOverlay(busStopOverlay);
    }

    Location location = (Location) ObjectCache.get(R.string.location);
    GeoPoint currentLocation = new GeoPoint(
      new Double(location.getLatitude() * 1E6).intValue(),
      new Double(location.getLongitude() * 1E6).intValue()
    );
    Drawable yourIcon = this.getResources().getDrawable(R.drawable.you);
    BusItemizedOverlay yourOverlay = new BusItemizedOverlay(yourIcon);
    OverlayItem yourLocation = new OverlayItem(currentLocation, null, null);
    yourOverlay.addOverlay(yourLocation);

    overlays.add(busItemizedOverlay);
    overlays.add(yourOverlay);

    MapController controller = mapView.getController();
    controller.animateTo(currentLocation);
    controller.setZoom(17);
  }

  private String listStops(List<String> routes) {
    String asString = "Routes: ";
    for (String route : routes) {
      asString += route + ", ";
    }
    return asString.substring(0, asString.length() - 2) + ".";
  }

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
}