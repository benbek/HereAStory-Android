package com.hereastory.shared;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = 1836635286032448494L;
	private String name;
	private String pictureFilePath;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPictureFilePath() {
		return pictureFilePath;
	}
	public void setPictureFilePath(String pictureFilePath) {
		this.pictureFilePath = pictureFilePath;
	}

}
