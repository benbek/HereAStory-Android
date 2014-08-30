package com.hereastory.database.api;

import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public interface DatabaseService {
	
	public void read(String id, PointOfInterestReadHandler handler);

	public void readLimited(String id, PointOfInterestReadHandler handler);

	public void readAllInArea(PointLocation location, double maxDistance, PointOfInterestReadHandler handler);

	public void add(PointOfInterest pointOfInterest, byte[] thumbnail, PointOfInterestAddHandler handler);

}
