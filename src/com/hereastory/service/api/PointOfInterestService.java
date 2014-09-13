package com.hereastory.service.api;

import com.hereastory.shared.PointOfInterest;


public interface PointOfInterestService {

	/**
	 * Returns all the points near the input location
	 * @param latitude 
	 * @param longitude
	 * @param maxDistance (in kilometres)
	 * @param handler
	 */
	void readAllInArea(double latitude, double longitude, double maxDistance, PointOfInterestReadHandler handler);

	void readLimited(String id, PointOfInterestReadHandler handler);

	void read(String id, PointOfInterestReadHandler handler);

	void add(PointOfInterest pointOfInterest, PointOfInterestAddHandler handler);

	
}
