package com.hereastory.shared;

public class LocationUnavailableException extends Exception {

	private static final long serialVersionUID = 4109855129431175285L;

	public LocationUnavailableException() {
		super();
	}
	
	public LocationUnavailableException(String message) {
		super(message);
	}
	
}
