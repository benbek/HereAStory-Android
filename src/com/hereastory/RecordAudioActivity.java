package com.hereastory;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.service.impl.AudioPlayer;
import com.hereastory.service.impl.OutputFileServiceImpl;
import com.hereastory.service.impl.PointOfInterestServiceImpl;
import com.hereastory.shared.IntentConsts;
import com.hereastory.shared.PointOfInterest;

public class RecordAudioActivity extends Activity {

    private static final String LOG_TAG = "RecordAudioActivity";

    private MediaRecorder audioRecorder;
    private AudioPlayer audioPlayer;
    private OutputFileService outputFileService;
    private PointOfInterestService pointOfInterestService;
    private boolean startPlaying = true;
    private boolean startRecording = true;
    private static String filePath;
	private PointOfInterest story;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);
		pointOfInterestService = new PointOfInterestServiceImpl();
		outputFileService = new OutputFileServiceImpl();
		filePath = outputFileService.getOutputMediaFile(FileType.AUDIO).getAbsolutePath();
		Intent intent = getIntent();
		story = (PointOfInterest) intent.getSerializableExtra(IntentConsts.STORY_OBJECT);
		// TODO: verify can't press next without recording
		// TODO: verify can't play audio before recording
		audioPlayer = new AudioPlayer();
		setupRecordButton();
		setupPlayButton();
		setupNextButton();
	}

	private void setupNextButton() {
    	final Button button = (Button) findViewById(R.id.buttonRecordAudioNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	story.setAudioFilePath(filePath);
            	//TODO pointOfInterestService.add(story);
				Intent intent = new Intent(getApplicationContext(), HearStoryActivity.class);
    			intent.putExtra(IntentConsts.STORY_OBJECT, story);
    			startActivity(intent);
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
        if (audioRecorder != null) {
            audioRecorder.release();
            audioRecorder = null;
        }

        audioPlayer.stopPlaying();
    }
    
    private void startPlaying() {
    	audioPlayer.startPlaying(filePath);
    }

    private void stopPlaying() {
        audioPlayer.stopPlaying();
    }

    private void startRecording() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setOutputFile(filePath);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            audioRecorder.prepare();
            audioRecorder.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed"); 
            Log.e(LOG_TAG, e.getMessage());
            for (StackTraceElement s : e.getStackTrace()) {
            	Log.e(LOG_TAG, s.toString());
            }
        }

       
    }

    private void stopRecording() {
        audioRecorder.stop();
        audioRecorder.release();
        audioRecorder = null;
    }

}
