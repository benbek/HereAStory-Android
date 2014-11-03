package com.hereastory.service.api;

import java.io.FileNotFoundException;
import java.io.IOException;


public interface BitmapService {

	void compress(String filePath) throws FileNotFoundException, IOException;

	byte[] getThumbnail(String origFilePath);

}
