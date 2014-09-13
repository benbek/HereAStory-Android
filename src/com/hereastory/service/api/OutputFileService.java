package com.hereastory.service.api;

import java.io.File;


public interface OutputFileService {

	File getOutputMediaFile(FileType fileType);
	
	File getOutputMediaFile(FileType fileType, String pointOfInterestId);

	File getProfilePictureFile(String userId);

	void clearDirectory();
	
	public enum FileType {
		
		IMAGE("jpg"), AUDIO("aac");
		
		private String suffix;
		
		FileType(String suffix) {
			this.suffix = suffix;
		}
		
		public String getSuffix() {
			return suffix;
		}
	}

}
