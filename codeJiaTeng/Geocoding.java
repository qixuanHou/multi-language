package com.Seg;

import java.net.URL;
import java.util.Scanner;

import org.json.*;

/**
 * Returns a location for the specified geographical name
 * @author jiatengxie
 * 
 */
public class Geocoding {

	private static final String key = "AIzaSyAtpS4U9aTF8rYU5nxEM16B_7y2ngtVUHQ";
	
	/**
	 * Returns a Location object using Google Geocoding API, including its original geographical name,
	 * the formatted address returned by Google, location type, latitude, longitude and its pid.
	 * @param location
	 * @return
	 */
	public static Location getLoc(String location) {
		
		String s = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + key;
		Scanner scan = null;
		JSONObject obj;
		do {
			try {
				URL url = new URL(s);
				scan = new Scanner(url.openStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String js = "";
			while (scan.hasNext()) {
				js += scan.nextLine();
			}
			scan.close();
			obj = new JSONObject(js);
			if (obj.getString("status").equals("ZERO_RESULTS")) {
				System.out.println("Abort: " + location + "!!!!!!!");
				return null;
			}
		} while (!obj.getString("status").equals("OK"));
		
		JSONObject res = obj.getJSONArray("results").getJSONObject(0);
		String formatted = res.getString("formatted_address");
		String ty = res.getJSONArray("types").getString(0);
		String pid = res.getString("place_id");
		JSONObject loc = res.getJSONObject("geometry").getJSONObject("location");
		double latitude = loc.getDouble("lat");
		double longitude = loc.getDouble("lng");
		
		return new Location(location, formatted, ty, latitude, longitude, pid);
		
	}
}
