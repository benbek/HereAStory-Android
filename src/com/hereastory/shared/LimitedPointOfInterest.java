package com.hereastory.shared;

import java.io.Serializable;
import java.util.Date;

public class LimitedPointOfInterest  implements Serializable {

	private static final long serialVersionUID = -4574620519127749204L;
	private Long id;
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
