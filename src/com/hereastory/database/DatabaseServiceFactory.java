package com.hereastory.database;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.database.impl.MockDatabaseService;


public class DatabaseServiceFactory {
	
	private static DatabaseService databaseService;
	
	public static DatabaseService getDatabaseService() {
		if (databaseService == null) {
			databaseService = new MockDatabaseService();
		}
		return databaseService;
	}
}