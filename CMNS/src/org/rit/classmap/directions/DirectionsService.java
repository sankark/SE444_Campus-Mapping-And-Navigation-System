package org.rit.classmap.directions;

/*
 * Contributors: Brian Spates
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rit.classmap.R;
import org.rit.classmap.map.MapActivity.DirectionsReceiver;

import com.google.android.gms.maps.model.LatLng;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/*
 * Service is responsible for querying directions from Google
 * and parsing them into data usable by the Google Maps api's 
 * drawing methods
 */
public class DirectionsService extends IntentService{

	public static final String PARAM_OUT_MSG = "omsg";
	public DirectionsService()
	{
		super("DiretionsService");
		
	}
	
	/*
	 * Method called on creation of this service
	 * Invokes all other methods in this class
	 * It then generates a broadcast intent to 
	 * deliver the results of its methods to the
	 * main activity. 
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		LatLng one = new LatLng(43.084667,-77.679962);
		LatLng two = new LatLng(43.084710,-77.674428);
		
		/*String jsonResponse = readQueryStream(queryGoogleDirections(one, two));
		if(jsonResponse != null)
		{		
			String polyline = parseJSONForPolyline(jsonResponse);
			if(polyline != null)
			{
				ArrayList<LatLng> points = decodePoly(polyline);
				if(determineDelegation())
				{*/
					JSONObject graph = loadGraph();
					IndoorDirectionsDelegate idd = new IndoorDirectionsDelegate(null, 1550, 1610, graph);
					ArrayList<LatLng> indoorPoints = idd.execute();
					if(indoorPoints != null)
					{
						//indoorPoints.addAll(points);
						//points = indoorPoints;
						//Log.i("okay", points.toString());
					}
				//}
				Intent bcInt = new Intent();
				bcInt.setAction(DirectionsReceiver.ACTION_RESP);
				bcInt.addCategory(Intent.CATEGORY_DEFAULT);
				bcInt.putParcelableArrayListExtra(PARAM_OUT_MSG, indoorPoints);
				sendBroadcast(bcInt);
			}
		//}
		
	//}
	
	
	private boolean determineDelegation()
	{
		
			return true;
	}
	
	//Parses through the JSON String for the one string we need, the overview_polyline: points string
	private String parseJSONForPolyline(String jsonString)
	{
		try {
			JSONObject jObj = new JSONObject(jsonString);
			JSONArray routes = jObj.getJSONArray("routes");
			JSONObject routeOne = routes.getJSONObject(0);
			JSONObject polyline = routeOne.getJSONObject("overview_polyline");
			String points = polyline.getString("points");
			return points;
		} catch (JSONException e) {

			Log.e("JSON Parsing Error", e.toString());
		}
		return null;
	}
	
	//Reads HTTPResponse into a string
	private String readQueryStream(InputStream is)
	{
		String result = "";
	    try{
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    
            }
            is.close();
            result=sb.toString();
            reader.close();
            return result;
	    }catch(Exception e){
	            Log.e("log_tag", "Error converting result "+e.toString());
	    }
	    return null;
	}  
	    
	
	private InputStream queryGoogleDirections(LatLng source, LatLng destination)
	{
		InputStream is = null;
		try {
			String query = "http://maps.googleapis.com/maps/api/directions/json?origin=" + Double.toString(source.latitude) +","+ Double.toString(source.longitude) + "&destination=" + Double.toString(destination.latitude) +","+ Double.toString(destination.longitude) + "&sensor=false&mode=walking";
			Log.i("jah", query);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(new HttpGet(query));
			is = response.getEntity().getContent();
			return is;
		} catch (ClientProtocolException e) {

			Log.e("Google Dir Query error", e.toString());
		} catch (IOException e) {
			Log.e("Google Dir Query error", e.toString());
			e.printStackTrace();
		}
		return null;
		
	}
	
	/*
	 * decodePoly decodes Google's base64 representation of lat & lng direction points
	 * into a list of normal lat & lng
	 */
	private ArrayList<LatLng> decodePoly(String encoded) {

		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((double) lat / 1E5,
				 (double) lng / 1E5);
			poly.add(p);
		}

		return poly;
	}

	private JSONObject loadGraph()
	{
		InputStream is = this.getResources().openRawResource(R.raw.gol70graph);
		String graphString = readQueryStream(is);
		JSONObject graph;
		try {
			graph = new JSONObject(graphString);
			return graph;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}
