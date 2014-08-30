package com.hereastory.service.api;

import java.util.List;

import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;

public interface PointOfInterestResponseHandler {

	void readCompleted(PointOfInterest pointOfInterest);

	void readFailed(String id, Exception e);

	void readLimitedCompleted(LimitedPointOfInterest pointOfInterest);

	void readLimitedFailed(String id, Exception e);

	void readAllInAreaCompleted(List<PointLocation> points);

	void readAllInAreaFailed(PointLocation location, double maxDistance, Exception e);

	void addCompleted(PointOfInterest pointOfInterest);

	void addFailed(PointOfInterest pointOfInterest, Exception e);

}
