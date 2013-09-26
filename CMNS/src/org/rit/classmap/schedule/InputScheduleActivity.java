package org.rit.classmap.schedule;

import org.rit.classmap.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class InputScheduleActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_schedule_item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

}
