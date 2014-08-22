package com.hereastory.service.impl;

import java.io.IOException;

import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;

    
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    
    public void startPlaying(String filePath) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            Log.e("AudioPlayer", "prepare() failed"); 
        }
    }
    
    public void stopPlaying() {
    	if (mediaPlayer != null) {
    		mediaPlayer.release();
    		mediaPlayer = null;
    	}
    }

}
