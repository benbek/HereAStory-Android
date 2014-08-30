package com.hereastory.service.api;

import android.graphics.Bitmap;

public interface BitmapService {
	
	public Bitmap readThumbnail(String filePath, int reqWidth, int reqHeight);
	
	public Bitmap resizeThumbnail(Bitmap bitmap, int reqWidth, int reqHeight);
}
