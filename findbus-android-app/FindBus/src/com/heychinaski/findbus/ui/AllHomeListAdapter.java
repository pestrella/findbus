package com.heychinaski.findbus.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.heychinaski.findbus.R;
import com.heychinaski.findbus.model.Stop;

public class AllHomeListAdapter extends ArrayAdapter<String>{
	
	public AllHomeListAdapter(Context context, List<Stop> stops) {
		super(context, R.layout.stops_list_entry, R.id.stopsListText, extractEntriesFromStops(stops));
	}

	private static List<String> extractEntriesFromStops(List<Stop> stops) {
		List<String> entries = new ArrayList<String>();
		for(int i = 0; i < stops.size(); i++) {
			Stop stop = stops.get(i);
			List<String> stopRoutes = stop.getRoutes();
			
			if(!entries.contains(stop.getName())) {
				entries.add(stop.getName());
			}
			
			for(int j = 0; j < stopRoutes.size(); j++) {
				String route = stopRoutes.get(j);
				if(!entries.contains(route)) {
					entries.add(route);
				}
			}
		}
		
		
		return entries;
	}
}
