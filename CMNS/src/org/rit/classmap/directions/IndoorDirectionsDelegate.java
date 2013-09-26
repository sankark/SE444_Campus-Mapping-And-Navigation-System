package org.rit.classmap.directions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class IndoorDirectionsDelegate {

	private HashMap<String, Vertex> vertexes = new HashMap<String, Vertex>();
	private JSONObject verts;
	private JSONObject edges;
	private List<Vertex> paths;
	private Vertex dest = null;
	private Vertex org = null;
	public IndoorDirectionsDelegate(LatLng source, int or, int destination, JSONObject graph)
	{
		try {
			verts = graph.getJSONObject("vertexes");
			edges = graph.getJSONObject("edges");
			ArrayList<Vertex> entrances = buildDirectedGraph(verts, edges, destination, or);
			//int closest = findClosestEntrance(entrances, source);
			
			//Vertex origin = entrances.get(closest);
			computePaths(org, verts, edges);
			paths = getShortestPathTo(dest);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("json error1 ", e.toString());
		}
	}
	
	public ArrayList<LatLng> execute()
	{
		
		
		ArrayList<LatLng> results  = new ArrayList<LatLng>();
		try {
			for(int i =0; i < paths.size(); i++)
			{
				
				JSONObject temp = verts.getJSONObject(paths.get(i).toString());
				
				results.add(new LatLng(temp.getDouble("lat"), temp.getDouble("lng")));
				
			}
			return results;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("json error 4 ", e.toString());
		}
		return null;
	}
	private ArrayList<Vertex> buildDirectedGraph(JSONObject verts, JSONObject edges, int destination, int or)
	{
		ArrayList<Vertex> entrances = new ArrayList<Vertex>();
		Iterator<String> keys = verts.keys();
		while(keys.hasNext())
		{
			String key = keys.next();
			Vertex temp = new Vertex(key);
			vertexes.put(key, temp);
			try {
				JSONObject node = verts.getJSONObject(key);
				int room = node.getInt("room");
				if(room == -2)
				{
					temp.lat = verts.getJSONObject(key).getDouble("lat");
					temp.lng = verts.getJSONObject(key).getDouble("lng");
					entrances.add(temp);
				}
				else if(room == destination)
				{
					dest = temp;
				}
				else if(room == or)
				{
					org = temp;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("json error 2", e.toString());
			}
		}
		
		
		return entrances;
		
	}
	
	private void computePaths(Vertex source, JSONObject verts, JSONObject edges)
	{
		source.minDistance = 0.;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.add(source);
		while(!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();
			try {
				JSONObject vert = verts.getJSONObject(u.toString());
				Log.i("because", vert.toString(2));
				JSONArray edgeArray;
				
					edgeArray = vert.getJSONArray("edges");
			
				for(int i =0; i < edgeArray.length(); i++)
				{
					JSONObject edge = edges.getJSONObject(edgeArray.getString(i));
					Vertex v = vertexes.get(edge.getString("vertTwoId"));
					double weight = edge.getDouble("distance");
					double distanceThroughU = u.minDistance + weight;
					if(distanceThroughU < v.minDistance)
					{
						vertexQueue.remove(v);
						
						v.minDistance = distanceThroughU;
						v.previous = u;
						vertexQueue.add(v);
					}
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				Log.e("json error 3", e.toString());
			}
		}
		
	}
	
	private List<Vertex> getShortestPathTo(Vertex target)
	{
		List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        return path;
	}
	
	private int findClosestEntrance(ArrayList<Vertex> entrances, LatLng source)
	{
		Double lat = source.latitude;
		Double lng = source.longitude;
		int closest = -1;
		final int R = 6371;
		ArrayList<Double> distances  = new ArrayList<Double>();
		for(int i =0; i < entrances.size(); ++i)
		{
			Double elat = entrances.get(i).lat;
			Double elng = entrances.get(i).lng;
			Double dlat = toRad(elat-lat);
			Double dlng = toRad(elng-lng);
			Double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + 
	                   Math.cos(toRad(lat)) * Math.cos(toRad(elat)) * 
	                   Math.sin(dlng / 2) * Math.sin(dlng / 2);
			Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			Double d = R * c;
			distances.add(i, d);
			if(closest == -1 || d < distances.get(closest))
			{
				closest = i;
			}
		}
		return closest;
		
		
	}
	 private Double toRad(Double value) {
	        return value * Math.PI / 180;
	 }
}
