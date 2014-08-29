package com.hereastory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {

	private PointOfInterest story;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		
		Intent intent = getIntent();
		story = (PointOfInterest) intent.getSerializableExtra(IntentConsts.STORY_OBJECT);

		TextView description = (TextView) findViewById(R.id.textHearStoryDescription);
		description.setText(story.getTitle());
		
		ImageView image = (ImageView) findViewById(R.id.imageHearStoryImage);
		image.setImageBitmap(BitmapFactory.decodeFile(story.getPictureFilePath()));
		new AudioPlayer().startPlaying(story.getAudioFilePath());
	}
}
