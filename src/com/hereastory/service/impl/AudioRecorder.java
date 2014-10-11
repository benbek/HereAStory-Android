package com.hereastory.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorder {
    private static final int SAMPLING_RATE = 22050;
	private static final int BIT_RATE = 64*1024;
	private static final int NUM_CHANNELS = 1;
    //TODO private static final long MAX_DURATION = TimeUnit.MINUTES.toMillis(5);
    
    private static MediaRecorder recorder;
    private static FileOutputStream fileOut;
    
    public void startRecording(String filePath) throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);        
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioChannels(NUM_CHANNELS);
        recorder.setAudioEncodingBitRate(BIT_RATE);
        recorder.setAudioSamplingRate(SAMPLING_RATE);
        
        fileOut = new FileOutputStream(filePath);
        recorder.setOutputFile(fileOut.getFD());
        recorder.prepare();
        recorder.start();
    }

	public void stopRecording() throws IOException {
		if (recorder != null) {
	        recorder.stop();
	        recorder.reset();
	        recorder.release();
	        fileOut.flush();
	        fileOut.close();
	        recorder = null;	
	        fileOut = null;
		}
	}
}
