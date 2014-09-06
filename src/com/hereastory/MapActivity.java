package com.hereastory;

import com.hereastory.ui.SystemUiHiderActivity;
import com.androidmapsextensions.GoogleMap.OnMarkerClickListener;
import com.androidmapsextensions.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;

/**
 * The main map activity.
 */
public class MapActivity extends SystemUiHiderActivity implements OnMarkerClickListener {

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
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getExtendedMap();

        // Set location to Safra Campus, Jerusalem: (31.774476,35.203543)
        LatLng jerusalem = new LatLng(31.774476, 35.203543);
        
        map.setMyLocationEnabled(true);
        
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(jerusalem, 13));

        map.setOnMarkerClickListener(this);
        
        map.addMarker(new MarkerOptions()
                .title("Givat Ram")
                .snippet("Where geeks prosper.")
                .flat(true)
                .rotation(20)
                .position(jerusalem));

        super.setupUiHide(findViewById(R.id.map), findViewById(R.id.fullscreen_content_controls), R.id.dummy_button);
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
		// TODO Auto-generated method stub
		return false;
	}
}