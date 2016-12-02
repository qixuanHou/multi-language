package com.Seg;

import java.util.HashMap;
import java.util.ArrayList;

public class Entry {

	protected String text;
	protected String mid;
	protected String time;
	protected HashMap<String, Location> potentiallocation;
	protected Location loc;
	protected ArrayList<Index> keywordindex;
	protected int label;
	
	public Entry(String t, String m, String ti) {
		text = t;
		mid = m;
		time = ti;
		potentiallocation = new HashMap<>();
		keywordindex = new ArrayList<>();
	}
	
	public void add(Location l) {
		if (potentiallocation.get(l.location) != null) {
			potentiallocation.get(l.location).add(l);
		} else {
			potentiallocation.put(l.location, l);
		}
	}
	
	public void addKeywordIndex(double a, double b) {
		keywordindex.add(new Index(a, b));
	}
	
	public void addKeywordIndex(Index a) {
		keywordindex.add(a);
	}
	
	public void pickLocation() {
		Index average = Index.averageIndex(keywordindex);
		double smallest = 200;
		for (String st : potentiallocation.keySet()) {
			double diff = 0;
			ArrayList<Index> in = potentiallocation.get(st).index;
			for (Index i : in) {
				diff += Index.compare(average, i);
			}
			diff = diff / in.size();
			if (diff < smallest) {
				loc = potentiallocation.get(st);
				smallest = diff;
			} else if (diff == smallest) {
				Index locav = Index.averageIndex(loc.index);
				Index newav = Index.averageIndex(in);
				if (newav.endindex <= locav.endindex) {
					loc = potentiallocation.get(st);
				}
			}
		}
	}
	
	public void setLabel(int i) {
		label = i;
	}

	public String toString() {
//		String s = "";
//		for (String st : potentiallocation.keySet()) {
//			s += potentiallocation.get(st) + " ";
//		}
//		s += "keyword[";
//		for (Index i : keywordindex) {
//			s += i + ", ";
//		}
//		s += Index.averageIndex(keywordindex) + "]";
//		return loc.toString();
		return text + "\t" + mid + "\t" + time;
	}
}