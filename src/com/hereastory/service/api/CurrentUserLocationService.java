package com.hereastory.service.api;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.hereastory.shared.LocationUnavailableException;

public interface CurrentUserLocationService {

	Location getLocation(Activity activity) throws LocationUnavailableException;

	Location getLocation(Activity activity, LocationClient locationClient) throws LocationUnavailableException;

}
