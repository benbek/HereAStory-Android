package com.hereastory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnCameraChangeListener;
import com.androidmapsextensions.GoogleMap.OnInfoWindowClickListener;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.GoogleMap.OnMyLocationChangeListener;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.Polyline;
import com.androidmapsextensions.PolylineOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.hereastory.service.api.OutputFileServiceFactory;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.LocationUnavailableException;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.hereastory.shared.WebFetcher;
import com.hereastory.ui.DirectionsFetcher;
import com.hereastory.ui.MarkerClusteringOptionsProvider;
import com.hereastory.ui.OnResponseRetrieved;
import com.hereastory.ui.SystemUiHiderActivity;
import com.parse.ParseException;

/**
 * The main map activity.
 */
public class MapActivity extends SystemUiHiderActivity implements GooglePlayServicesClient.ConnectionCallbacks, OnMarkerClickListener, OnInfoWindowClickListener, OnCameraChangeListener, OnMyLocationChangeListener, OnResponseRetrieved {
	
	private static final String LOG_TAG = MapActivity.class.getSimpleName();
	private static final String ERROR_INFO_WINDOW = "error_info_window";
	
	private final class POIReader implements PointOfInterestReadHandler {
		@Override
		public void readLimitedFailed(String id, Exception e) {
			displayErrorPopup();
			Log.w(LOG_TAG, "ReadLimited failed when trying to get POI with id \"".concat(id).concat("\""), e);
		}

		@Override
		public void readLimitedCompleted(LimitedPointOfInterest poi) {
			Marker clickedMarker = map.getMarkerShowingInfoWindow();
			updateMarkerInfoWindow(clickedMarker, poi.getTitle(), poi.getAuthorName());
			
			cachedMarkers.put((PointLocation) clickedMarker.getData(), poi);
		}

		@Override
		public void readFailed(String id, Exception e) {
			displayErrorPopup();
			Log.w(LOG_TAG, "Read failed when trying to get POI with id \"".concat(id).concat("\""), e);
		}

		private void displayErrorPopup() {
			Marker clickedMarker = map.getMarkerShowingInfoWindow();
			updateMarkerInfoWindow(clickedMarker, MapActivity.this.getString(R.string.marker_failed_title), 
					MapActivity.this.getString(R.string.marker_failed_snippet));
		}

		@Override
		public void readCompleted(PointOfInterest poi) {
			Intent hearStoryIntent = new Intent(MapActivity.this, HearStoryActivity.class);
			hearStoryIntent.putExtra(IntentConsts.STORY_OBJECT, poi);

			startActivityForResult(hearStoryIntent, IntentConsts.HEAR_STORY_CODE);
		}

		@Override
		public void readAllInAreaFailed(double latitude, double longitude,
				double maxDistance, ParseException e) {
			MapActivity.this.showExceptionDialog(e);
			Log.e(LOG_TAG, "ReadAllInArea failed", e);
		}

		@Override
		public void readAllInAreaCompleted(List<PointLocation> points) {
			for (PointLocation loc : points) {
				if (!knownPoints.contains(loc)) {
					addMarker(new MarkerOptions()
						.position(new LatLng(loc.getLatitude(), loc.getLongitude()))
						.data(loc));
					knownPoints.add(loc);
				}
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

	        }
			
		}
	} // End of class LocationFailureHandler
	
	private View recordStoryButton;
	private GoogleMap map;
	
	LocationFailureHandler failureHandler = new LocationFailureHandler(this);
    LocationClient locationClient;
    Location myLastLocation = null;
	
	final PointOfInterestReadHandler markerReader = new POIReader();
	private PointOfInterestService poiService;
	
	private List<Marker> declusterifiedMarkers;
	protected Map<PointLocation, LimitedPointOfInterest> cachedMarkers = new HashMap<PointLocation, LimitedPointOfInterest>();
	protected Set<PointLocation> knownPoints = new HashSet<PointLocation>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setLocale();
        
        setContentView(R.layout.activity_map);
        
        int gPlayResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (gPlayResult != ConnectionResult.SUCCESS) {
        	GooglePlayServicesUtil.getErrorDialog(gPlayResult, this, 0).show();
        	finish();
        }
        
        locationClient = new LocationClient(this, this, failureHandler);
                
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getExtendedMap();
        
        map.setMyLocationEnabled(true);
        
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationChangeListener(this);

        super.setupUiHide(findViewById(R.id.map), findViewById(R.id.fullscreen_content_controls), R.id.record_story_button);
        
        OutputFileServiceFactory.init(this);
        // Here-a-Story services and interfaces
        poiService = new PointOfInterestServiceImpl();
        
        // Clustering
        ClusteringSettings clusterSettings = new ClusteringSettings();
        /*clusterSettings.clusterOptionsProvider(new ClusterOptionsProvider() {
			
			@Override
			public ClusterOptions getClusterOptions(List<Marker> markers) {
				float hue;
                if (markers.get(0).getClusterGroup() == STORIES_COLLECTION) {
                    hue = BitmapDescriptorFactory.HUE_ORANGE;
                } else {
                    hue = BitmapDescriptorFactory.HUE_GREEN;
                }
                BitmapDescriptor blueIcon = BitmapDescriptorFactory.defaultMarker(hue);
                return new ClusterOptions().icon(blueIcon);
			}
		}).clusterSize(20);*/
        clusterSettings.clusterOptionsProvider(new MarkerClusteringOptionsProvider(getResources()));
        map.setClustering(clusterSettings);
        
        Location myLocation = map.getMyLocation();

        if (myLocation != null) {
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 13));
        	this.myLastLocation = myLocation;
        } else if (savedInstanceState == null) {
        	// Set location to Safra Campus, Jerusalem: (31.774476,35.203543)
        	LatLng jerusalem = new LatLng(32.0866403, 34.7778583);
        	map.moveCamera(CameraUpdateFactory.newLatLngZoom(jerusalem, 13));
        }
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
    	BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.single_marker);
		map.addMarker(marker.icon(markerIcon)
    			/*.rotation(340)*/);
    }
    
    protected void addMarkersAtLocation(LatLng location, float zoom) {
		poiService.readAllInArea(location.latitude, location.longitude, Math.max(50, 100 * (11.0 - zoom)), markerReader);
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
		for (Polyline polyline : map.getPolylines()) {
			polyline.remove();
		}
		
		if (clickedMarker.isCluster()) {
			declusterify(clickedMarker);
            return true;
		}
		
		PointLocation loc = clickedMarker.getData();
		if (loc != null) {			
			clickedMarker.setTitle(this.getString(R.string.marker_loading_title));
			clickedMarker.setSnippet(""); // Clear out old values
			if (cachedMarkers.containsKey(loc)) {
				clickedMarker.showInfoWindow();
				markerReader.readLimitedCompleted(cachedMarkers.get(loc));
			} else {
				poiService.readLimited(loc.getPointOfInterestId(), markerReader);
			}
		    fetchWalkingPoints(loc);
		}
		
		return false;
	}

	private void fetchWalkingPoints(PointLocation destination) {
		if (myLastLocation == null)
			return;
		
		String directionsUrl = DirectionsFetcher.makeUrl(myLastLocation.getLatitude(), 
				myLastLocation.getLongitude(), destination.getLatitude(), destination.getLongitude());
		
		WebFetcher webFetcher = new WebFetcher(this);
		webFetcher.execute(directionsUrl);
	}
	
	public void onResponseRetrieved(String jsonDirections) {
		List<LatLng> walkingPoints;
		PolylineOptions polylineOptions = new PolylineOptions();
	    try {
			walkingPoints = DirectionsFetcher.fetchPath(jsonDirections);
		} catch (JSONException e) {
			return;
		}
	    polylineOptions.addAll(walkingPoints);
		/*LatLng last = null;
		for (int i = 0; i < list.size()-1; i++) {
		    LatLng src = list.get(i);
		    LatLng dest = list.get(i+1);
		    last = dest;
		    Polyline line = googleMap.addPolyline(new PolylineOptions().add( 
		            new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));
		}*/
		map.addPolyline(polylineOptions.width(3).color(Color.BLUE));
	}
	
    private void declusterify(Marker cluster) {
        clusterifyMarkers();
        declusterifiedMarkers = cluster.getMarkers();
        //LatLng clusterPosition = cluster.getPosition();
        //double distance = calculateDistanceBetweenMarkers();
        //double currentDistance = -declusterifiedMarkers.size() / 2 * distance;
        for (Marker marker : declusterifiedMarkers) {
            marker.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
            //LatLng newPosition = new LatLng(clusterPosition.latitude, clusterPosition.longitude + currentDistance);
            PointLocation originalPosition = (PointLocation)marker.getData();
            marker.animatePosition(new LatLng(originalPosition.getLatitude(), originalPosition.getLongitude()));
            //currentDistance += distance;
        }
    }
	
    private double calculateDistanceBetweenMarkers() {
        Projection projection = map.getProjection();
        Point point = projection.toScreenLocation(new LatLng(0.0, 0.0));
        point.x += getResources().getDimensionPixelSize(R.dimen.distance_between_markers);
        LatLng nextPosition = projection.fromScreenLocation(point);
        return nextPosition.longitude;
    }
	
    private void clusterifyMarkers() {
        if (declusterifiedMarkers != null) {
            for (Marker marker : declusterifiedMarkers) {
            	PointLocation location = marker.getData();
                marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
                marker.setClusterGroup(ClusterGroup.DEFAULT);
            }
            declusterifiedMarkers = null;
        }
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
            }
            return false;
        }
    }
	
	public void recordStoryClick(View view) {
		this.recordStoryButton = view;
        view.setEnabled(false);
		if (myLastLocation != null) {
			startCreateStory(myLastLocation);
		} else {
			locationClient.connect();
		}
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
		
		startCreateStory(location);
	}

	private void startCreateStory(Location location) {
		Intent createStoryIntent = new Intent(this, LoginActivity.class);
		createStoryIntent.putExtra(IntentConsts.CURRENT_LAT, location.getLatitude());
		createStoryIntent.putExtra(IntentConsts.CURRENT_LONG, location.getLongitude());

		startActivityForResult(createStoryIntent, IntentConsts.CREATE_STORY_CODE);
		this.recordStoryButton.setEnabled(true);
	}

	@Override
	public void onDisconnected() {
		
	}

	@Override
	public void onInfoWindowClick(Marker origin) {
		if (origin.getData().toString() == ERROR_INFO_WINDOW) {
			origin.hideInfoWindow();
		} else {
			PointLocation data = origin.getData();
			poiService.read(data.getPointOfInterestId(), markerReader);
		}
	}

	private void updateMarkerInfoWindow(Marker clickedMarker, String infoWindowTitle, String infoWindowSnippet) {
		if (clickedMarker != null) {
			clickedMarker.hideInfoWindow();
			clickedMarker.setTitle(infoWindowTitle);
			clickedMarker.setSnippet(infoWindowSnippet);
			clickedMarker.showInfoWindow();
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		addMarkersAtLocation(position.target, position.zoom);
		//clusterifyMarkers();
	}

	@Override
	public void onMyLocationChange(Location location) {
		if (location == null)
			return;
		if (myLastLocation == null || (location.getLatitude() != myLastLocation.getLatitude() 
				&& location.getLongitude() != myLastLocation.getLongitude())) {
			map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), 
					location.getLongitude())));
			myLastLocation = location;
		}
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
    }
}
