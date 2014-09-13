package com.hereastory.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.media.MediaRecorder;

public class AudioRecorder {
    private static final int SAMPLING_RATE = 22050;
	private static final int BIT_RATE = 64*1024;
	private static final int NUM_CHANNELS = 1;
    private static final long MAX_DURATION = TimeUnit.MINUTES.toMillis(5);
    
    private static MediaRecorder recorder;
    
    public void startRecording(String filePath) throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);        
        recorder.setAudioChannels(NUM_CHANNELS);
        recorder.setAudioEncodingBitRate(BIT_RATE);
        recorder.setAudioSamplingRate(SAMPLING_RATE);
        recorder.setMaxDuration((int)Math.min(MAX_DURATION, Integer.MAX_VALUE)); // TODO use onInfoListner
        recorder.setOutputFile(filePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.prepare();
        recorder.start();
    }

	public void stopRecording() {
		if (recorder != null) {
	        recorder.stop();
	        recorder.release();
	        recorder = null;		
		}
	}
}
