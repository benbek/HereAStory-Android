package com.hereastory.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioPlayer {
	
    private static MediaPlayer player;
    
    public void startPlaying(String filePath) throws IOException {
        prepare(filePath);
        player.start();
    }

	private void prepare(String filePath) throws IOException {
		player = new MediaPlayer();
        player.setDataSource(filePath);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.prepare();
	}
    
    public void stopPlaying() {
    	if (player != null) {
    		player.release();
    		player = null;
    	}
    }

	public Number getDuration(String filePath) throws IOException {
		try {
	        prepare(filePath);
			long seconds = TimeUnit.MILLISECONDS.toSeconds(player.getDuration());
			player.release();
			return seconds;
		} catch (Exception e) {
			return 0;
		}
	}

}
