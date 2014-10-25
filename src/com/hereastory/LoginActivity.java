package com.hereastory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hereastory.service.api.UserService;
import com.hereastory.service.impl.UserServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	protected static final String LOG_TAG = "LoginActivity";
	protected static final AtomicBoolean loggedIn = new AtomicBoolean(false); 
	private static double latitude;
	private static double longitude;
	private Button loginButton;

	private UserService userService;
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		userService = new UserServiceImpl();
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		latitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LAT);
		longitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LONG);
		loginButton = (Button) findViewById(R.id.authButton);
		/*loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		})*/;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		//ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		List<String> permissions = Arrays.asList("public_profile");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d(LOG_TAG, "User cancelled the Facebook login");
					mapActivity();
				} else {
					Log.d(LOG_TAG, "User logged in through Facebook");
					ParseFacebookUtils.link(ParseUser.getCurrentUser(), LoginActivity.this);
					userService.saveFacebookInfo();
					createStoryActivity();
				}
			}
		});
	}

	private void createStoryActivity() {
		Intent intent = new Intent(this, CreateStoryActivity.class);
		intent.putExtra(IntentConsts.CURRENT_LAT, latitude);
		intent.putExtra(IntentConsts.CURRENT_LONG, longitude);
		startActivity(intent);
	}
	
	private void mapActivity() {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	   if (state.isOpened()) {
	        Log.i(LOG_TAG, "Logged in...");
			if (!loggedIn.getAndSet(true)) {
	        	ParseFacebookUtils.link(ParseUser.getCurrentUser(), LoginActivity.this);
	        	userService.saveFacebookInfo();
	        }
	        createStoryActivity();
	   }
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
}
