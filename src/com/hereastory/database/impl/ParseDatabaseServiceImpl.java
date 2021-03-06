package com.hereastory.database.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import android.util.Log;

import com.hereastory.database.api.DatabaseService;
import com.hereastory.service.api.BitmapService;
import com.hereastory.service.api.OutputFileService;
import com.hereastory.service.api.OutputFileService.FileType;
import com.hereastory.service.api.PointOfInterestAddHandler;
import com.hereastory.service.api.PointOfInterestReadHandler;
import com.hereastory.service.impl.BitmapServiceImpl;
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

	private static final String THUMBNAIL_FILENAME = "thumb.";
	private static final String IMAGE_FILENAME = "image.";
	private static final String AUDIO_FILENAME = "audio.";
	private static final int POINTS_AMOUNT_LOMIT = 1000;
	private static final String LOG_TAG = "ParseDatabaseServiceImpl";
	
	private static final String USER_TABLE = "User";
	private static final String USER_FACEBOOK_TABLE = "UserFacebook";
	private static final String NAME = "name";
	private static final String FACEBOOK_ID = "facebookId";
	private static final String PROFILE_PICTURE_SMALL = "profilePictureSmall";

	private static final String POI_TABLE = "PointOfInterest";
	private static final String POI_EXTERNAL_TABLE = "POIExtraInfo";

	private static final String LOCATION = "location";
	private static final String AUTHOR = "author";
	private static final String DELETED = "deleted";
	private static final String DURATION = "duration";
	private static final String TITLE = "title";
	private static final String PUBLISHED_DATE = "publishedDate";
	public static final String LIKE_COUNT = "likeCount";
	private static final String AUDIO = "audio";
	private static final String IMAGE = "image";
	private static final String THUMBNAIL = "thumbnail";
	private static final String POI_KEY = "poi";
	private static final String USER = "user";

	private final OutputFileService outputFileService;
	private final BitmapService bitmapService;
	
	public ParseDatabaseServiceImpl(OutputFileService outputFileService) {
		this.outputFileService = outputFileService;
		this.bitmapService = new BitmapServiceImpl();
	}
	
	@Override
	public void add(final PointOfInterest pointOfInterest, byte[] thumbnail, final PointOfInterestAddHandler handler) {
		final ParseObject object = new ParseObject(POI_TABLE);		
		//object.setACL(getPublicACL());
		try {
			setPointFields(pointOfInterest, object);
		} catch (IOException e) {
			Log.e(LOG_TAG, "Failed saving point", e);
			handler.addFailed(pointOfInterest, e);
			return;
		}
		
		SaveCallback saveCallback = new PointOfInterestSaveCallback(pointOfInterest, handler, object);
		object.saveInBackground(saveCallback);
	}

	private void setPointFields(final PointOfInterest pointOfInterest, ParseObject object) throws IOException {
		object.put(DELETED, false);
		object.put(DURATION, pointOfInterest.getDuration());
		object.put(TITLE, pointOfInterest.getTitle());
		object.put(PUBLISHED_DATE, pointOfInterest.getCreationDate());
		object.put(LIKE_COUNT, pointOfInterest.getLikeCount());
		object.put(LOCATION, getParseGeoPoint(pointOfInterest.getLocation()));
		object.put(AUTHOR, ParseUser.getCurrentUser());
		object.put(AUDIO, getParseFile(AUDIO_FILENAME+FileType.AUDIO.getSuffix(), pointOfInterest.getAudio()));
		object.put(IMAGE, getParseFile(IMAGE_FILENAME+FileType.IMAGE.getSuffix(), pointOfInterest.getImage()));
		addThumbnail(object, pointOfInterest);
	}

	private void addThumbnail(ParseObject object, PointOfInterest pointOfInterest) {
		try {
			byte[] thumbnail = bitmapService.getThumbnail(pointOfInterest.getImage());
			object.put(THUMBNAIL, new ParseFile(THUMBNAIL_FILENAME+FileType.IMAGE.getSuffix(), thumbnail));
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed generating thumbnail", e);
		}
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
		query.include(USER_TABLE);
		query.getInBackground(id, new GetCallback<ParseObject>() {
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
			final LimitedPointOfInterest pointOfInterest, ParseObject object) throws ParseException, IOException {
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

	private void fillLimitedFields(final LimitedPointOfInterest pointOfInterest, ParseObject object) throws ParseException, IOException {
		pointOfInterest.setCreationDate(object.getDate(PUBLISHED_DATE));
		pointOfInterest.setDuration(object.getNumber(DURATION));
		pointOfInterest.setLikeCount(object.getNumber(LIKE_COUNT));
		pointOfInterest.setTitle(object.getString(TITLE));
		pointOfInterest.setAuthorName(getAuthorName(object.getParseObject(AUTHOR)));
	}

	private void fillNonLimitedFields(ParseObject object, PointOfInterest pointOfInterest) throws ParseException, IOException {
		pointOfInterest.setLocation(getLocation(object));
		
		String imageFilePath = saveFile(object, IMAGE, FileType.IMAGE);
		pointOfInterest.setImage(imageFilePath);
		
		String audioFilePath = saveFile(object, AUDIO, FileType.AUDIO);
		pointOfInterest.setAudio(audioFilePath);

		pointOfInterest.setAuthor(getUser(object.getParseObject(AUTHOR)));

	}

	private String saveFile(ParseObject object, String field, FileType fileType) throws ParseException, IOException {
		byte[] bytes = object.getParseFile(field).getData();
		String filePath = outputFileService.getOutputMediaFile(fileType, object.getObjectId()).getAbsolutePath();
		IOUtils.write(bytes, new FileOutputStream(filePath));
		return filePath;
	}

	@Override
	public void readAllInArea(final double latitude, final double longitude, final double maxDistance, final PointOfInterestReadHandler handler) {
		ParseGeoPoint userLocation = new ParseGeoPoint(latitude, longitude);
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
					String errorMessage = String.format(Locale.US, "readAllInArea failed with parameters: (latitude=%f, longitude=%f, maxDistance=%f)", 
							latitude, longitude, maxDistance);
					Log.e(LOG_TAG, errorMessage, e);
					handler.readAllInAreaFailed(latitude, longitude, maxDistance, e);
				}
			}
		});
	}
	
	private PointLocation getLocation(ParseObject object) {
		ParseGeoPoint point = object.getParseGeoPoint(LOCATION);
		return new PointLocation(point.getLatitude(), point.getLongitude(), object.getObjectId());
	}
	
	private ParseGeoPoint getParseGeoPoint(PointLocation location) {
		return new ParseGeoPoint(location.getLatitude(), location.getLongitude());
	}
	
	private String getAuthorName(ParseObject parseUser) throws ParseException {
		parseUser.fetchIfNeeded();
		if (parseUser.getString(NAME) == null ){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_FACEBOOK_TABLE); 
			query.whereEqualTo(USER, parseUser);
			List<ParseObject> result = query.find();
			if (result.isEmpty()) {
				return "";
			} else {
				ParseObject fbInfo = result.iterator().next();
				return fbInfo.getString(NAME);
			}
		} else {
			return parseUser.getString(NAME);
		}
	}

	private User getUser(ParseObject parseUser) throws ParseException, IOException {
		parseUser.fetchIfNeeded();
		String userId = parseUser.getObjectId();
		User user = new User();
		user.setId(userId);
		ParseFile profilePicFile;
		if (parseUser.getString(NAME) == null ){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_FACEBOOK_TABLE); 
			query.whereEqualTo(USER, parseUser);
			List<ParseObject> result = query.find();
			if (result.isEmpty()) {
				return user;
			} else {
				ParseObject fbInfo = result.iterator().next();
				user.setName(fbInfo.getString(NAME));
				profilePicFile = fbInfo.getParseFile(PROFILE_PICTURE_SMALL);
			}
		} else {
			user.setName(parseUser.getString(NAME));
			profilePicFile = parseUser.getParseFile(PROFILE_PICTURE_SMALL);
		}
		if (profilePicFile != null) {
			setProfilePicture(userId, user, profilePicFile);
		}
		return user;
	}

	private void setProfilePicture(String userId, User user, final ParseFile profilePicFile) throws ParseException, IOException,
			FileNotFoundException {
		byte[] bytes = profilePicFile.getData();
		String filePath = outputFileService.getProfilePictureFile(userId).getAbsolutePath();
		if (!new File(filePath).exists()) {
			IOUtils.write(bytes, new FileOutputStream(filePath));
		}
		user.setProfilePictureSmall(filePath);
	}

	private ParseFile getParseFile(String name, String filePath) throws FileNotFoundException, IOException {
		byte[] bytes = IOUtils.toByteArray(new FileInputStream(filePath));
		return new ParseFile(name, bytes);
	}

	private ParseACL getPublicACL() {
		ParseACL acl = new ParseACL();
		acl.setPublicReadAccess(true);
		return acl;
	}
	
	class PointOfInterestSaveCallback extends SaveCallback {
		
		private PointOfInterest pointOfInterest;
		private PointOfInterestAddHandler handler;
		private ParseObject object;
		
		private PointOfInterestSaveCallback(final PointOfInterest pointOfInterest, final PointOfInterestAddHandler handler, ParseObject object ) {
			super();
			this.pointOfInterest = pointOfInterest;
			this.handler = handler;
			this.object = object;
		}
		
		@Override
		public void done(ParseException e) {
			if (e == null) {
				// save external object
                ParseObject objectExternal = new ParseObject(POI_EXTERNAL_TABLE);		
                //objectExternal.setACL(getPublicACL());
                objectExternal.put(POI_KEY, object);
        		try {
        			setPointFields(pointOfInterest, objectExternal);
        		} catch (IOException e1) {
        			Log.e(LOG_TAG, "Failed saving point", e1);
        			handler.addFailed(pointOfInterest, e1);
        			return;
        		}
        		
        		objectExternal.saveInBackground(new SaveCallback() {

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
			} else {	
				Log.e(LOG_TAG, "Failed saving point", e);
				handler.addFailed(pointOfInterest, e);
			}
		}
	}

	@Override
	public void addFacebookUser(String facebookId, String name, byte[] profilePicture) {
		try {
			final ParseObject object = new ParseObject(USER_FACEBOOK_TABLE);		
			//object.setACL(getPublicACL());
			object.put(NAME, name);
			object.put(FACEBOOK_ID, facebookId);
			object.put(USER, ParseUser.getCurrentUser());
			if (profilePicture != null) {
				ParseFile parseFile = new ParseFile("smallProfile.jpg", profilePicture);
				parseFile.save();
				object.put(PROFILE_PICTURE_SMALL, parseFile);
			}
			object.save();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed saving user", e);
		} 
	}

	@Override
	public User getCurrentAuthor() {
		try {
			return getUser(ParseUser.getCurrentUser());
		} catch (Exception e) {
			Log.e(LOG_TAG, "Failed reading user", e);
			return new User();
		}
	}

	@Override
	public void incrementLikeCount(String storyId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(POI_TABLE);
		query.getInBackground(storyId, new GetCallback<ParseObject>() {
			@Override
			public void done(ParseObject object, ParseException e) {
				try {
					if (e == null) {
						Integer newValue = (Integer) object.getNumber(LIKE_COUNT) + 1;
						object.put(LIKE_COUNT, newValue);
						object.save();
					} else {
						Log.e(LOG_TAG, "Failed adding like", e);
					}
				} catch (Exception ex) {
					Log.e(LOG_TAG, "Failed adding like", ex);
				}
			}
		});
	}
}
