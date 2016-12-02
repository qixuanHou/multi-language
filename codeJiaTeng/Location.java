package com.Seg;

import java.util.ArrayList;

public class Location {

	protected String location;
	protected String formattedlocation;
	protected String type;
	protected double latitude;
	protected double longitude;
	protected String placeid;
	protected ArrayList<Index> index;
	
	public Location(String l, String fl, String t, double lat, double lng, String pid) {
		this(l);
		formattedlocation = fl;
		type = t;
		latitude = lat;
		longitude = lng;
		placeid = pid;
	}
	
	public Location(String l) {
		location = l;
		index = new ArrayList<>();
	}
	
	public boolean add(Location l) {
		if (this.location.equals(l.location)) {
			for (Index i : l.index) {
				addIndex(i);
			}
			return true;
		}
		return false;
	}
	
	public void addIndex(Index a) {
		if (!index.contains(a)) {
			index.add(a);
		}
	}
	
	public void addIndex(double a, double b) {
		Index n = new Index(a, b);
		if (!index.contains(n)) {
			index.add(n);
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof Location) {
			Location that = (Location) o;
			return that.location.equals(this.location);
		}
		return false;
	}
	
	public boolean equalsGeocode(Object o) {
		if (o instanceof Location) {
			Location that = (Location) o;
			return that.placeid.equals(this.placeid);
		}
		return false;
	}
	
	public String toString() {
		if (placeid == null) {
			String s = "[";
			for (int i = 0; i < index.size(); i++) {
				if (i == index.size() - 1) {
					s += index.get(i) + "]";
				} else {
					s += index.get(i) + ", ";
				}
			}
			return location + s;
		} else {
			return location + " " + formattedlocation + "(" + latitude + ", " + longitude + ")";
		}
	}
	
//	public int compareTo(Location s) {
//	if (s == null) {
//		return -1;
//	}
//	if (this.placeid.equals(s.placeid)) {
//		return 0;
//	} else {
//		
//	}
//}
}
