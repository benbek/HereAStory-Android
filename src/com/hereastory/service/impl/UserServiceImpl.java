package com.hereastory.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.UserService;
import com.parse.ParseFacebookUtils;

public class UserServiceImpl implements UserService {

	private static final String LOG_TAG = "UserServiceImpl";

	private DatabaseService databaseService;
	private final BitmapService bitmapService;

	public UserServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
		this.bitmapService = new BitmapServiceImpl();
	}

	@Override
	public void saveFacebookInfo() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser graphUser, Response response) {
				if (graphUser != null) {
					new DownloadProfilePictureTask().execute(new String[] {graphUser.getId(), graphUser.getName()});
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
			byte[] profilePicture = null;
			try {
				profilePicture = bitmapService.downloadBitmap(url);
			} catch (Exception e) {
				Log.e(LOG_TAG, "Failed downloading profile picture", e);
			}
			databaseService.addFacebookUser(facebookId, name, profilePicture);
			return null;
		}

	}
}
