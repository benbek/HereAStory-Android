package com.hereastory.service.api;

import android.content.Context;

import com.hereastory.service.impl.OutputFileServiceImpl;

public class OutputFileServiceFactory {

	private static OutputFileService outputFileServiceService;

	public static OutputFileService init(Context context) {
		outputFileServiceService = new OutputFileServiceImpl(context);
		return outputFileServiceService;
	}
	
	public static OutputFileService getOutputFileService() {
		// assumes that init was already called
		if (outputFileServiceService == null) {
			throw new IllegalStateException("Must call init method first");
		}
		return outputFileServiceService;
	}
}
