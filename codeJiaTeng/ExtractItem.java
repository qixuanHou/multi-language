package com.Seg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * This file extracts labeled data items from the cluster files, such as 7yes1.txt, and outputs a file
 * that contains the data items, such as 7items.txtm, and its segmented version, such as 7items_segmented.txt
 * @author jiatengxie
 *
 */
public class ExtractItem {

	private static final String basedir = System.getProperty("SegDemo", "data");

	public static void main(String[] args) throws IOException {
		extractItems("7yes1.txt", "7items.txt");
		segmentItems("7items.txt", "7items_segmented.txt");
	}
	
	/**
	 * Expects a file that has the following format, such as 7yes1.txt
	 * 
	 * Location\tNumberOfItems\tCell
	 * Cluter label
	 * [Label\t]text\tmid\tcreatedAt
	 * ....
	 * [Label\t] text mid createdAt
	 * 
	 * Location\tNumberOfItems\tCell
	 * ...
	 * 
	 * And outputs a file that has the following format, such as 7items.txt
	 * 
	 * Label\ttext\tmid
	 * ...
	 * 
	 * @param itemfile
	 * @param outfile
	 * @throws IOException
	 */
	public static void extractItems(String itemfile, String outfile) throws IOException {
		int totalirrelevant = 0, totalrelevant = 0;
		int size = 1;
		int increment = 0;
		int counter = 0;
		int clusterlabel = 2;
		Scanner a = new Scanner(new FileInputStream(itemfile));
		PrintWriter pw = new PrintWriter(new FileOutputStream(outfile));
		while (a.hasNext()) {
			String s = a.nextLine();
			counter++;
			if (counter == size) {
				increment = Integer.parseInt(s.split("\t")[1]);
			} else if (counter == size + 1) {
				clusterlabel = Integer.parseInt(s);
				size += increment + 3;
			} else if (counter != size - 1) {
				String[] line = s.split("\t");
				String text;
				int label;
				String itemid;
				if (line.length == 4) {
					text = line[1];
					label = Integer.parseInt(line[0]);
					System.out.println(counter);
					itemid = line[2];
				} else {
					text = s.split("\t")[0];
					label = clusterlabel;
					itemid = line[1];
				}
				pw.println(label + "\t" + text + "\t" + itemid);
				if (label == 0)
					totalirrelevant++;
				else
					totalrelevant++;
			}
		}
		System.out.println("ti: " + totalirrelevant + " ri:" + totalrelevant);
		a.close();
		pw.close();
	}
	
	/**
	 * Segment the text in the following format, such as 7items.txt
	 * 
	 * Label\ttext\tmid
	 * ...
	 * 
	 * And outputs a file with segmented text in the following format, such as 7items_segmented.txt
	 * 
	 * Label\ttext\tmid
	 * ...
	 * 
	 * @param itemfile
	 * @param outfile
	 * @throws IOException
	 */
	public static void segmentItems(String itemfile, String outfile) throws IOException {
		
		System.setOut(new PrintStream(System.out, true, "utf-8"));
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", basedir);
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		
		CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
		segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		
		Scanner a = new Scanner(new FileInputStream(itemfile));
		PrintWriter pw = new PrintWriter(new FileOutputStream(outfile));
		
		while (a.hasNext()) {
			String[] e = a.nextLine().split("\t");
			String label = e[0];
			String text = e[1];
			String itemid = e[2];
			String ans = ""; 
			List<String> segmented = segmenter.segmentString(text);
			for (int i = 0; i < segmented.size(); i++) {
				if (i != segmented.size() - 1) {
					ans += segmented.get(i) + " ";
				} else {
					ans += segmented.get(i);
				}
			}
			pw.println(label + "\t" + ans + "\t" + itemid);
		}
		a.close();
		pw.close();
	}
}
