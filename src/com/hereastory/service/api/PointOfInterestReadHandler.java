package com.hereastory.service.api;

import java.util.List;

import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.parse.ParseException;

public interface PointOfInterestReadHandler {

	void readCompleted(PointOfInterest pointOfInterest);

	void readFailed(String id, Exception e);

	void readLimitedCompleted(LimitedPointOfInterest pointOfInterest);

	void readLimitedFailed(String id, Exception e);

	void readAllInAreaCompleted(List<PointLocation> points);

	void readAllInAreaFailed(double latitude, double longitude, double maxDistance, ParseException e);
}
