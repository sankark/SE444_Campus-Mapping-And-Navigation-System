package org.rit.classmap.schedule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.rit.classmap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class DisplayScheduleActivity extends Activity {
	
	/**
	 * Launch a new Input Schedule screen
	 * @param v
	 */
	public void headerAddClick( View v )
	{
		startActivity(new Intent(this, InputScheduleActivity.class));
	}
	
	private JSONObject readFile( )
	{
		String tmp = "";
		try 
		{
			FileInputStream fis = getApplicationContext().openFileInput( "scheduleData.json" );
			InputStreamReader r = new InputStreamReader( fis );
			BufferedReader br = new BufferedReader( r );
			StringBuilder sb = new StringBuilder();
			String line = null;
			while( ( line = br.readLine() ) != null )
			{
				sb.append( line + "\n" );
			}
			fis.close();
			r.close();
			br.close();
			tmp = sb.toString();
			JSONObject schedule = new JSONObject();
			return schedule;
		} 
		catch ( Exception e )
		{
			
		}
		return null;
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
