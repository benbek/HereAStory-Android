package com.hereastory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.impl.BitmapServiceImpl;
import com.hereastory.service.impl.ErrorDialogService;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class CreateStoryActivity extends Activity {
	
	private static final String LOG_TAG = "CreateStoryActivity";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static Uri fileUri;
	private static PointOfInterest story;	
    private OutputFileService outputFileService;
    private BitmapService bitmapService;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_story);
		outputFileService = new OutputFileServiceImpl(this);
		bitmapService = new BitmapServiceImpl();
		
		fileUri = getOutputMediaFileUri(); // create a file to save the image

		story = new PointOfInterest();
		setLocation();
		setupCapturePictureButton();
	}

	private void setLocation() {
		double latitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LAT);
		double longitude = (Double) getIntent().getSerializableExtra(IntentConsts.CURRENT_LONG);
		story.setLocation(new PointLocation(latitude, longitude, null));
	}

    private void setupCapturePictureButton() {
    	final Button capturePictureButton = (Button) findViewById(R.id.buttonNewStoryDescriptionNext);
    	capturePictureButton.setEnabled(false);
        
    	capturePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	capturePicture();
            }
        });
        
        getDescriptionEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		       if (!hasFocus && !getDescription().isEmpty()) {
		    	   capturePictureButton.setEnabled(true);
		        }
			}
		});
	}
    
	private String getDescription() {
		EditText descriptionEditText = getDescriptionEditText();
		String description = descriptionEditText.getText().toString();
		return description;
	}

	private EditText getDescriptionEditText() {
		return (EditText) findViewById(R.id.editTextNewStoryDescription);
	}
	
	private void capturePicture() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
		startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri() {
		// camera has problems saving to the regular media dir
		String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";  
	    File imageFile = new File(imageFilePath); 
	    return  Uri.fromFile(imageFile);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				setImage();
            	story.setTitle(getDescription());

				Intent intent = new Intent(getApplicationContext(), RecordAudioActivity.class);
    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
    			startActivity(intent);
			} else {
				Intent intent = new Intent(getApplicationContext(), MapActivity.class);
				startActivity(intent);	
			}
		}
	}

	private void setImage() {
		File actualFilePath = outputFileService.getOutputMediaFile(FileType.IMAGE); // camera has problems saving to here
		try {
			FileUtils.moveFile(new File(fileUri.getPath()), actualFilePath);
			bitmapService.compress(actualFilePath.getPath());
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_compressing_image, e, getApplicationContext());
		}
		story.setImage(actualFilePath.getAbsolutePath());
	}

}
