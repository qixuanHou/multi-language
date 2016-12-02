package com.Seg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import edu.stanford.nlp.trees.tregex.tsurgeon.ParseException;

public class Cluster implements Comparable<Cluster> {

	protected ArrayList<Entry> entry;
	protected int[][] timevec;
	protected Location location;
	protected String cell;
	
	public Cluster(Location loc) {
		entry = new ArrayList<>();
		location = loc;
	}
	
	public Cluster(Location loc, Entry e) {
		this(loc);
		add(e);
	}
	
	public void add(Entry e) {
		entry.add(e);
	}
	
	public void geocode() {
		location = Geocoding.getLoc(location.location);
	}
	
	public int compareTo(Cluster e) {
		return entry.size() - e.entry.size();
	}
	
	public String toString() {
		String s = location + "\t" + entry.size() + "\tcell" + cell + "\n";
		for (Entry e : entry) {
			s += e + "\n";
		}
		return s;
	}
	
	public String timeString() {
		String s = location + "\t" + entry.size() + "\tcell" + cell + "\n";
		for (int i = 0; i < 31; i++) {
			s += timevec[i][0] + "\n";
		}
		return s;
	}
	
	public void cell() {
		int row = (int) ((90 + location.latitude) * 24);
		int column = (int) ((180 + location.longitude) * 24);
		cell = "(" + row + ", " + column + ")";
	}
	
//	public void timeToVec() {
//		timevec = new int[31][1];
//		for (int i = 0; i < 31; i++) {
//			timevec[i][0] = 0;
//		}
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Calendar cal = Calendar.getInstance();
//		for (Entry e : entry) {
//			try {
//				Date date = df.parse(e.time);
//				cal.setTime(date);
//				timevec[cal.get(Calendar.DAY_OF_MONTH) - 1][0]++;
//			} catch (Exception ee) {
//				ee.printStackTrace();
//			}
//		}
//	}
}