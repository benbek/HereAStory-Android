package com.hereastory;

import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
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
	private DatabaseService databaseService;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		databaseService = DatabaseServiceFactory.getDatabaseService();
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		latitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LAT);
		longitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LONG);
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
	}

	private void createStoryActivity() {
		Intent intent = new Intent(this, CreateStoryActivity.class);
		intent.putExtra(IntentConsts.CURRENT_LAT, latitude);
		intent.putExtra(IntentConsts.CURRENT_LONG, longitude);
		startActivity(intent);
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Button loginButton = (Button) findViewById(R.id.authButton);
			loginButton.setVisibility(View.INVISIBLE);
			
			if (!loggedIn.getAndSet(true)) {
				ParseFacebookUtils.logIn(LoginActivity.this, new LogInCallback() {
					
					@Override
					public void done(ParseUser user, ParseException e) {
						if (e == null) {
							saveFacebookInfo();
						} else {
							Log.e(LOG_TAG, "error loggin in", e);
						}
					}
				});
			}
			createStoryActivity();
		}
	}

	private void saveFacebookInfo() {		
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser graphUser, Response response) {
				if (graphUser != null) {
					new DownloadProfilePictureTask().execute(new String[] { graphUser.getId(), graphUser.getName() });
				} else if (response.getError() != null) {
					if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
							|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
						Log.d(LOG_TAG, "The facebook session was invalidated.");
					} else {
						Log.e(LOG_TAG, "Some other error: " + response.getError().getErrorMessage());
					}
				}
			}

		});
		request.executeAsync();
	}

	private class DownloadProfilePictureTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String facebookId = params[0];
			String name = params[1];
			String url = "https://graph.facebook.com/" + facebookId + "/picture";
			try {
				byte[]  profilePicture = IOUtils.toByteArray(new URL(url).openConnection().getInputStream());
				databaseService.addFacebookUser(facebookId, name, profilePicture);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed saving user", e);
			}
			return null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
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
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
    }
}
