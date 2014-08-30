package com.hereastory.service.impl;

import java.nio.ByteBuffer;

import android.graphics.Bitmap;

import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class PointOfInterestServiceImpl implements PointOfInterestService {

	private static final int IMAGE_WIDTH = 852;
	private static final int IMAGE_HEIGHT = 1136;
	private static final int THUMB_IMAGE_WIDTH = 192;
	private static final int THUMB_IMAGE_HEIGHT = 256;
	
	private DatabaseService databaseService;
	private BitmapService bitmapService;
	
	public PointOfInterestServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
		bitmapService = new BitmapServiceImpl();
	}
	
	@Override
	public void readAllInArea(PointLocation location, double maxDistance, PointOfInterestReadHandler handler) {
		databaseService.readAllInArea(location, maxDistance, handler);
	}

	@Override
	public void readLimited(String id, PointOfInterestReadHandler handler) {
		databaseService.readLimited(id, handler);
	}

	@Override
	public void read(String id, PointOfInterestReadHandler handler) {
		databaseService.read(id, handler);
	}

	@Override
	public void add(PointOfInterest pointOfInterest, PointOfInterestAddHandler handler) {
		Bitmap image = bitmapService.readThumbnail(pointOfInterest.getImageFilePath(), IMAGE_WIDTH, IMAGE_HEIGHT);
		Bitmap thumbnail = bitmapService.resizeThumbnail(image, THUMB_IMAGE_WIDTH, THUMB_IMAGE_HEIGHT);
		databaseService.add(pointOfInterest, toByteArray(image), toByteArray(thumbnail), handler);
	}
	
	private byte[] toByteArray(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(buffer);
        return buffer.array();
	}

}
