package com.hereastory.service.api;

import java.io.File;

public interface OutputFileService {

	File getOutputMediaFile(FileType fileType);

	void clearDirectory();
	
	public enum FileType {
		
		PICTURE("jpg"), AUDIO("3gp");
		
		private String suffix;
		
		FileType(String suffix) {
			this.suffix = suffix;
		}
		
		public String getSuffix() {
			return suffix;
		}
	}

}
