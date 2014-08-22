package com.hereastory.database.impl;

import java.util.List;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;


public class MockDatabaseService implements DatabaseService {

	@Override
	public void add(PointOfInterest story) {
		story.setId(1l);
	}

	@Override
	public PointOfInterest read(Long id) {
		PointOfInterest story = new PointOfInterest();
		story.setId(1l);
		story.setDescription("description");
		return story ;
	}

	@Override
	public LimitedPointOfInterest readLimited(Long id) {
		// TODO Auto-generated method stub
		// test
		return null;
	}

	@Override
	public List<PointLocation> readAllInArea() {
		// TODO Auto-generated method stub
		return null;
	}

}
