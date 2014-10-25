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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		result = prime
				* result
				+ ((pointOfInterestId == null) ? 0 : pointOfInterestId
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PointLocation)) {
			return false;
		}
		PointLocation other = (PointLocation) obj;
		if (latitude == null) {
			if (other.latitude != null) {
				return false;
			}
		} else if (!latitude.equals(other.latitude)) {
			return false;
		}
		if (longitude == null) {
			if (other.longitude != null) {
				return false;
			}
		} else if (!longitude.equals(other.longitude)) {
			return false;
		}
		if (pointOfInterestId == null) {
			if (other.pointOfInterestId != null) {
				return false;
			}
		} else if (!pointOfInterestId.equals(other.pointOfInterestId)) {
			return false;
		}
		return true;
	}

}
