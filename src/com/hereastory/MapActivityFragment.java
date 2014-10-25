package com.hereastory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnInfoWindowClickListener;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.hereastory.MapActivity.PlaceholderFragment;
import com.hereastory.service.api.OutputFileServiceFactory;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.LocationUnavailableException;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
//import com.hereastory.ui.SystemUiHiderActivity;
import com.parse.ParseException;

/**
 * The main map activity.
 */
public class MapActivityFragment extends Activity //SystemUiHiderActivity 
	implements GooglePlayServicesClient.ConnectionCallbacks,
			   OnMarkerClickListener, 
			   OnInfoWindowClickListener{
	
	private static final String LOG_TAG = MapActivityFragment.class.getSimpleName();

	private final class POIReader implements PointOfInterestReadHandler {
		@Override
		public void readLimitedFailed(String id, Exception e) {
			displayErrorPopup();
			Log.w(LOG_TAG, "ReadLimited failed when trying to get POI with id \"".concat(id).concat("\""), e);
		}

		@Override
		public void readLimitedCompleted(LimitedPointOfInterest poi) {
			Marker clickedMarker = map.getMarkerShowingInfoWindow();
			clickedMarker.setTitle(poi.getTitle());
			clickedMarker.setSnippet(poi.getAuthor().getName());
			
			cachedMarkers.put((PointLocation) clickedMarker.getData(), poi);
		}

		@Override
		public void readFailed(String id, Exception e) {
			displayErrorPopup();
			Log.w(LOG_TAG, "Read failed when trying to get POI with id \"".concat(id).concat("\""), e);
		}

		private void displayErrorPopup() {
			Marker clickedMarker = map.getMarkerShowingInfoWindow();
			if (clickedMarker != null) {
				clickedMarker.hideInfoWindow();
				clickedMarker.setTitle(MapActivityFragment.this.getString(R.string.marker_failed_title));
				clickedMarker.setSnippet(MapActivityFragment.this.getString(R.string.marker_failed_snippet));
				clickedMarker.showInfoWindow();
			}
		}

		@Override
		public void readCompleted(PointOfInterest poi) {
		}

		@Override
		public void readAllInAreaFailed(double latitude, double longitude,
				double maxDistance, ParseException e) {
			MapActivityFragment.this.showExceptionDialog(e);
			Log.e(LOG_TAG, "ReadAllInArea failed", e);
		}

		@Override
		public void readAllInAreaCompleted(List<PointLocation> points) {
			for (PointLocation loc : points) {
				addMarker(new MarkerOptions()
					.position(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.data(loc));
			}
		}
	} // End of class POIReader

	private static class LocationFailureHandler implements GooglePlayServicesClient.OnConnectionFailedListener {
	    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

		private Activity activity;
		
		private LocationFailureHandler(Activity activity) {
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
	} // End of class LocationFailureHandler
	
	private View recordStoryButton;
	private GoogleMap map;
	
	LocationFailureHandler failureHandler = new LocationFailureHandler(this);
    LocationClient locationClient;
	
	final PointOfInterestReadHandler markerReader = new POIReader();
	private PointOfInterestService poiService;
	
	protected Map<PointLocation, LimitedPointOfInterest> cachedMarkers = new HashMap<PointLocation, LimitedPointOfInterest>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setLocale();
        setContentView(R.layout.fragment_map);
        
        int gPlayResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (gPlayResult != ConnectionResult.SUCCESS) {
        	GooglePlayServicesUtil.getErrorDialog(gPlayResult, this, 0).show();
        	finish();
        }
  
        locationClient = new LocationClient(this, this, failureHandler);
                
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager()
               .findFragmentById(R.id.map)).getExtendedMap();

        
        // Set location to Safra Campus, Jerusalem: (31.774476,35.203543)
        LatLng jerusalem = new LatLng(31.774476, 35.203543);
        
        map.setMyLocationEnabled(true);
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(jerusalem, 13));

        map.setOnMarkerClickListener(this);
        
       //TODO Tomer2Ben: what is this good for?
       //super.setupUiHide(findViewById(R.id.map), findViewById(R.id.fullscreen_content_controls), R.id.record_story_button);
        
        OutputFileServiceFactory.init(this);
        // Here-a-Story services and interfaces
        poiService = new PointOfInterestServiceImpl();
        
        addMarkersAtLocation(jerusalem);
    }

	/**
	 * Sets the language of the activity and thus, the map's.
	 */
	private void setLocale() {
		Locale locale = new Locale(this.getString(R.string.default_map_locale));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
              getBaseContext().getResources().getDisplayMetrics());
	}

    protected void addMarker(MarkerOptions marker) {
    	map.addMarker(marker.flat(true)
    			/*.rotation(340)*/);
    }
    
    protected void addMarkersAtLocation(LatLng location) {
		poiService.readAllInArea(location.latitude, location.longitude, 1000, markerReader);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	setLocale();
        int gPlayResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (gPlayResult != ConnectionResult.SUCCESS) {
        	GooglePlayServicesUtil.getErrorDialog(gPlayResult, this, 0).show();
        }
    }

	@Override
	public boolean onMarkerClick(final Marker clickedMarker) {
		PointLocation loc = clickedMarker.getData();
		if (loc != null) {
			clickedMarker.setTitle(this.getString(R.string.marker_loading_title));
			clickedMarker.setSnippet(""); // Clear out old values
			poiService.readLimited(loc.getPointOfInterestId(), markerReader);
		}
		
		return false;
	}
	
	protected void showExceptionDialog(Exception e) {
	     AlertDialog.Builder builder = new AlertDialog.Builder(this);
	     builder.setMessage(e.getMessage())
	            .setCancelable(false)
	            /*.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                }
	            })
	            .setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                     dialog.cancel();
	                }
	            });*/
	            .setNeutralButton(this.getString(R.string.close_exception_dialog), new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                     dialog.cancel();
	                }
	            });
	     AlertDialog alert = builder.create();
         alert.show();
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
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, LocationFailureHandler.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
            	errorDialog.show();
            } else {
            	// TODO
            }
            return false;
        }
    }
	
	public void recordStoryClick(View view) {
		this.recordStoryButton = view;
        locationClient.connect();
        view.setEnabled(false);
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		if (!servicesConnected(this)) {
			locationClient.disconnect();
			this.recordStoryButton.setEnabled(true);
			return;
		}
		
		// Location is now available
		Location location;
    	LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		try {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				showExceptionDialog(new LocationUnavailableException(this.getString(R.string.gps_unavailable_exception)));
				return;
	        }
	    	location = locationClient.getLastLocation();
	    	if (location == null) {
	    		Log.w(LOG_TAG, "Unable to acquire location after connected to GooglePlayServices");
	    		showExceptionDialog(new LocationUnavailableException(this.getString(R.string.location_unavailable_exception)));
	    		return;
	    	}
		} finally {
			locationClient.disconnect();
			this.recordStoryButton.setEnabled(true);
		}
		
		Intent createStoryIntent = new Intent(this, LoginActivity.class);
		createStoryIntent.putExtra(IntentConsts.CURRENT_LAT, location.getLatitude());
		createStoryIntent.putExtra(IntentConsts.CURRENT_LONG, location.getLongitude());

		startActivityForResult(createStoryIntent, IntentConsts.CREATE_STORY_CODE);
		this.recordStoryButton.setEnabled(true);
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}


}
