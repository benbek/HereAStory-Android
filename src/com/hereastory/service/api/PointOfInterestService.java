package com.hereastory.service.api;

import java.util.List;

import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.parse.ParseException;

public interface PointOfInterestService {
	
	List<PointLocation> readAllInArea(PointLocation location, double maxDistance) throws ParseException;
	
	LimitedPointOfInterest readLimited(String id);
	
	PointOfInterest read(String id);
	
	void add(PointOfInterest pointOfInterest);
	
}
