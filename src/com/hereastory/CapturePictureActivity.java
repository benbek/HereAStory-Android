package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.impl.BitmapServiceImpl;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class CapturePictureActivity extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static Uri fileUri;
	
    private OutputFileService outputFileService;
    private BitmapService bitmapService;
	private PointOfInterest story;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture_picture);
		outputFileService = new OutputFileServiceImpl();
		bitmapService = new BitmapServiceImpl();
		
		Intent intent = getIntent();
		story = (PointOfInterest) intent.getSerializableExtra(IntentConsts.STORY_OBJECT);
		// TODO: verify can't press next without picture

		setupCaptureButton();
		setupNextButton();
	}

	private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonCapturePictureNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RecordAudioActivity.class);
    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
    			startActivity(intent);
            }
        });
	}

	private void setupCaptureButton() {
    	final Button button = (Button) findViewById(R.id.buttonCapturePicture);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// create Intent to take a picture and return control to the calling application
        		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        		fileUri = getOutputMediaFileUri(); // create a file to save the image
        		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        		// start the image capture Intent
        		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri() {
		return Uri.fromFile(outputFileService.getOutputMediaFile(FileType.PICTURE));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
            	byte[] bytes = bitmapService.readAndResize(fileUri.getPath());
				story.setImage(bytes);
				// TODO:
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
				// TODO:
			} else {
				// Image capture failed, advise user
				// TODO:
			}
		}
	}

}
