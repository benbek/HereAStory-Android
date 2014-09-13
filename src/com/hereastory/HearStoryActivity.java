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
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {

    private AudioPlayer audioPlayer;
	private PointOfInterest story; // TODO why is this a member?

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		
		audioPlayer = new AudioPlayer();
		
		story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);
		
		TextView title = (TextView) findViewById(R.id.textHearStoryTitle);
		title.setText(story.getTitle());
		
		ImageView image = (ImageView) findViewById(R.id.imageHearStoryImage); 
		
		try {
			byte[] bytes = IOUtils.toByteArray(new FileInputStream(story.getImage()));
			image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
			audioPlayer.startPlaying(story.getAudio());
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
