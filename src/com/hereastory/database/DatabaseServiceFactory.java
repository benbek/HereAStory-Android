package com.hereastory.database;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.database.impl.ParseDatabaseServiceImpl;
import com.hereastory.service.api.OutputFileServiceFactory;


public class DatabaseServiceFactory {
	
	private static DatabaseService databaseService;
	
	public static DatabaseService getDatabaseService() {
		if (databaseService == null) {
			databaseService = new ParseDatabaseServiceImpl(OutputFileServiceFactory.getOutputFileService());
		}
		return databaseService;
	}
}
