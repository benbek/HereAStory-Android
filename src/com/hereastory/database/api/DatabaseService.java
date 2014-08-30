package com.hereastory.database.api;

import com.hereastory.service.api.PointOfInterestResponseHandler;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public interface DatabaseService {
	
	public void add(PointOfInterest pointOfInterest, PointOfInterestResponseHandler handler);

	public void read(String id, PointOfInterestResponseHandler handler);

	public void readLimited(String id, PointOfInterestResponseHandler handler);

	public void readAllInArea(PointLocation location, double maxDistance, PointOfInterestResponseHandler handler);

}
