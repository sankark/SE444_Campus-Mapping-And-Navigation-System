package org.rit.classmap.social;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rit.classmap.map.MapActivity.DirectionsReceiver;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class SocialLocationsService extends IntentService{

	public SocialLocationsService() {
		super("SocialLocationsService");
		
	}

	public final static String PARAM_SOCIAL_MSG = "ism";
	@Override
	protected void onHandleIntent(Intent intent) {
		Double lat = intent.getDoubleExtra("lat", 0);
		Double lng = intent.getDoubleExtra("lng", 0);
		setCoordinates(lat, lng);
		getEventsToday();
		Intent bcInt = new Intent();
		bcInt.setAction(DirectionsReceiver.ACTION_RESP);
		bcInt.addCategory(Intent.CATEGORY_DEFAULT);
		bcInt.putStringArrayListExtra("info", ev);
		bcInt.putExtra("lats", lats.toArray());
		bcInt.putExtra("lngs", lngs.toArray());
		sendBroadcast(bcInt);
		
	}
	private ArrayList<String> ev;
	private ArrayList<Double> lats;
	private ArrayList<Double> lngs;
	private List<String> eids;
	
	private double lat;
	private double lng;
	

	
	// Sets the user's current location when called.
	public void setCoordinates(double latitude, double longitude){
		lat = latitude;
		lng = longitude;
	}
	
	// Publishes a list of valid events to potentially populate the map with.
	private void getEventsToday() {
		// Dates for FQL queries
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today = df.format(c.getTime()) + "T00:00:00";
		String tomorrow = "";
		
		try{
			tomorrow = (Integer.parseInt(today.substring(0,1)) + 1 ) + today.substring(2);
		} catch (NumberFormatException nfe) {
			System.err.println( nfe.getMessage() );
		}
		
		// Make FQL query to get event ids and parse them into eids list.
		String fqlQuery = "SELECT eid FROM event "
				+ "WHERE eid IN (SELECT eid FROM event_member WHERE uid = me()) "
				+ "AND start_time > " + today + " AND end_time < " + tomorrow;
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request (session,
			"/fql",
			params,
			HttpMethod.GET,
			new Request.Callback() {
				public void onCompleted(Response response) {
					parseEids(response);
				}
		});
		
		// Make FQL query for each eid in the eids list and parse the Events into the ev List.
		for ( int i = 0; i < eids.size(); i++ ) {
			fqlQuery = "SELECT name, venue, start_time, end_time FROM event "
					+ "WHERE eid=" + eids.get(i);
			params = new Bundle();
			params.putString("q", fqlQuery);
			request = new Request (session,
					"/fql",
					params,
					HttpMethod.GET,
					new Request.Callback() {
						public void onCompleted(Response response) {
							parseEvent(response);
						}
			});
		}
		
		
	}

	// Parses the FQL response and places event ids in the eid list.
	private void parseEids( Response r ) {
		try{
			GraphObject go = r.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");
			
			for ( int i = 0; i < ( arr.length() ); i++ ) {
				JSONObject json_obj = arr.getJSONObject(i);
				// Parse JSON Objects
				eids.add(json_obj.getString("eid"));
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	// Parses the FQL response and places events in the ev list.
	private void parseEvent( Response r ) {
		try{
			boolean addEvent = false;
			GraphObject go = r.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");
			
			for ( int i = 0; i < ( arr.length() ); i++ ) {
				JSONObject json_obj = arr.getJSONObject(i);
				
				// Parse JSON Objects into HashMaps
				String event = "";
				Double latitude = 0.0;
				Double longitude = 0.0;
				event = json_obj.getString("name") + "\n";
				JSONObject evjso = json_obj.getJSONObject("venue");
				try {
					event += evjso.getString("name") + "\n";
					latitude = Double.parseDouble(evjso.getString("latitude"));
					longitude = Double.parseDouble(evjso.getString("longitude"));
				} catch (JSONException jse) {
					// YAY EXCEPTIONS! :D
				} catch (NumberFormatException nfe) {
					// YAY MORE EXCEPTIONS! <3
				}
				event += json_obj.getString("start_time") + "\n";
				event += json_obj.getString("end_time") + "\n";
				
				// Check that the coordinates are within range. If so, add them to the list.
				if(checkCoordinates(latitude,longitude,1000)){
					ev.add(event);
					lats.add(latitude);
					lngs.add(longitude);
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/* 
	 * Checks to see if the coordinates are within a certain radius (in meters).
	 * Returns true if they are within the radius; else, false.
	 */
	private boolean checkCoordinates(double elat, double elng, long radius){
		long r = radius;
		final int R = 6371000;
		
		Double dlat = toRad(elat-lat);
		Double dlng = toRad(elng-lng);
		Double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + 
	               Math.cos(toRad(lat)) * Math.cos(toRad(elat)) * 
	               Math.sin(dlng / 2) * Math.sin(dlng / 2);
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		Double d = R * c;
		
		if(d < r){
			return true;
		}
		
		return false;
	}
	
	// Converts the value of a double to radians.
	private Double toRad(Double value) {
        return value * Math.PI / 180;
	}


	
}
