package com.hereastory.database;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.database.impl.ParseDatabaseServiceImpl;
import com.hereastory.service.api.OutputFileService;


public class DatabaseServiceFactory {
	
	private static DatabaseService databaseService;
	
	public static DatabaseService getDatabaseService(OutputFileService outputFileService) {
		if (databaseService == null) {
			databaseService = new ParseDatabaseServiceImpl(outputFileService);
		}
		return databaseService;
	}
}
