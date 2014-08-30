package com.hereastory.service.api;

import java.io.File;


public interface OutputFileService {

	File getOutputMediaFile(FileType fileType);
	
	File getOutputMediaFile(FileType fileType, String pointOfInterestId);

	void clearDirectory();
	
	public enum FileType {
		
		PICTURE("jpg"), AUDIO("aac");
		
		private String suffix;
		
		FileType(String suffix) {
			this.suffix = suffix;
		}
		
		public String getSuffix() {
			return suffix;
		}
	}


}
