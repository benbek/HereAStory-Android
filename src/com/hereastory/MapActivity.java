package com.hereastory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.MapFragment;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.hereastory.ui.SystemUiHiderActivity;
import com.parse.ParseException;

/**
 * The main map activity.
 */
public class MapActivity extends SystemUiHiderActivity implements OnMarkerClickListener {
	
	private static final String LOG_TAG = MapActivity.class.getSimpleName();
	
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
				clickedMarker.setTitle(MapActivity.this.getString(R.string.marker_failed_title));
				clickedMarker.setSnippet(MapActivity.this.getString(R.string.marker_failed_snippet));
				clickedMarker.showInfoWindow();
			}
		}

		@Override
		public void readCompleted(PointOfInterest poi) {
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
				addMarker(new MarkerOptions()
					.position(new LatLng(loc.getLatitude(), loc.getLongitude()))
					.data(loc));
			}
		}
	}

	private GoogleMap map;
	
	final PointOfInterestReadHandler markerReader = new POIReader();
	private PointOfInterestService poiService;
	
	protected Map<PointLocation, LimitedPointOfInterest> cachedMarkers = new HashMap<PointLocation, LimitedPointOfInterest>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int gPlayResult = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (gPlayResult != ConnectionResult.SUCCESS) {
        	GooglePlayServicesUtil.getErrorDialog(gPlayResult, this, 0).show();
        	finish();
        }
                
        // Get a handle to the Map Fragment
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getExtendedMap();

        // Set location to Safra Campus, Jerusalem: (31.774476,35.203543)
        LatLng jerusalem = new LatLng(31.774476, 35.203543);
        
        map.setMyLocationEnabled(true);
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(jerusalem, 13));

        map.setOnMarkerClickListener(this);
        
        addMarker(new MarkerOptions()
                .title("Givat Ram")
                .snippet("Where geeks prosper.")
                .position(jerusalem));

        super.setupUiHide(findViewById(R.id.map), findViewById(R.id.fullscreen_content_controls), R.id.record_story_button);
        
        // Here-a-Story services and interfaces
        poiService = new PointOfInterestServiceImpl();
        
        addMarkersAtLocation(jerusalem);
    }

    protected void addMarker(MarkerOptions marker) {
    	map.addMarker(marker.flat(true)
    			.rotation(340));
    }
    
    protected void addMarkersAtLocation(LatLng location) {
		poiService.readAllInArea(location.latitude, location.longitude, 1000, markerReader);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
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
	
	private void recordStoryClick() {
		
	}
	
	protected void showExceptionDialog(Exception e) {
		
	}
}