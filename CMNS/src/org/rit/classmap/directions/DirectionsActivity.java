package org.rit.classmap.directions;

import org.rit.classmap.R;
import org.rit.classmap.R.layout;
import org.rit.classmap.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Spinner;

public class DirectionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_directions);
		Spinner spin = (Spinner) findViewById(R.id.origin_building);
		
	}

	public void getDirections(View view)
	{
		Intent intent  = new Intent(this, DirectionsService.class);
		intent.putExtra("building", 70);
		intent.putExtra("room", 1610);
		startService(intent);
		finish();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.directions, menu);
		return true;
	}

	
	public void populateHistory()
	{
		
	}
	
	public void populateClassRooms()
	{
		
	}
}
