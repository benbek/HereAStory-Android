package com.hereastory.service.api;

import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;


public interface PointOfInterestService {

	void readAllInArea(PointLocation location, double maxDistance, PointOfInterestReadHandler handler);

	void readLimited(String id, PointOfInterestReadHandler handler);

	void read(String id, PointOfInterestReadHandler handler);

	void add(PointOfInterest pointOfInterest, PointOfInterestAddHandler handler);
	
}
