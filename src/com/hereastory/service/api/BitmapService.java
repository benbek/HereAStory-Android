package com.hereastory.service.api;


public interface BitmapService {

	byte[] getThumbnail(byte[] orig);

	byte[] readAndResize(String filePath);
}
