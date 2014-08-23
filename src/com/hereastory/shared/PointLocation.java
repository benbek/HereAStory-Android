package com.hereastory.shared;

import java.io.Serializable;

public class PointLocation implements Serializable {
	
	private static final long serialVersionUID = -5114185597366461526L;
	private Double latitude;
	private Double Doubletitude;

	public PointLocation(Double latitude, Double Doubletitude) {
		this.latitude = latitude;
		this.Doubletitude = Doubletitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getDoubletitude() {
		return Doubletitude;
	}

	public void setDoubletitude(Double Doubletitude) {
		this.Doubletitude = Doubletitude;
	}
}
