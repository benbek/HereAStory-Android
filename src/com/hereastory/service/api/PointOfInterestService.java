package com.hereastory.service.api;

import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;


public interface PointOfInterestService {

	void readAllInArea(PointLocation location, double maxDistance, PointOfInterestResponseHandler handler);

	void readLimited(String id, PointOfInterestResponseHandler handler);

	void read(String id, PointOfInterestResponseHandler handler);

	void add(PointOfInterest pointOfInterest, PointOfInterestResponseHandler handler);
	

}
