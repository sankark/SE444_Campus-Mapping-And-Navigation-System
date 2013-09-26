package org.rit.classmap.social;

/**
 * Contributors: Michael Yeaple
 * 
 * This Activity implements the Facebook login functionality so that
 * local events can be placed on the map for the user.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rit.classmap.R;
import org.rit.classmap.R.layout;
import org.rit.classmap.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.content.Intent;

import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;

public class SocialLoginActivity extends Activity {

	private String TAG = "SocialLoginActivity";
	private TextView welcome;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_social_login);
		
		welcome = (TextView) findViewById(R.id.welcome);
		LoginButton authButton = (LoginButton) this.findViewById(R.id.authButton);
		authButton.setReadPermissions(Arrays.asList("user_events","friends_events"));
		authButton.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public void onError(FacebookException fe) {
				Log.i(TAG, "Error " + fe.getMessage());
			}
		});
		authButton.setSessionStatusCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {
					Log.i(TAG,"Access Token" + session.getAccessToken());
					Request.executeMeRequestAsync(session,
							new Request.GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user, Response response) {
									if (user != null) {
										Log.i(TAG,"User ID " + user.getId());
										Log.i(TAG,"Email " + user.asMap().get("email"));
										welcome.setText("Welcome, " + user.getName() + "!");
									}
								}
					});
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.social_login, menu);
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

}
