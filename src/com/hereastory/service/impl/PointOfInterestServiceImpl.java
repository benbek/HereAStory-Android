package com.hereastory.service.impl;

import java.util.Date;

import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.shared.PointOfInterest;

public class PointOfInterestServiceImpl implements PointOfInterestService {

	
	private DatabaseService databaseService;
	private BitmapService bitmapService;
	
	public PointOfInterestServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
		bitmapService = new BitmapServiceImpl();
	}
	
	@Override
	public void readAllInArea(double latitude, double longitude, double maxDistance, PointOfInterestReadHandler handler) {
		databaseService.readAllInArea(latitude, longitude, maxDistance, handler);
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
		byte[] thumbnail = bitmapService.getThumbnail(pointOfInterest.getImage());
		pointOfInterest.setCreationDate(new Date());
		pointOfInterest.setLikeCount(0);
		databaseService.add(pointOfInterest, thumbnail , handler);
	}

}
