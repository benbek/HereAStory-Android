package com.hereastory;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class HearStoryActivity extends Activity {

	private OutputFileService outputFileService;
    private AudioPlayer audioPlayer;
	private PointOfInterest story; // TODO why is this a member?

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hear_story);
		
		audioPlayer = new AudioPlayer();
		outputFileService = new OutputFileServiceImpl();
		
		story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);
		
		TextView title = (TextView) findViewById(R.id.textHearStoryTitle);
		title.setText(story.getTitle());
		
		ImageView image = (ImageView) findViewById(R.id.imageHearStoryImage); 
		image.setImageBitmap(BitmapFactory.decodeByteArray(story.getImage(), 0,  story.getImage().length));
		
		try {
			startPlaying();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startPlaying() throws IOException {
		File audioFile = outputFileService.getOutputMediaFile(FileType.AUDIO, story.getId());
		if (!audioFile.exists()) {
			FileUtils.writeByteArrayToFile(audioFile, story.getAudio());
		}
		audioPlayer.startPlaying(audioFile.getAbsolutePath());
	}

	@Override
    public void onPause() {
        super.onPause();
        audioPlayer.stopPlaying();
    }
}
