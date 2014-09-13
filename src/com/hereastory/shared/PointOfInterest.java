package com.hereastory.shared;



public class PointOfInterest extends LimitedPointOfInterest {

	private static final long serialVersionUID = -6765657242190983678L;
	
	private String title;
	private PointLocation location;
	private String image;
	private String audio;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PointLocation getLocation() {
		return location;
	}

	public void setLocation(PointLocation location) {
		this.location = location;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getAudio() {
		return audio;
	}

	public void setAudio(String audio) {
		this.audio = audio;
	}

}
