package com.hereastory;

import com.hereastory.ui.SystemUiHiderActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.os.Bundle;

/**
 * The main map activity.
 */
public class MapActivity extends SystemUiHiderActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Get a handle to the Map Fragment
        GoogleMap map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        // Set location to Safra Campus, Jerusalem: (31.774476,35.203543)
        LatLng jerusalem = new LatLng(31.774476, 35.203543);

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(jerusalem, 13));

        map.addMarker(new MarkerOptions()
                .title("Givat Ram")
                .snippet("Where geeks prosper.")
                .position(jerusalem));

        super.setupUiHide(findViewById(R.id.map), findViewById(R.id.fullscreen_content_controls), R.id.dummy_button);
    }

}