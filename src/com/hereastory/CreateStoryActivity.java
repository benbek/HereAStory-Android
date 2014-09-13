package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class CreateStoryActivity extends Activity {
	
	private static final String LOG_TAG = "CreateStoryActivity";
	private static PointOfInterest story;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);
		
		double latitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LAT);
		double longitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LONG);
		story = new PointOfInterest();
		story.setLocation(new PointLocation(latitude, longitude, null));
		
		// TODO: verify can't press next without description

		setupNextButton();
	}

    private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonNewStoryDescriptionNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	story.setTitle(getDescription());
            	
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

}
