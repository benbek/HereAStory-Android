package com.hereastory.service.impl;

import java.io.IOException;

import android.media.MediaPlayer;

public class AudioPlayer {
	
    private MediaPlayer player;
    
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

}
