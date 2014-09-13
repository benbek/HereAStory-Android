package com.hereastory;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.AudioRecorder;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class RecordAudioActivity extends Activity {
	private static final String LOG_TAG = "RecordAudioActivity";
	
	private static String filePath; // TODO why static?
	private static PointOfInterest story; // TODO why static?

    private AudioRecorder audioRecorder;
    private boolean startRecording = true;
    private AudioPlayer audioPlayer;
    private boolean startPlaying = true;
    
    private OutputFileService outputFileService;
	private PointOfInterestService pointOfInterestService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);

		outputFileService = new OutputFileServiceImpl();
		pointOfInterestService = new PointOfInterestServiceImpl();
		audioPlayer = new AudioPlayer();
		audioRecorder = new AudioRecorder();
		
		filePath = outputFileService.getOutputMediaFile(FileType.AUDIO).getAbsolutePath();
		story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);

		setupRecordButton();
		setupPlayButton();
		setupNextButton();
		// TODO: verify can't press next without recording
		// TODO: verify can't play audio before recording
	}

	private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonRecordAudioNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	story.setAudio(filePath);
            	pointOfInterestService.add(story, new PointOfInterestAddHandler() {
					
					@Override
					public void addFailed(PointOfInterest pointOfInterest, Exception e) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void addCompleted(PointOfInterest pointOfInterest) {
						Intent intent = new Intent(getApplicationContext(), HearStoryActivity.class);
		    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
		    			startActivity(intent);
					}
				});
            }
        });
	}

	private void setupRecordButton() {
		final Button button = (Button) findViewById(R.id.buttonRecordAudio);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startRecording) {
                	button.setText(getResources().getString(R.string.stop_recording_audio));
                	startRecording();
                } else {
                	button.setText(getResources().getString(R.string.start_recording_audio));
                	stopRecording();
                }
                startRecording = !startRecording;
            }
        });
	}
	
	private void setupPlayButton() {
		final Button button = (Button) findViewById(R.id.buttonPlayAudio);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startPlaying) {
                	button.setText(getResources().getString(R.string.stop_playing_audio));
                	startPlaying();
                } else {
                	button.setText(getResources().getString(R.string.start_playing_audio));
                	stopPlaying();
                }
                startPlaying = !startPlaying;
            }
        });
	}

    @Override
    public void onPause() {
        super.onPause();
        audioRecorder.stopRecording();
        audioPlayer.stopPlaying();
    }
    
    private void startPlaying() {
    	try {
			audioPlayer.startPlaying(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void stopPlaying() {
        audioPlayer.stopPlaying();
    }

    private void startRecording() {
        try {
			audioRecorder.startRecording(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void stopRecording() {
        audioRecorder.stopRecording();
    }

}
