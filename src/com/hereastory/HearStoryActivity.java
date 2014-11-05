package com.hereastory;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.ErrorDialogService;
import com.hereastory.shared.HereAStoryAnalytics;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {
	
	private static final String LOG_TAG = "HearStoryActivity";
	
	private static HereAStoryAnalytics analytics;
    private AudioPlayer audioPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		analytics = HereAStoryAnalytics.getInstanceForContext(this);
		analytics.track(LOG_TAG);
		
		audioPlayer = new AudioPlayer();
		PointOfInterest story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);
		
		showTitle(story);
		showCreationDate(story);
		showDuration(story);
		showLikes(story);
		showAuthorName(story);
		showImage(story);
		showAuthorPicture(story);
		playAudio(story);
		
	}
	private void showAuthorPicture(PointOfInterest story) {
		ImageView image = (ImageView) findViewById(R.id.profile_picture); 
		
		try {
			byte[] bytes = IOUtils.toByteArray(new FileInputStream(story.getAuthor().getProfilePictureSmall()));
			image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_showing_image, e, getApplicationContext());
		}
	}
	
	private void showAuthorName(PointOfInterest story) {
		TextView title = (TextView) findViewById(R.id.authorName);
		title.setText(story.getAuthorName());
	}
	
	private void showLikes(PointOfInterest story) {
		Button likes = (Button) findViewById(R.id.loved_btn);
		likes.setText(story.getLikeCount().toString());
	}
	
	private void showDuration(PointOfInterest story) {
		TextView duration = (TextView) findViewById(R.id.story_duration);
		duration.setText(story.getDuration().toString());
	}
	
	private void showCreationDate(PointOfInterest story) {
		TextView date = (TextView) findViewById(R.id.story_creation_date);
		date.setText(new SimpleDateFormat("dd/MM/yy").format(story.getCreationDate())); 
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
	
	@Override
	protected void onDestroy() {
		analytics.flush();
	    super.onDestroy();
	}
	
	@Override
    public void onBackPressed() {
        super.onBackPressed();
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
    }

}
