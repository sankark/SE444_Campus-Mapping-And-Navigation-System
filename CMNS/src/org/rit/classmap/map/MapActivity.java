package org.rit.classmap.map;

/*
 * Contributors: Brian Spates, Michael Yeaple
 * 
 * MainActivity runs within the main UI thread
 * other services and activities must be 
 * created and started from this activity
 * 
 */
import java.util.ArrayList;
import java.util.List;

import org.rit.classmap.R;
import org.rit.classmap.directions.DirectionsActivity;
import org.rit.classmap.directions.DirectionsService;
import org.rit.classmap.schedule.DisplayScheduleActivity;
import org.rit.classmap.social.SocialLocationsService;
import org.rit.classmap.social.SocialLoginActivity;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.Iterator;
import java.util.Map;

@SuppressLint("NewApi")
public class MapActivity extends Activity {

    private GoogleMap map;
    private DirectionsReceiver dirReceiver;
    private SocialReceiver socReceiever;
    private LocLis locer;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        
        //Setup broadcast receiver to collect directions from DirectionsService on another thread
        IntentFilter filter = new IntentFilter(DirectionsReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        socReceiever = new SocialReceiver();
        dirReceiver = new DirectionsReceiver();
        this.registerReceiver(dirReceiver, filter);
        this.registerReceiver(socReceiever, filter);
        //Setup broadcast receiver to collect directions from -----TODO
        // MarkersReceiver markReceiver = new MarkersReceiver();
        Intent intent = new Intent(getApplicationContext(), SocialLocationsService.class);
        intent.putExtra("lng", -77.674428);
        intent.putExtra("lat", 43.084710);
        startService(intent);
        drawMarkers(parseEvents(null, null, null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.schedule:
                launchSchedule();
                return true;

            case R.id.social:
                launchLogin();
                return true;

            case R.id.directions:
                launchDirections();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        this.unregisterReceiver(dirReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(DirectionsReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(dirReceiver, filter);
    }

    //temporary button used for testing purposes
    public void testButton(View view) {

        Intent intent = new Intent(this, DirectionsService.class);
        startService(intent);
    }

    /*
     * nested class used for inter-process communication
     * probably should be replaced with the Local version
     * for security and encapsulation reasons 
     */
    public class DirectionsReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "org.rit.classmap.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context cont, Intent intent) {
            ArrayList<LatLng> result = intent.getParcelableArrayListExtra(DirectionsService.PARAM_OUT_MSG);
            drawDirLines(result);
        }
    }

    public class SocialReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "org.rit.classmap.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context cont, Intent intent) {
            ArrayList<String> infos = intent.getStringArrayListExtra("info");
            double[] lats = intent.getDoubleArrayExtra("lats");
            double[] lngs = intent.getDoubleArrayExtra("lngs");
            if (infos != null) {
                drawMarkers(parseEvents(infos, lats, lngs));
            }
        }
    }

    //draws direction lines on the map
    private void drawDirLines(List<LatLng> points) {
        PolylineOptions dirLines = new PolylineOptions().addAll(points).width(10);
        Polyline line = map.addPolyline(dirLines);
    }

    //Add markers into map
    private void drawMarkers(List<MarkerOptions> markerOptions) {
        Iterator<MarkerOptions> i = markerOptions.iterator();
        while (i.hasNext()) {
            map.addMarker(i.next());
        }
    }

    //Given raw event information (Facebook Response), parse it and create the marker configurations.
    //Precondition: eventList is never null
    private List<MarkerOptions> parseEvents(List<String> eventNames, double[] lats, double[] longs) {

        List<MarkerOptions> opts = new ArrayList<MarkerOptions>();

        if (eventNames != null && lats != null && longs != null) {
            assert ((eventNames.size() == lats.length) & (eventNames.size() == longs.length));

            for (int i = 0; i < eventNames.size(); i++) {
                opts.add(new MarkerOptions()
                        .position(new LatLng(lats[i], longs[i]))
                        .title(eventNames.get(i))
                        .snippet(""));
            }
        } else {
            opts.add(new MarkerOptions()
                    .position(new LatLng(43.084667, -77.679962))
                    .title("Software Usability")
                    .snippet("GCCIS"));
            opts.add(new MarkerOptions()
                    .position(new LatLng(43.084667, -77.680962))
                    .title("Behind GCCIS")
                    .snippet(""));
            opts.add(new MarkerOptions()
                    .position(new LatLng(43.085667, -77.679962))
                    .title("Parking Lot")
                    .snippet("This is a parking lot."));
            opts.add(new MarkerOptions()
                    .position(new LatLng(43.084667, -77.677962))
                    .title("Middle of somewhere")
                    .snippet(""));
            opts.add(new MarkerOptions()
                    .position(new LatLng(43.083667, -77.678962))
                    .title("Lunch, Crossroads")
                    .snippet(""));

        }
        /*LatLng one = new LatLng(43.084667,-77.679962);
         LatLng two = new LatLng(43.084667,-77.679963);
         LatLng three = new LatLng(43.084667,-77.679964);
         opts.add(new MarkerOptions().position(one));
         opts.add(new MarkerOptions().position(two));
         opts.add(new MarkerOptions().position(three));*/

        return opts;
    }

    // Parses the FQL response and places it in the event list.
/*	private void parseFQLResponse( Response r ) {
     try{
     GraphObject go = r.getGraphObject();
     JSONObject jso = go.getInnerJSONObject();
     JSONArray arr = jso.getJSONArray("data");
			
     for ( int i = 0; i < ( arr.length() ); i++ ) {
     JSONObject json_obj = arr.getJSONObject(i);
				
     // TODO: Grab correct objects
     String name = json_obj.getString("name");
     }
     } catch (Throwable t) {
     t.printStackTrace();
     }
     }*/
    //methods to lauch other activities
    public void launchSchedule() {
        startActivity(new Intent(this, DisplayScheduleActivity.class));
    }

    public void launchLogin() {
        startActivity(new Intent(this, SocialLoginActivity.class));
    }

    public void launchDirections() {
        startActivity(new Intent(this, DirectionsActivity.class));
    }

    class LocLis implements OnMyLocationChangeListener {

        private boolean loc = true;

        @Override
        public void onMyLocationChange(Location location) {
            if (loc) {
                Intent intent = new Intent(getApplicationContext(), SocialLocationsService.class);
                intent.putExtra("lat", location.getLatitude());
                intent.putExtra("lng", location.getLongitude());
                startService(intent);
                loc = false;
            }

        }
    }
}
