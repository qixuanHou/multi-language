package com.Seg;

import java.util.ArrayList;

public class Index {

	protected double startindex;
	protected double endindex;
	
	public Index(double a, double b) {
		startindex = a;
		endindex = b;
	}
	
	public String toString() {
		return "(" + startindex + ", " + endindex + ")";
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof Index) {
			Index other = (Index) o;
			return this.startindex == other.startindex && this.endindex == other.endindex;
		}
		return false;
	}
	
	public static Index averageIndex(ArrayList<Index> index) {
		double a = 0;
		double b = 0;
		for (Index i : index) {
			a += i.startindex;
			b += i.endindex;
		}
		return new Index((a/index.size()), (b/index.size()));
	}
	
	public static double compare(Index a, Index b) {
		if (a.startindex >= b.endindex) {
			return a.startindex - b.endindex;
		} else if (a.endindex <= b.startindex) {
			return b.startindex - a.endindex;
		} else {
			return 0;
		}
	}
}
