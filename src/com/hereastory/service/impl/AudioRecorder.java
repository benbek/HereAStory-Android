package com.hereastory.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
    private static final int SAMPLING_RATE = 22050;
	private static final int BIT_RATE = 64*1024;
	private static final int NUM_CHANNELS = 1;
    
    private static MediaRecorder recorder;
    private static FileOutputStream fileOut;
    
    public void startRecording(String filePath) throws IOException {
    	MediaRecorder recorder1 = new MediaRecorder();
        recorder1.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder1.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);        
        recorder1.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder1.setAudioChannels(NUM_CHANNELS);
        recorder1.setAudioEncodingBitRate(BIT_RATE);
        recorder1.setAudioSamplingRate(SAMPLING_RATE);
        
        fileOut = new FileOutputStream(filePath);
        recorder1.setOutputFile(fileOut.getFD());
        recorder1.prepare();
        recorder1.start();
        recorder = recorder1;
    }

	public void stopRecording() throws IOException {
		if (recorder != null) {
			try {
		        recorder.stop();
		        recorder.reset();
		        recorder.release();
			} catch (Exception e) {
				Log.e("AudioRecorder", "failed stopping recorder", e);
			} try {
		        fileOut.flush();
		        fileOut.close();
			} catch (Exception e) {
				Log.e("AudioRecorder", "failed flushing out file", e);
			} 
	        recorder = null;	
	        fileOut = null;
		}
	}
}
