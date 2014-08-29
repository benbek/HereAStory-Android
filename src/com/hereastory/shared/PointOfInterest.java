package com.hereastory.shared;


public class PointOfInterest extends LimitedPointOfInterest {

	private static final long serialVersionUID = -6765657242190983678L;
	
	private String title;
	private PointLocation location;
	private String pictureFilePath;
	private String audioFilePath;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPictureFilePath() {
		return pictureFilePath;
	}

	public void setPictureFilePath(String pictureFilePath) {
		this.pictureFilePath = pictureFilePath;
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
