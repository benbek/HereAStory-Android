package com.hereastory.service.api;

import android.content.Context;

import com.hereastory.service.impl.OutputFileServiceImpl;

public class OutputFileServiceFactory {

	private static OutputFileService outputFileServiceService;

	public static void init(Context context) {
		outputFileServiceService = new OutputFileServiceImpl(context);
	}
	
	public static OutputFileService getOutputFileService() {
		if (outputFileServiceService == null) {
			throw new IllegalStateException("Must call init method first");
		}
		return outputFileServiceService;
	}
}
