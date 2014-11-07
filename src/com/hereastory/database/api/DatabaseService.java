package com.hereastory.database.api;

import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.shared.PointOfInterest;
import com.hereastory.shared.User;

public interface DatabaseService {
	
	public void read(String id, PointOfInterestReadHandler handler);

	public void readLimited(String id, PointOfInterestReadHandler handler);

	public void readAllInArea(double latitude, double longitude, double maxDistance, PointOfInterestReadHandler handler);

	public void add(PointOfInterest pointOfInterest, byte[] thumbnail, PointOfInterestAddHandler handler);

	public void addFacebookUser(String facebookId, String name, byte[] profilePicture);

	public User getCurrentAuthor();

	public void incrementLikeCount(String storyId);

}
