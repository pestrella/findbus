package com.heychinaski.findbus.service;

import static java.lang.String.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.heychinaski.findbus.model.Stop;

public class StopService {
	
	private static String DUMMY_DATA = "[{'routes': ['7', '15', '23'], 'latitude': 51.516370000000002, 'code': '14020', 'name': 'PADDINGTON STATION ', 'longitude': -0.174314}, {code: 'R0853', name: 'SOMEWHERE ELSE', routes: ['30', '205'], latitude: -0.119833, longitude: 51.517663}]";

	public List<Stop> loadLocalStops(double latitude, double longitude) {
		List<Stop> stops = null;
		
		InputStream content = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		try {
			response = httpclient.execute(new HttpGet(format("http://find-bus.appspot.com/bus_stop/@location/%f,%f", latitude, longitude)));
			content = response.getEntity().getContent();

			BufferedReader r = new BufferedReader(new InputStreamReader(content));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}

			JSONArray stopsJson = (JSONArray) new JSONTokener(total.toString()).nextValue();
//			JSONArray stopsJson = (JSONArray) new JSONTokener(DUMMY_DATA).nextValue();
			stops = new ArrayList<Stop>(stopsJson.length());
			for(int i = 0; i < stopsJson.length(); i++) {
				JSONObject stopJson = stopsJson.getJSONObject(i);

				Stop newStop = stopFromJson(stopJson);
				
				stops.add(newStop);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stops;
	}

	private Stop stopFromJson(JSONObject stopJson) throws JSONException {
		Stop newStop = new Stop();
		newStop.setName(stopJson.getString("name"));
		newStop.setLatitude(stopJson.getDouble("latitude"));
		newStop.setLongitude(stopJson.getDouble("longitude"));
		List<String> routes = new ArrayList<String>();
		JSONArray routesJson = stopJson.getJSONArray("routes");
		for(int j = 0; j < routesJson.length(); j++) {
			routes.add(routesJson.getString(j));
		}
		
		newStop.setRoutes(routes);
		return newStop;
	}
}
