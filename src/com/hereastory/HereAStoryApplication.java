package com.hereastory;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseUser;

public class HereAStoryApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, "DiS4CUEwi42XAIbvRVIXQdQhUrAHrodsIMM4jUn5", "1N0FlIzLSyEckm3KFzaDcSiPwk9y3Oqk19CKC5aT");

		ParseUser.enableAutomaticUser();
	}
}
