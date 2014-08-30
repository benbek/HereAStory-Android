package com.hereastory.shared;

import java.io.Serializable;

public class PointLocation implements Serializable {
	
	private static final long serialVersionUID = -5114185597366461526L;
	
	private Double latitude;
	private Double longitude;

	public PointLocation(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
}
