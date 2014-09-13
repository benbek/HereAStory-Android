package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hereastory.service.api.CurrentUserLocationService;
import com.hereastory.service.impl.CurrentUserLocationServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.LocationUnavailableException;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class CreateStoryActivity extends Activity {
	
	private CurrentUserLocationService locationService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);
		locationService = new CurrentUserLocationServiceImpl();
		// TODO: verify can't press next without description

		setupNextButton();
	}

    private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonNewStoryDescriptionNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	PointOfInterest story = new PointOfInterest();	
            	story.setTitle(getDescription());
            	try {
					story.setLocation(getLocation());
				} catch (LocationUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = new Intent(getApplicationContext(), CapturePictureActivity.class);
    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
    			startActivity(intent);
            }
        });
	}
    
	private String getDescription() {
		EditText descriptionEditText = (EditText) findViewById(R.id.editTextNewStoryDescription);
		String description = descriptionEditText.getText().toString();
		return description;
	}
	
	private PointLocation getLocation() throws LocationUnavailableException {
		Location location = locationService.getLocation(this);
		return new PointLocation(location.getLatitude(), location.getLongitude(), null); 
	}

}
