package com.hereastory.shared;

import org.json.JSONObject;

import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class HereAStoryAnalytics {
	
	public static final String MIXPANEL_TOKEN = "4d7517291e7a9803d822b0d868d58141";
	
	private MixpanelAPI mixpanelAPIInstance;
	
	private HereAStoryAnalytics(Context context) {
		mixpanelAPIInstance = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
	}
	
	public static HereAStoryAnalytics getInstanceForContext(Context context) {
		return new HereAStoryAnalytics(context);
	}
	
	public void track(String eventName) {
		mixpanelAPIInstance.track(eventName, new JSONObject());
	}
	
	public void track(String eventName, JSONObject props) {
		mixpanelAPIInstance.track(eventName, props);
	}
	
	public void registerSuperProperties(JSONObject props) {
		mixpanelAPIInstance.registerSuperProperties(props);
	}

	public void flush() {
		mixpanelAPIInstance.flush();
	}
}
