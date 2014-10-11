package com.hereastory;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.ErrorDialogService;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {
	private static final String LOG_TAG = "HearStoryActivity";

    private AudioPlayer audioPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		
		audioPlayer = new AudioPlayer();
		PointOfInterest story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);
		
		showTitle(story);
		showImage(story);
		playAudio(story);
	}

	private void playAudio(PointOfInterest story) {
		try {
			audioPlayer.startPlaying(story.getAudio());
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_start_playing, e, getApplicationContext());
		}
	}

	private void showImage(PointOfInterest story) {
		ImageView image = (ImageView) findViewById(R.id.imageHearStoryImage); 
		
		try {
			byte[] bytes = IOUtils.toByteArray(new FileInputStream(story.getImage()));
			image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_showing_image, e, getApplicationContext());
		}
	}

	private void showTitle(PointOfInterest story) {
		TextView title = (TextView) findViewById(R.id.textHearStoryTitle);
		title.setText(story.getTitle());
	}

	@Override
    public void onPause() {
        super.onPause();
        audioPlayer.stopPlaying();
    }
}
