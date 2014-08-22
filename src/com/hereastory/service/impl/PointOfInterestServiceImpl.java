package com.hereastory.service.impl;

import java.util.List;

import com.hereastory.database.DatabaseServiceFactory;
import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.PointOfInterestService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public class PointOfInterestServiceImpl implements PointOfInterestService {
	
	private DatabaseService databaseService;
	
	public PointOfInterestServiceImpl() {
		databaseService = DatabaseServiceFactory.getDatabaseService();
	}
	
	@Override
	public List<PointLocation> readAllInArea(PointLocation location) {
		// TODO group points together
		return databaseService.readAllInArea();
	}

	@Override
	public LimitedPointOfInterest readLimited(Long id) {
		return databaseService.readLimited(id);
	}

	@Override
	public PointOfInterest read(Long id) {
		return databaseService.read(id);
	}

	@Override
	public void add(PointOfInterest pointOfInterest) {
		databaseService.add(pointOfInterest);
	}

}
