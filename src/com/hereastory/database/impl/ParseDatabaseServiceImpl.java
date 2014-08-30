package com.hereastory.database.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.PointOfInterestResponseHandler;
import com.hereastory.shared.LimitedPointOfInterest;
import com.hereastory.shared.PointLocation;
import com.hereastory.shared.PointOfInterest;
import com.hereastory.shared.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ParseDatabaseServiceImpl implements DatabaseService {

	private static final int POINTS_AMOUNT_LOMIT = 1000;
	private static final String LOG_TAG = "ParseDatabaseServiceImpl";
	
	public static final String USER_TABLE = "User";
	public static final String NAME = "name";
	public static final String PROFILE_PICTURE_SMALL = "profilePictureSmall";

	public static final String POI_TABLE = "PointOfInterest";
	public static final String LOCATION = "location";
	public static final String OBJECT_ID = "objectId";
	public static final String AUTHOR = "author";
	public static final String DELETED = "deleted";
	public static final String DURATION = "duration";
	public static final String TITLE = "title";
	public static final String PUBLISHED_DATE = "publishedDate";
	public static final String LIKE_COUNT = "likeCount";
	public static final String AUDIO = "audio";
	public static final String IMAGE = "image";

	@Override
	public void add(final PointOfInterest pointOfInterest, final PointOfInterestResponseHandler handler) {	
		ParseObject object = new ParseObject(POI_TABLE);		
		object.setACL(getPublicACL());
		object.put(DELETED, false);
		object.put(DURATION, pointOfInterest.getDuration());
		object.put(TITLE, pointOfInterest.getTitle());
		object.put(PUBLISHED_DATE, pointOfInterest.getCreationDate());
		object.put(LIKE_COUNT, pointOfInterest.getLikeCount());
		object.put(LOCATION, getParseGeoPoint(pointOfInterest.getLocation()));
		object.put(AUTHOR, getParseUser(pointOfInterest.getAuthor()));
		
		try {
			object.put(AUDIO, getParseFile(pointOfInterest.getAudioFilePath()));
			object.put(IMAGE, getParseFile(pointOfInterest.getImageFilePath()));
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed saving point", e);
			handler.addFailed(pointOfInterest, e);
			return;
		}
		
		object.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					handler.addCompleted(pointOfInterest);
				} else {	
					Log.e(LOG_TAG, "Failed saving point", e);
					handler.addFailed(pointOfInterest, e);
				}
			}
		});
	}

	@Override
	public void readLimited(String id, PointOfInterestResponseHandler handler){
		read(id, true, handler);
	}
	
	@Override
	public void read(String id, PointOfInterestResponseHandler handler) {
		read(id, false, handler);
	}
	
	private void read(final String id, final boolean limited, final PointOfInterestResponseHandler handler) {
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
					if (limited) {
						handler.readLimitedCompleted(pointOfInterest);
					} else {
						fillNonLimitedFields(object, (PointOfInterest)pointOfInterest);
						handler.readCompleted((PointOfInterest)pointOfInterest);
					}
				} else {
					Log.e(LOG_TAG, "Failed reading point " + id, e);
					if (limited) {
						handler.readLimitedFailed(id, e);
					} else {
						handler.readFailed(id, e);
					}
				}
			}
		});
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

	@Override
	public void readAllInArea(final PointLocation location, final double maxDistance, final PointOfInterestResponseHandler handler) {
		ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLatitude());
		ParseQuery<ParseObject> query = ParseQuery.getQuery(POI_TABLE); 
		query.whereEqualTo(DELETED, false);
		query.whereWithinKilometers(LOCATION, userLocation, maxDistance);
		query.setLimit(POINTS_AMOUNT_LOMIT);
		
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					List<PointLocation> points = new ArrayList<PointLocation>();
					for (ParseObject object : objects) {
						points.add(getLocation(object));
					}
					handler.readAllInAreaCompleted(points);
				} else {
					String errorMessage = String.format(Locale.US, "readAllInArea failed with parameters: (latitude=%d, longitude=%d, maxDistance=%d)", 
							location.getLatitude(), location.getLatitude(), maxDistance);
					Log.e(LOG_TAG, errorMessage, e);
					handler.readAllInAreaFailed(location, maxDistance, e);
				}
			}
		});
	}
	
	private PointLocation getLocation(ParseObject object) {
		ParseGeoPoint point = (ParseGeoPoint)object.get(LOCATION);
		return new PointLocation(point.getLatitude(), point.getLongitude());
	}
	
	private ParseGeoPoint getParseGeoPoint(PointLocation location) {
		return new ParseGeoPoint(location.getLatitude(), location.getLongitude());
	}
	
	private User getUser(ParseObject object, String nameField, String pictureField) {
		User user = new User();
		user.setName(object.getString(nameField));
		user.setId(object.getString(OBJECT_ID));

		// user.setPictureFilePath(pictureFilePath); // TODO
		return user;
	}
	
	private ParseUser getParseUser(User user) {
		ParseUser parseUser = new ParseUser();
		parseUser.setObjectId(user.getId());
		return parseUser;
	}
	
	private ParseFile getParseFile(String filePath) throws FileNotFoundException, IOException {
		byte[] fileBytes = IOUtils.toByteArray(new FileInputStream(filePath));
		return new ParseFile(fileBytes);
	}
	
	private ParseACL getPublicACL() {
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(true);
		return acl;
	}
}
