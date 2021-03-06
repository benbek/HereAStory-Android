package com.hereastory.service.impl;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.ContextWrapper;

import com.hereastory.service.api.OutputFileService;

public class OutputFileServiceImpl implements OutputFileService {
	
	private ContextWrapper contextWrapper;
	
	public OutputFileServiceImpl(Context context) {
		this.contextWrapper = new ContextWrapper(context);
	}

	@Override
	public File getProfilePictureFile(String userId) {
		File mediaStorageDir = getDirectory();
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "profile_" + userId + "." + FileType.IMAGE.getSuffix());
		return mediaFile;
	}
	
	@Override
	public File getOutputMediaFile(FileType fileType) {
		return getOutputMediaFile(fileType, null);
	}

	@Override
	public File getOutputMediaFile(FileType fileType, String pointOfInterestId) {
		File mediaStorageDir = getDirectory();

		// Create a media file name
		String fileName = pointOfInterestId;
		if (fileName == null) {
			fileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
		}
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName + "." + fileType.getSuffix());
		return mediaFile;
	}

	private File getDirectory() {
		File mediaStorageDir = new File(contextWrapper.getFilesDir(), "media");
		
		if (!mediaStorageDir.exists()) {
			mediaStorageDir.mkdirs();
		}

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
