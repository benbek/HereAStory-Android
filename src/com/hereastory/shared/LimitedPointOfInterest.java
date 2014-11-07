package com.hereastory.shared;

import java.io.Serializable;
import java.util.Date;

public class LimitedPointOfInterest  implements Serializable {

	private static final long serialVersionUID = -4574620519127749204L;
	
	private String id;
	private Date creationDate;
	private Number likeCount;
	private Number duration;
	private String title;

	private String authorName;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Number getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Number likeCount) {
		this.likeCount = likeCount;
	}
	
	public Number getDuration() {
		return duration;
	}
	
	public void setDuration(Number duration) {
		this.duration = duration;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

}
