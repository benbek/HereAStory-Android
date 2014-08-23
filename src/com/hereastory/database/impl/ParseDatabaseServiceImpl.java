package com.hereastory.database.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ParseDatabaseServiceImpl implements DatabaseService {

	private static final int POINTS_AMOUNT_LOMIT = 1000;
	private static final String LOG_TAG = "ParseDatabaseServiceImpl";

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
	public List<PointLocation> readAllInArea(final PointLocation location, final double maxDistance) throws ParseException {
		ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLatitude());
		ParseQuery<ParseObject> query = ParseQuery.getQuery(PointOfInterestParseConsts.TABLE_NAME); 
		query.whereWithinKilometers(PointOfInterestParseConsts.LOCATION, userLocation, maxDistance);
		query.setLimit(POINTS_AMOUNT_LOMIT);
		List<ParseObject> queryResult;
		try {
			queryResult = query.find();
		} catch (ParseException e) {
			String errorMessage = String.format(Locale.US, "readAllInArea failed with parameters: (latitude=%d, longitude=%d, maxDistance=%d)", 
					location.getLatitude(), location.getLatitude(), maxDistance);
			Log.e(LOG_TAG, errorMessage, e);
			throw e;
		}
		List<PointLocation> points = new ArrayList<PointLocation>();
		for (ParseObject parseObject : queryResult) {
			ParseGeoPoint point = (ParseGeoPoint)parseObject.get(PointOfInterestParseConsts.LOCATION);
			points.add(new PointLocation(point.getLatitude(), point.getLongitude()));
		}
		return points;
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
