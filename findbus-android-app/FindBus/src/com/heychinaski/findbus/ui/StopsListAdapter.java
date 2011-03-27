package com.heychinaski.findbus.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.heychinaski.findbus.R;
import com.heychinaski.findbus.model.Stop;

public class StopsListAdapter extends ArrayAdapter<Stop> {
	final private List<Stop> stops;

	public StopsListAdapter(Context context, List<Stop> stops) {
		super(context, R.layout.stops_list_entry, R.id.stopsListText, stops);
		this.stops = stops;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView textView = (TextView)super.getView(position, convertView, parent);
		Stop stop = getItem(position);
		
		String display = stop.getName();
		
		List<String> routes = stop.getRoutes();
		if(routes.size() > 0) {
			display += " (";
			for(int i = 0; i < routes.size(); i++) {
				if(i > 0) {
					display += ", ";
				}
				display += routes.get(i);
			}
			
			display += ")";
		}
		textView.setText(display);
		
		return textView;
	}
}
