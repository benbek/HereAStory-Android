package com.hereastory.shared;

import java.io.Serializable;

import android.graphics.Bitmap;

public class User implements Serializable {

	private static final long serialVersionUID = 1836635286032448494L;
	
	private String id;
	private String name;
	private Bitmap profilePictureSmall; // TODO change to byte[]?
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public Bitmap getProfilePictureSmall() {
		return profilePictureSmall;
	}

	public void setProfilePictureSmall(Bitmap profilePictureSmall) {
		this.profilePictureSmall = profilePictureSmall;
	}

}
