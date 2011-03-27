package com.heychinaski.findbus.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class LocationHelper {
	private LocationManager locationManager;
	private NetworkLocationListener networkLocationListener;
	private GpsLocationListener gpsLocationListener;

	private Location bestResult = null;
	private Timer timer;
	private AsyncTask<Location, ?, ?> task;
	private Activity activity;
	private long maxTimeInSeconds;

	
	private boolean alreadyRun = false;
	private long freshnessInSeconds;
	
	public LocationHelper(Activity activity, final AsyncTask<Location, ?, ?> task, final long maxTimeInSeconds, final long freshnessInSeconds) {
		super();
		this.activity = activity;
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		this.task = task;
		this.maxTimeInSeconds = maxTimeInSeconds;
		this.freshnessInSeconds = freshnessInSeconds;
	}

	public void getLocationThenExecTask() {
		if(alreadyRun) {
			throw new RuntimeException("LocationHelper can only be used once");
		} else {
			alreadyRun = true;
		}
		
		Criteria accurate = new Criteria();
		accurate.setAccuracy(Criteria.ACCURACY_FINE);

		timer = new Timer();

		boolean gpsEnabled = false;
		boolean networkEnabled = false;
		//exceptions will be thrown if provider is not permitted.
		try{gpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
		try{networkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

		networkLocationListener = new NetworkLocationListener();
		gpsLocationListener = new GpsLocationListener();

		if(gpsEnabled) {
			Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(lastLocation != null && System.currentTimeMillis() - lastLocation.getTime() < freshnessInSeconds * 1000) {
				showDebugToast("Using last GPS location");
				runTask(lastLocation);
				return;
			}
		}

		timer.schedule(new GiveUpTimerTask(), maxTimeInSeconds * 1000);
		if(gpsEnabled) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
		}
		if(networkEnabled) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);
		}
	}

	private class GpsLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
		@Override
		public void onProviderEnabled(String arg0) {}
		@Override
		public void onProviderDisabled(String arg0) {}

		@Override
		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(networkLocationListener);
			locationManager.removeUpdates(this);
			timer.cancel();
			
			showDebugToast("Using GPS location");

			runTask(location);
		}
	}

	private class NetworkLocationListener implements LocationListener {
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
		@Override
		public void onProviderEnabled(String arg0) {}
		@Override
		public void onProviderDisabled(String arg0) {}

		@Override
		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(this);
			if(gpsLocationListener != null) {
				locationManager.removeUpdates(gpsLocationListener);
			}

			bestResult = location;
		}
	}

	private class GiveUpTimerTask extends TimerTask {
		@Override
		public void run() {
			locationManager.removeUpdates(gpsLocationListener);
			locationManager.removeUpdates(networkLocationListener);

			if(bestResult != null) {
				showDebugToast("Times up! Using network location");
				
				runTask(bestResult);
			} else {
				// TODO deal with location failure!
			}
		}
	}
	
	public void showDebugToast(final String message) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(activity, "DEBUG: " + message, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void runTask(final Location location) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				task.execute(location);
			}
		});
	}
}
