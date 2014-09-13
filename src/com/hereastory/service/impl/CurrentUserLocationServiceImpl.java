package com.hereastory.service.impl;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.hereastory.service.api.CurrentUserLocationService;
import com.hereastory.shared.LocationUnavailableException;

public class CurrentUserLocationServiceImpl implements CurrentUserLocationService, GooglePlayServicesClient.ConnectionCallbacks {
	
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
    @Override
    public Location getLocation(Activity activity) throws LocationUnavailableException {
    	LocationClientHandler handler = new LocationClientHandler(activity);
        LocationClient locationClient = new LocationClient(activity, this, handler);
        locationClient.connect();
        Location location;
		try {
			location = getLocation(activity, locationClient);
		} finally {
			locationClient.disconnect();
		}
        return location;
    }
    
    @Override
    public Location getLocation(Activity activity, LocationClient locationClient) throws LocationUnavailableException {
    	if (!servicesConnected(activity)) {
    		throw new LocationUnavailableException();
    	}
    	LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
    	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Do what you need if enabled...
    		// TODO
        } else{
            //Do what you need if not enabled...
        	// TODO
        }
		return locationClient.getLastLocation(); //TODO could be null
	}

	private boolean servicesConnected(Activity activity) {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        // Google Play services was not available for some reason.
        // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
            	errorDialog.show();
            } else {
            	// TODO
            }
            return false;
        }
    }
	
	private static class LocationClientHandler implements GooglePlayServicesClient.OnConnectionFailedListener {
		
		private Activity activity;
		
		private LocationClientHandler(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			/*
	         * Google Play services can resolve some errors it detects.
	         * If the error has a resolution, try sending an Intent to
	         * start a Google Play services activity that can resolve
	         * error.
	         */
	        if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(activity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the
	             * user with the error.
	             */
	            //TODO showErrorDialog(connectionResult.getErrorCode());
	        }
			
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
