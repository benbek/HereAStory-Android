package com.hereastory.database.impl;

import java.util.List;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;


public class ParseDatabaseServiceImpl implements DatabaseService {

	@Override
	public void add(PointOfInterest story) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PointOfInterest read(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LimitedPointOfInterest readLimited(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PointLocation> readAllInArea() {
		// TODO Auto-generated method stub
		return null;
	}
/*
	public void addTask(Long id, String title, Long dueDate, String androidId) {
		ParseObject task = new ParseObject(DbConsts.PARSE_OBJECT_NAME);
		task.put(DbConsts.PARSE_ID_COLUMN, id);
		task.put(DbConsts.TITLE_COLUMN, title);
		task.put(DbConsts.DUE_COLUMN, dueDate);
		task.put(DbConsts.PARSE_ANDROID_ID, androidId);
		task.setACL(new ParseACL(ParseUser.getCurrentUser()));
		task.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
		});
	}

	public void deleteTask(String title, Long dueDate) {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(DbConsts.PARSE_OBJECT_NAME);
		query.whereEqualTo(DbConsts.TITLE_COLUMN, title).whereEqualTo(
				DbConsts.DUE_COLUMN, dueDate);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					object.deleteInBackground();
				}
			}

		});
	}*/
}
