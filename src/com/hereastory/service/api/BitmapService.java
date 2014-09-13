package com.hereastory.service.api;


public interface BitmapService {

	byte[] readAndResize(String filePath);

	byte[] getThumbnail(String origFilePath);

}
