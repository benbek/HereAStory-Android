package com.hereastory.database.api;

import java.util.List;

import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public interface DatabaseService {
	
	public void add(PointOfInterest story);

	public PointOfInterest read(Long id);

	public LimitedPointOfInterest readLimited(Long id);

	public List<PointLocation> readAllInArea();
}
