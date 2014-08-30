package com.hereastory.service.api;

import com.hereastory.shared.PointOfInterest;

public interface PointOfInterestAddHandler {

	void addCompleted(PointOfInterest pointOfInterest);

	void addFailed(PointOfInterest pointOfInterest, Exception e);

}
