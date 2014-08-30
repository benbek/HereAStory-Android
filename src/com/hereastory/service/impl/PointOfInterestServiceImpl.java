package com.hereastory.service.impl;

import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.PointOfInterestResponseHandler;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class PointOfInterestServiceImpl implements PointOfInterestService {
	
	private DatabaseService databaseService;
	
	public PointOfInterestServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
	}
	
	@Override
	public void readAllInArea(PointLocation location, double maxDistance, PointOfInterestResponseHandler handler) {
		databaseService.readAllInArea(location, maxDistance, handler);
	}

	@Override
	public void readLimited(String id, PointOfInterestResponseHandler handler) {
		databaseService.readLimited(id, handler);
	}

	@Override
	public void read(String id, PointOfInterestResponseHandler handler) {
		databaseService.read(id, handler);
	}

	@Override
	public void add(PointOfInterest pointOfInterest, PointOfInterestResponseHandler handler) {
		databaseService.add(pointOfInterest, handler);
	}

}
