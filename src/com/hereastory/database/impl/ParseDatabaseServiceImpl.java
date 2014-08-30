package com.hereastory.database.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
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
	public static final String THUMBNAIL = "thumbnail";

	@Override
	public void add(final PointOfInterest pointOfInterest, byte[] thumbnail, final PointOfInterestAddHandler handler) {	
		ParseObject object = new ParseObject(POI_TABLE);		
		object.setACL(getPublicACL());
		object.put(DELETED, false);
		object.put(DURATION, pointOfInterest.getDuration());
		object.put(TITLE, pointOfInterest.getTitle());
		object.put(PUBLISHED_DATE, pointOfInterest.getCreationDate());
		object.put(LIKE_COUNT, pointOfInterest.getLikeCount());
		object.put(LOCATION, getParseGeoPoint(pointOfInterest.getLocation()));
		object.put(AUTHOR, getParseUser(pointOfInterest.getAuthor()));
		object.put(AUDIO, new ParseFile(pointOfInterest.getAudio()));
		object.put(IMAGE, new ParseFile(pointOfInterest.getImage()));
		object.put(THUMBNAIL, new ParseFile(thumbnail));
		
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
	public void readLimited(String id, PointOfInterestReadHandler handler){
		read(id, true, handler);
	}
	
	@Override
	public void read(String id, PointOfInterestReadHandler handler) {
		read(id, false, handler);
	}
	
	private void read(final String id, final boolean limited, final PointOfInterestReadHandler handler) {
		final LimitedPointOfInterest pointOfInterest = getPointOfInterestObject(limited);
		pointOfInterest.setId(id);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(POI_TABLE);
		query.whereEqualTo(OBJECT_ID, id);
		query.include(USER_TABLE);
		
		query.getFirstInBackground(new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				try {
					if (e == null) {
						handleReadCompleted(limited, handler, pointOfInterest, object);
					} else {
						handleReadFailed(id, limited, handler, e);
					}
				} catch (Exception ex) {
					handleReadFailed(id, limited, handler, ex);
				}
			}
		});
	}

	private void handleReadCompleted(final boolean limited, final PointOfInterestReadHandler handler,
			final LimitedPointOfInterest pointOfInterest, ParseObject object) throws ParseException {
		fillLimitedFields(pointOfInterest, object);
		if (limited) {
			handler.readLimitedCompleted(pointOfInterest);
		} else {
			fillNonLimitedFields(object, (PointOfInterest)pointOfInterest);
			handler.readCompleted((PointOfInterest)pointOfInterest);
		}
	}

	private void handleReadFailed(final String id, final boolean limited, final PointOfInterestReadHandler handler,
			Exception ex) {
		Log.e(LOG_TAG, "Failed reading point " + id, ex);
		if (limited) {
			handler.readLimitedFailed(id, ex);
		} else {
			handler.readFailed(id, ex);
		}
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

	private void fillLimitedFields(final LimitedPointOfInterest pointOfInterest, ParseObject object) throws ParseException {
		pointOfInterest.setCreationDate(object.getDate(PUBLISHED_DATE));
		pointOfInterest.setDuration(object.getNumber(DURATION));
		pointOfInterest.setLikeCount(object.getNumber(LIKE_COUNT));
		pointOfInterest.setAuthor(getUser(object.getParseObject(AUTHOR), NAME, PROFILE_PICTURE_SMALL));
	}
	
	private void fillNonLimitedFields(ParseObject object, PointOfInterest pointOfInterest) throws ParseException {
		pointOfInterest.setLocation(getLocation(object));
		pointOfInterest.setTitle(object.getString(TITLE));
		pointOfInterest.setImage(object.getParseFile(IMAGE).getData());
		pointOfInterest.setAudio(object.getParseFile(AUDIO).getData());
	}

	@Override
	public void readAllInArea(final PointLocation location, final double maxDistance, final PointOfInterestReadHandler handler) {
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
		ParseGeoPoint point = object.getParseGeoPoint(LOCATION);
		return new PointLocation(point.getLatitude(), point.getLongitude(), object.getString(OBJECT_ID));
	}
	
	private ParseGeoPoint getParseGeoPoint(PointLocation location) {
		return new ParseGeoPoint(location.getLatitude(), location.getLongitude());
	}
	
	private User getUser(ParseObject object, String nameField, String pictureField) throws ParseException {
		User user = new User();
		user.setName(object.getString(nameField));
		user.setId(object.getString(OBJECT_ID));
		user.setProfilePictureSmall(getBitmap(object.getParseFile(PROFILE_PICTURE_SMALL)));
		return user;
	}
	
	private Bitmap getBitmap(ParseFile file) throws ParseException {
		byte[] bytes = file.getData();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	private ParseUser getParseUser(User user) {
		ParseUser parseUser = new ParseUser();
		parseUser.setObjectId(user.getId());
		return parseUser;
	}
	
	private ParseACL getPublicACL() {
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(true);
		return acl;
	}

}
