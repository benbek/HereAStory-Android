package com.hereastory.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsFetcher {
	
	public static String makeUrl(double srcLat, double srcLon, double destLat, double destLon) {
	    StringBuilder urlString = new StringBuilder();
	    urlString.append("http://maps.googleapis.com/maps/api/directions/json?origin=");
	    urlString.append(Double.toString(srcLat)).append(",").append(Double.toString(srcLon));
	    urlString.append("&destination=");
	    urlString.append(Double.toString(destLat)).append(",").append(Double.toString(destLon));
	    urlString.append("&units=metric&mode=walking");

	    return urlString.toString();
	}
	
	public static List<LatLng> fetchPath(String jsonDirections) throws JSONException {
		final JSONObject json = new JSONObject(jsonDirections);
		JSONArray routeArray = json.getJSONArray("routes");
		JSONObject routes = routeArray.getJSONObject(0);

		JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		String encodedString = overviewPolylines.getString("points");

		return decodePoly(encodedString);
	}
	
	private static List<LatLng> decodePoly(String encoded) {
		List<LatLng> poly = new ArrayList<LatLng>();
		int index = 0;
		int length = encoded.length();
		int latitude = 0;
		int longitude = 0;

		while (index < length) {
			int b;
			int shift = 0;
			int result = 0;

			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int destLat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			latitude += destLat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);

			int destLong = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			longitude += destLong;

			poly.add(new LatLng((latitude / 1E5), (longitude / 1E5)));
		}
		return poly;
	}
}