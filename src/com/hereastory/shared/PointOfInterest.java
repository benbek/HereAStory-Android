package com.hereastory.shared;



public class PointOfInterest extends LimitedPointOfInterest {

	private static final long serialVersionUID = -6765657242190983678L;
	
	private String title;
	private PointLocation location;
	private byte[] image;
	private byte[] audio;

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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getAudio() {
		return audio;
	}

	public void setAudio(byte[] audio) {
		this.audio = audio;
	}

}
