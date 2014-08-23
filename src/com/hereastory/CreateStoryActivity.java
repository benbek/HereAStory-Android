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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);
		// TODO: verify can't press next without description

		setupNextButton();
	}

    private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonNewStoryDescriptionNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	PointOfInterest story = new PointOfInterest();	
            	story.setDescription(getDescription());
            	story.setLocation(getLocation());
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
	
	private PointLocation getLocation() {
		return new PointLocation(1.0, 2.0); // TODO
	}

}
