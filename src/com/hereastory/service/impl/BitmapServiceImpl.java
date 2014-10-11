package com.hereastory.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import com.hereastory.service.api.BitmapService;

public class BitmapServiceImpl implements BitmapService {

	private static final int IMAGE_WIDTH = 852;
	private static final int IMAGE_HEIGHT = 1136;
	private static final int THUMB_IMAGE_WIDTH = 192;
	private static final int THUMB_IMAGE_HEIGHT = 256;
	
	@Override
	public void compress(String filePath) throws FileNotFoundException, IOException {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, IMAGE_WIDTH, IMAGE_HEIGHT);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 30, outputStream);
		
		outputStream.writeTo(new FileOutputStream(filePath));
	}
	
	@Override
	public byte[] getThumbnail(String origFilePath) {
		Bitmap bitmap = BitmapFactory.decodeFile(origFilePath);
		Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap , THUMB_IMAGE_WIDTH, THUMB_IMAGE_HEIGHT);
		return toByteArray(thumbnail);
	}
	
	private byte[] toByteArray(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
	}
	
	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		
		if (height > reqHeight || width > reqWidth) {
			
			final int halfHeight = height / 2;
			final int halfWidth = width / 2;
			
			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		
		return inSampleSize;
	}
}
