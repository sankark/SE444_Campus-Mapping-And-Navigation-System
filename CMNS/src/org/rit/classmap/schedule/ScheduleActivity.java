package org.rit.classmap.schedule;

import org.rit.classmap.R;
import org.rit.classmap.R.layout;
import org.rit.classmap.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ScheduleActivity extends Activity {
	
	/**
	 * onClick Listener for the add button in the header of the Schedule
	 * view.
	 * @param v
	 */
	public void headerAddClick( View v )
	{
		startActivity(new Intent(this, AlterClassActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}
}
