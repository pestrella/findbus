package com.heychinaski.findbus.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.heychinaski.findbus.R;
import com.heychinaski.findbus.model.Stop;

public class RoutesListAdapter extends ArrayAdapter<String>{
	
	public RoutesListAdapter(Context context, List<Stop> stops) {
		super(context, R.layout.stops_list_entry, R.id.stopsListText, extractRoutesFromStops(stops));
	}

	private static List<String> extractRoutesFromStops(List<Stop> stops) {
		List<String> routes = new ArrayList<String>();
		for(int i = 0; i < stops.size(); i++) {
			List<String> stopRoutes = stops.get(i).getRoutes();
			for(int j = 0; j < stopRoutes.size(); j++) {
				String route = stopRoutes.get(j);
				if(!routes.contains(route)) {
					routes.add(route);
				}
			}
		}
		
		
		return routes;
	}
}
