package com.hereastory.service.impl;

import java.util.List;

import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.parse.ParseException;

public class PointOfInterestServiceImpl implements PointOfInterestService {
	
	private DatabaseService databaseService;
	
	public PointOfInterestServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
	}
	
	@Override
	public List<PointLocation> readAllInArea(PointLocation location, double maxDistance) throws ParseException {
		return databaseService.readAllInArea(location, maxDistance);
	}

	@Override
	public LimitedPointOfInterest readLimited(String id) {
		return databaseService.readLimited(id);
	}

	@Override
	public PointOfInterest read(String id) {
		return databaseService.read(id);
	}

	@Override
	public void add(PointOfInterest pointOfInterest) {
		databaseService.add(pointOfInterest);
	}

}
