package com.hereastory.shared;

import java.io.Serializable;

public class PointLocation implements Serializable {
	
	private static final long serialVersionUID = -1986712232859714060L;
	
	private Long latitude;
	private Long longtitude;

	public PointLocation(Long latitude, Long longtitude) {
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
	}

	public Long getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Long longtitude) {
		this.longtitude = longtitude;
	}
}
