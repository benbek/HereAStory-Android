package com.hereastory.shared;

import java.io.Serializable;
import java.util.Date;

public class PointOfInterest implements Serializable {

	private static final long serialVersionUID = -6765657242190983678L;
	
	private Long id;
	private String description;
	private PointLocation location;
	private String pictureFilePath;
	private String audioFilePath;
	private User user;
	private Date creationDate;
	private Long likes;
	private Double length;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Long getLikes() {
		return likes;
	}

	public void setLikes(Long likes) {
		this.likes = likes;
	}

	public Double getLength() {
		return length;
	}

	public void setLength(Double length) {
		this.length = length;
	}

}
