package com.hereastory;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.api.OutputFileServiceFactory;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.AudioRecorder;
import com.hereastory.service.impl.ErrorDialogService;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class RecordAudioActivity extends Activity {
	private static final String LOG_TAG = "RecordAudioActivity";
	
	private static String filePath;
	private static PointOfInterest story;

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
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		outputFileService = OutputFileServiceFactory.init(this);
		pointOfInterestService = new PointOfInterestServiceImpl();
		audioPlayer = new AudioPlayer();
		audioRecorder = new AudioRecorder();
		
		filePath = outputFileService.getOutputMediaFile(FileType.AUDIO).getAbsolutePath();
		story = (PointOfInterest) getIntent().getSerializableExtra(IntentConsts.STORY_OBJECT);

		setupRecordButton();
		setupPlayButton();
		setupNextButton();
	}

	private void setupNextButton() {
    	final Button button = getNextButton();
    	button.setEnabled(false);
        button.setOnClickListener(new View.OnClickListener() {
        	
            public void onClick(View v) {
            	pointOfInterestService.add(story, new PointOfInterestAddHandler() {
					
					@Override
					public void addFailed(PointOfInterest pointOfInterest, Exception e) {
						ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_stop_recording, e, RecordAudioActivity.this);
					}
					
					@Override
					public void addCompleted(PointOfInterest pointOfInterest) {}
				});
            	
				Intent intent = new Intent(getApplicationContext(), HearStoryActivity.class);
    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
    			startActivity(intent);
            }
        });
	}

	private Button getNextButton() {
		return (Button) findViewById(R.id.buttonRecordAudioNext);
	}

	private void setupRecordButton() {
		final Button button = (Button) findViewById(R.id.buttonRecordAudio);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startRecording) {
                	getNextButton().setEnabled(false);
                	getPlayButton().setEnabled(false);
                	startRecording();
                	button.setText(getResources().getString(R.string.stop_recording_audio));
                } else {
                	button.setText(getResources().getString(R.string.start_recording_audio));
                	stopRecording();
                	getPlayButton().setEnabled(true);
                	getNextButton().setEnabled(true);
                }
                startRecording = !startRecording;
            }
        });
	}
	
	private void setupPlayButton() {
		final Button button = getPlayButton();
		button.setEnabled(false);
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

	private Button getPlayButton() {
		return (Button) findViewById(R.id.buttonPlayAudio);
	}

    @Override
    public void onPause() {
        super.onPause();
		stopRecording();
        stopPlaying();
    }
    
    private void startPlaying() {
    	try {
			audioPlayer.startPlaying(filePath);
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_start_playing, e, this);
		}
    }

    private void stopPlaying() {
        audioPlayer.stopPlaying();
    }

    private void startRecording() {
        try {
			audioRecorder.startRecording(filePath);
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_start_recording, e, this);
		}
    }

    private void stopRecording() {
        try {
			audioRecorder.stopRecording();
			story.setDuration(audioPlayer.getDuration(filePath));
			story.setAudio(filePath);
		} catch (IOException e) {
			ErrorDialogService.showGeneralError(LOG_TAG, R.string.failed_stop_recording, e, this);
		}
    }
    
	@Override
    public void onBackPressed() {
        super.onBackPressed();
		Intent intent = new Intent(this, MapActivity_bo.class);
		startActivity(intent);
    }

}
