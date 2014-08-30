package com.hereastory;

import java.io.IOException;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {

    private AudioPlayer audioPlayer;
	private PointOfInterest story;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		
		story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);
		audioPlayer = new AudioPlayer();
		
		TextView title = (TextView) findViewById(R.id.textHearStoryTitle);
		title.setText(story.getTitle());
		
		ImageView image = (ImageView) findViewById(R.id.imageHearStoryImage); // TODO
		image.setImageBitmap(BitmapFactory.decodeFile(story.getImageFilePath()));
		
		try {
			audioPlayer.startPlaying(story.getAudioFilePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    @Override
    public void onPause() {
        super.onPause();
        audioPlayer.stopPlaying();
    }
}
