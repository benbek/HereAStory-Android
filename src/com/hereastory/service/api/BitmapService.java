package com.hereastory.service.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


public interface BitmapService {

	void compress(String filePath) throws FileNotFoundException, IOException;

	byte[] getThumbnail(String origFilePath);

	byte[] downloadBitmap(String url) throws MalformedURLException, IOException;

}
