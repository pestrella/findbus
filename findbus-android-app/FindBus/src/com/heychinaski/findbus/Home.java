package com.heychinaski.findbus;

import java.util.List;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;

import com.heychinaski.findbus.model.Stop;
import com.heychinaski.findbus.service.LocationHelper;
import com.heychinaski.findbus.service.StopService;
import com.heychinaski.findbus.ui.AllHomeListAdapter;
import com.heychinaski.findbus.ui.RoutesListAdapter;
import com.heychinaski.findbus.ui.StopsListAdapter;

public class Home extends TabActivity {
	private static final long MAX_LOCATION_WAIT_SECS = 20;
	private StopService stopService;
	private ListView allList;
	private ListView stopsList;
	private ListView busList;
	private LocationHelper locationService;
	
	private ProgressDialog progressDialog;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TabHost tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.home, tabHost.getTabContentView(), true);

		allList = (ListView) findViewById(R.id.all_list);
		stopsList = (ListView) findViewById(R.id.stops_list);
		busList = (ListView) findViewById(R.id.bus_list);


		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec("All").setIndicator("All").setContent(R.id.all_list));
		tabHost.addTab(tabHost.newTabSpec("Stops").setIndicator("Stops").setContent(R.id.stops_list));
		tabHost.addTab(tabHost.newTabSpec("Buses").setIndicator("Buses").setContent(R.id.bus_list));

		stopService = new StopService();
		load();
	}

	private void load() {
		// TODO strings.xml
		progressDialog = ProgressDialog.show(Home.this, "Please wait", "Getting location", true);
		locationService = new LocationHelper(this, new GetDataAndPopulateTask(), MAX_LOCATION_WAIT_SECS, 300);
		locationService.getLocationThenExecTask();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// TODO strings.xml
		menu.add(0, 0, 0, "Refresh");
    return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case 0: {
				load();
				break;
			}

			default: {
				throw new RuntimeException("Menu item not recognised");
			}
		}
		
		return true;
	}

	private class GetDataAndPopulateTask extends AsyncTask<Location, Void, List<Stop>> {
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading stops");
		}
		
		protected List<Stop> doInBackground(Location ... locations) {
			return stopService.loadLocalStops(locations[0].getLatitude(), locations[0].getLongitude());
		}

		protected void onPostExecute(List<Stop> stops) {
			allList.setAdapter(new AllHomeListAdapter(getApplicationContext(), stops));
			stopsList.setAdapter(new StopsListAdapter(getApplicationContext(), stops));
			busList.setAdapter(new RoutesListAdapter(getApplicationContext(), stops));
			
			progressDialog.dismiss();
		}
	}
}