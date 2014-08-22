package com.hereastory.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Environment;
import android.util.Log;

import com.hereastory.service.api.OutputFileService;

public class OutputFileServiceImpl implements OutputFileService {
	
	@Override
	public File getOutputMediaFile(FileType fileType) {
		// TODO: To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = getDirectory();
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("OutputFileGenerator", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + "." + fileType.getSuffix());
		return mediaFile;
	}

	private File getDirectory() {
		File baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File mediaStorageDir = new File(baseDir, "HereAStory");
		return mediaStorageDir;
	}
	
	@Override
	public void clearDirectory() {
		File mediaStorageDir = getDirectory();
		if (mediaStorageDir.exists()) {
			File[] files = mediaStorageDir.listFiles();
			for (File file : files) {
				file.delete();
			}
		}
	}

}
