package com.hereastory;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;

public class HereAStoryApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "DiS4CUEwi42XAIbvRVIXQdQhUrAHrodsIMM4jUn5", "1N0FlIzLSyEckm3KFzaDcSiPwk9y3Oqk19CKC5aT");
		//ParseUser.enableAutomaticUser();
		ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));
	    ParseACL defaultACL = new ParseACL();
	    defaultACL.setPublicReadAccess(true);
	    defaultACL.setPublicWriteAccess(true);
	    ParseACL.setDefaultACL(defaultACL, true);		
		Crashlytics.start(this);
	}
}
