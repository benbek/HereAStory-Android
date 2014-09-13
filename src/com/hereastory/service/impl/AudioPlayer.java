package com.hereastory.service.impl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;

public class AudioPlayer {
	
    private static MediaPlayer player;
    
    public void startPlaying(String filePath) throws IOException {
        player = new MediaPlayer();
        player.setDataSource(filePath);
        player.prepare();
        player.start();
    }
    
    public void stopPlaying() {
    	if (player != null) {
    		player.release();
    		player = null;
    	}
    }

	public Number getDuration(String filePath) {
		MediaPlayer player = new MediaPlayer();
        try {
			player.setDataSource(filePath);
		} catch (IOException e) {
			return 0;
		}
		return TimeUnit.MILLISECONDS.toSeconds(player.getDuration());
	}

}
