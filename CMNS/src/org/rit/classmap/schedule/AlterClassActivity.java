package org.rit.classmap.schedule;

import org.rit.classmap.R;
import org.rit.classmap.R.layout;
import org.rit.classmap.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AlterClassActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_schedule);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

}
