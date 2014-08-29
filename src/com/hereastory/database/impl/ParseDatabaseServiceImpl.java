package com.hereastory.database.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.hereastory.shared.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ParseDatabaseServiceImpl implements DatabaseService {

	private static final int POINTS_AMOUNT_LOMIT = 1000;
	private static final String LOG_TAG = "ParseDatabaseServiceImpl";
	
	public static final String USER_TABLE = "User";
	public static final String NAME = "name";
	public static final String PROFILE_PICTURE_SMALL = "profilePictureSmall";


	public static final String POI_TABLE = "PointOfInterest";
	public static final String POI_EXTERNAL_TABLE = "POIExternalInfo";
	public static final String LOCATION = "location";
	public static final String OBJECT_ID = "objectId";
	public static final String AUTHOR = "author";
	public static final String DELETED = "deleted";
	public static final String DURATION = "duration";
	public static final String TITLE = "title";
	public static final String PUBLISHED_DATE = "publishedDate";
	public static final String LIKE_COUNT = "likeCount";

	

	@Override
	public void add(PointOfInterest story) {
		// TODO Auto-generated method stub
		
		/*
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
		});*/
		
	}

	@Override
	public LimitedPointOfInterest readLimited(String id){
		return read(id, true);
	}
	
	@Override
	public PointOfInterest read(String id) {
		return (PointOfInterest)read(id, false);
	}
	
	private LimitedPointOfInterest read(final String id, final boolean limited) {
		final LimitedPointOfInterest pointOfInterest = getPointOfInterestObject(limited);
		pointOfInterest.setId(id);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(POI_TABLE);
		query.whereEqualTo(OBJECT_ID, id);
		query.include(USER_TABLE);
		
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					fillLimitedFields(pointOfInterest, object);
					if (!limited) {
						fillNonLimitedFields(object, (PointOfInterest)pointOfInterest);
					}
				} else {
					Log.e(LOG_TAG, "Failed reading point " + id, e);
					throw new RuntimeException(e);
				}
			}
		});

		return pointOfInterest;
	}

	private LimitedPointOfInterest getPointOfInterestObject(final boolean limited) {
		final LimitedPointOfInterest pointOfInterest;
		if (limited) {
			pointOfInterest = new LimitedPointOfInterest();
		} else {
			pointOfInterest = new PointOfInterest();
		}
		return pointOfInterest;
	}

	private void fillLimitedFields(final LimitedPointOfInterest pointOfInterest, ParseObject object) {
		pointOfInterest.setCreationDate(object.getDate(PUBLISHED_DATE));
		pointOfInterest.setDuration(object.getNumber(DURATION));
		pointOfInterest.setLikeCount(object.getNumber(LIKE_COUNT));
		pointOfInterest.setAuthor(getUser(object.getParseObject(AUTHOR), NAME, PROFILE_PICTURE_SMALL));
	}
	
	private void fillNonLimitedFields(ParseObject object, PointOfInterest pointOfInterest) {
		pointOfInterest.setLocation(getLocation(object));
		pointOfInterest.setTitle(object.getString(TITLE));
		//pointOfInterest.setAudioFilePath(audioFilePath); // TODO save file to disk
		//pointOfInterest.setPictureFilePath(pictureFilePath);// TODO
	}
	
	private User getUser(ParseObject object, String nameField, String pictureField) {
	    User user = new User();
	    user.setName(object.getString(nameField));
	   // user.setPictureFilePath(pictureFilePath); // TODO
		return user;
	}

	@Override
	public List<PointLocation> readAllInArea(final PointLocation location, final double maxDistance) {
		ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLatitude());
		ParseQuery<ParseObject> query = ParseQuery.getQuery(POI_TABLE); 
		query.whereEqualTo(DELETED, false);
		query.whereWithinKilometers(LOCATION, userLocation, maxDistance);
		query.setLimit(POINTS_AMOUNT_LOMIT);
		List<ParseObject> queryResult;
		try {
			queryResult = query.find(); // TODO BG
		} catch (ParseException e) {
			String errorMessage = String.format(Locale.US, "readAllInArea failed with parameters: (latitude=%d, longitude=%d, maxDistance=%d)", 
					location.getLatitude(), location.getLatitude(), maxDistance);
			Log.e(LOG_TAG, errorMessage, e);
			throw new RuntimeException(e);
		}
		List<PointLocation> points = new ArrayList<PointLocation>();
		for (ParseObject object : queryResult) {
			points.add(getLocation(object));
		}
		return points;
	}
	
	private PointLocation getLocation(ParseObject object) {
		ParseGeoPoint point = (ParseGeoPoint)object.get(LOCATION);
		return new PointLocation(point.getLatitude(), point.getLongitude());
	}
}
