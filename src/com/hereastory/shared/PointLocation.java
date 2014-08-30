package com.hereastory.shared;

import java.io.Serializable;

public class PointLocation implements Serializable {
	
	private static final long serialVersionUID = -5114185597366461526L;
	
	private Double latitude;
	private Double longitude;
	private String pointOfInterestId;

	public PointLocation(Double latitude, Double longitude, String pointOfInterestId) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.pointOfInterestId = pointOfInterestId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public String getPointOfInterestId() {
		return pointOfInterestId;
	}

}
