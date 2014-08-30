package com.hereastory.shared;


public class PointOfInterest extends LimitedPointOfInterest {

	private static final long serialVersionUID = -6765657242190983678L;
	
	private String title;
	private PointLocation location;
	private String imageFilePath;
	private String audioFilePath;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageFilePath() {
		return imageFilePath;
	}

	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}

	public String getAudioFilePath() {
		return audioFilePath;
	}

	public void setAudioFilePath(String audioFilePath) {
		this.audioFilePath = audioFilePath;
	}

	public PointLocation getLocation() {
		return location;
	}

	public void setLocation(PointLocation location) {
		this.location = location;
	}

}
