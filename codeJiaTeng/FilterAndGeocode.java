package com.Seg;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;

/**
 * Outputs files such as 7yes1.txt using files that contain data items such as 7.txt
 * @author jiatengxie
 *
 */
public class FilterAndGeocode {

	private static final String basedir = System.getProperty("SegDemo", "data");
	
	//locations that must be matched by full name. For instance, "公安" cannot match "公安县".
	private static final String[] stoploc = {"公安", "资源", "平安", "南部", "尼玛", "合作", "清新", "通道", "莲花", "比如", "温泉", "和平", "安宁", "盘山", "安宁", "凤凰"};
	
	//Special cases. For example, "九寨" can match "九寨沟县", even though the difference between their length is 2.
	private static final String[] okloc = {"九寨", "峨眉", "攀枝", "神农架"};
	
	//stop words and phrases
	private static final String[] stopwords = {"崩塌", "山崩"};
		//{"腐败", "经济", "道德", "股市", "质量", "感情", "市值", "精神", "走滑坡路", "待遇", "官场", "信仰", "南国塌方", "公德", "房价", "业绩", "信念", "销售", "情感好文", "股票", "思想", "物质"};
	
	//all the spams
	private static ArrayList<String> spam = new ArrayList<>();

	//initialize the spams
	static {
		try {
			Scanner sp = new Scanner(new FileInputStream("spam.txt"));
	  		while (sp.hasNext()) {
	  			spam.add(sp.nextLine().replaceAll("\\s", ""));
	  		}
	  		sp.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
	  
		//load all the data items from the specified month
		ArrayList<Entry> st = loadFile("11.txt");
		
		//load the gazetteer
		String alllocation = getDictionary("chinalocation.txt");
		
		System.setOut(new PrintStream(System.out, true, "utf-8"));
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", basedir);
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		
		//initialize the segmenter
		CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
		segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		
		//initialize the NER classifier
		String serializedClassifier = "data/chinese.misc.distsim.crf.ser.gz";
		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
		
		try {
			//specify the output stream
			PrintWriter aa = new PrintWriter(new FileOutputStream("11yes1.txt"));

			//the total number of items after geocoding and filtering
			int total = 0;
			
			//the parameter cl contains the sorted clusters
			List<Cluster> cl = loc(segmenter, classifier, alllocation, st);
			
			for (Cluster c : cl) {
				total += c.entry.size();	
				//computes its cell
				c.cell();	
				//prints it to the output stream
				aa.println(c);
			}
			
			//prints at the end of the file the number of clusters and the number of data items
			aa.println(total);
			aa.println(cl.size());
			aa.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Load all the entries from the specified file following the format
	 * 
	 * text
	 * mid
	 * createdat
	 * ...
	 * 
	 * Returns an ArrayList that contains all the entries that don't contain stop words or spams
	 * @param file
	 * @return
	 * @throws IOException
	 */
  	public static ArrayList<Entry> loadFile(String file) throws IOException {
  		ArrayList<Entry> st = new ArrayList<>();
  		Scanner a = new Scanner(new FileInputStream(file));
  		int index = 1;
  		String text = "";
  		String mid = "";
  		String time = "";
  		int totalcount = 0;
  		int spamcount = 0;
  		int stopcount = 0;
  		int left = 0;
  		while (a.hasNext()) {
  			if (index % 3 == 1) {
 				text = a.nextLine();
 			} else if (index % 3 == 2) {
  				mid = a.nextLine();
  			} else {
  				time = a.nextLine();
  				
  				//checks if the text contains stopwords
  				boolean isstop = false;
  				for (String s : stopwords) {
  					if (text.contains(s)) {
  						isstop = true;
  					}
  				}
  				
  				//checks if it is in the spam database
  				boolean isspam = false;
  				for (String t : spam) {
  					if (t.contains(text.replaceAll("\\s", "")) || text.replaceAll("\\s", "").contains(t)) {
  						isspam = true;
  					}
  				}
  				
  				if (isspam) {
  					spamcount++;
  				}
  				if (!isspam && isstop) {
  					stopcount++;
  				}
  				
  				//add it to the collection if it is not spam and does not contain stopwords
  				if (!isspam && !isstop) {
  					st.add(new Entry(text, mid, time));
  					left++;
  				}
  				totalcount++;
  			}
  			index++;
  		}
  		a.close();
  		System.out.printf("total:%d, after spam:%d, after stop:%d, left:%d", totalcount, totalcount - spamcount, totalcount - spamcount - stopcount, left);
  		return st;
  	}
  	
  	/**
  	 * Loads the dicitonary into one long string
  	 * @param file
  	 * @return
  	 * @throws IOException
  	 */
  	public static String getDictionary(String file) throws IOException {
  		Scanner location = new Scanner(new FileInputStream(file));
  		String dictionary = "";
  		if (location.hasNext()) {
  			dictionary = location.nextLine();
  		}
  		System.out.println(" dict: " + dictionary.split(" ").length);
  		location.close();
  		return dictionary;
  	}
  	
  	/**
  	 * This method firstly extracts the location entity for each data item if there is any, and then groups data items that
  	 * have the same location entity into clusters and sort the clusters in descending order in terms of the number of items
  	 * it has.
  	 * @param segmenter
  	 * @param classifier
  	 * @param dictionary
  	 * @param st
  	 * @return
  	 */
  	public static List<Cluster> loc(CRFClassifier<CoreLabel> segmenter, AbstractSequenceClassifier<CoreLabel> classifier,
  									 String dictionary, ArrayList<Entry> st) {
  		HashMap<String, Cluster> clusters = new HashMap<>();
  		for (Entry entry : st) {
 	
  			//segments the text into words
			List<String> segmented = segmenter.segmentString(entry.text);
			
			//this is the segmented text that contains all the words separated by white space
			String ans = "";
			
			//si is the starting index of the word in the original text (not the segmented one)
			int si = 0;
			
			//ifprint notes whether or not the entry has location entity in it, and if not, it will be filtered out
			boolean ifprint = false;
			
			//for each word of the text
			for (String ss : segmented) {
				
				//add the word to the whole word-segmented text
				ans += ss + " ";
				
				//only search words in the dictionary whose lengths are greater than or equal to 2
				if (ss.length() >= 2) {
					
					//checks if the dictionary contains the word and the word is not in the middle of a geo-name.
					if (dictionary.contains(ss)) {
						
						//the index of the word in the gazetteer
						int index = dictionary.indexOf(ss);
						
						//either the previous character is a white space or it is the first character
						if (index == 0 || dictionary.charAt(index - 1) == 32) {
							
							//checks if this word is in the stoploc
							boolean isstoploc = false;
							for (String stop : stoploc) {
								if (ss.equals(stop)) {
									isstoploc = true;
								}
							}		
							if (!isstoploc) {
								
								//get the location name from the gazetteer
								String reloc = dictionary.substring(index, dictionary.indexOf(" ", index));
								
								//make sure the difference between the length of the word and the length of the location name is less than 2
								//unless it is in the list of special locations
								boolean isoklocation = true;
								if (reloc.length() == ss.length() + 2) {
									isoklocation = false;
									for (String ok : okloc) {
										if (ss.equals(ok)) {
											isoklocation = true;
										}
									}
								}
								
								//add the found location to the entry and store the index of the location
								if (isoklocation) {
									Location l = new Location(reloc);
									l.addIndex(new Index(si, si+ss.length()));
									entry.add(l);
									ifprint = true;
								}
							}
						}
					}
				}
				si += ss.length();
			}
			
			//word-segmented text
			ans = ans.substring(0, ans.length() - 1);
			
			//use the NER classifier to find out if there is any word labeled as GPE, which we are interested in
			//add the word that is labeled as GPE to the entry and store the its index
			if (classifier.classifyToString(ans).contains("GPE")) {
				List<Triple<String,Integer,Integer>> triples = classifier.classifyToCharacterOffsets(ans);
				for (Triple<String,Integer,Integer> trip : triples) {
					if (trip.first().equals("GPE")) {
						String loc = ans.substring(trip.second, trip.third);
						if (loc.length() >= 2) {
							ifprint = true;
							String temp = ans.substring(0, trip.second);
							int space = temp.length() - temp.replaceAll("\\s", "").length();
							int startindex = trip.second - space;
							int endindex = trip.third - (loc.length() - loc.replaceAll("\\s", "").length()) - space;
							Location l = new Location(loc.replaceAll("\\s", ""));
							l.addIndex(new Index(startindex, endindex));
							entry.add(l);
						}
					}
				}
			}
			
			//add all the indexes for all the keywords to prepare for the location selection
			String t = ans.replaceAll("\\s", "");
			int sindex = t.indexOf("滑坡");
			while (sindex >= 0) {
				entry.addKeywordIndex(new Index(sindex, sindex + 2));
				sindex = t.indexOf("滑坡", sindex + 2);
			}
			sindex = t.indexOf("泥石流");
			while (sindex >= 0) {
				entry.addKeywordIndex(new Index(sindex, sindex + 3));
				sindex = t.indexOf("泥石流", sindex + 3);
			}
			sindex = t.indexOf("塌方");
			while (sindex >= 0) {
				entry.addKeywordIndex(new Index(sindex, sindex + 2));
				sindex = t.indexOf("塌方", sindex + 2);
			}
			
			//if the entry does contain location entity, pick the correct location entity and then add it to its corresponding cluster
			if (ifprint) {
				entry.pickLocation();
				if (clusters.get(entry.loc.location) == null) {
					clusters.put(entry.loc.location, new Cluster(entry.loc, entry));
				} else {
					clusters.get(entry.loc.location).add(entry);
				}
			}
			
		}
		
  		//we perform geocoding on each cluster to get a formatted address, and add clusters together if they
  		//have the same formatted address.
  		//we sort the clusters in descending orders in terms of their size afterwards.
		List<Cluster> cs = new ArrayList<>(clusters.values());
		HashMap<Location, Cluster> c = new HashMap<>();
		Collections.sort(cs);
		Collections.reverse(cs);
		for (Cluster cl : cs) {
			if (cl.entry.size() >= 6) {
				cl.geocode();
				if (cl.location != null) {
					boolean has = false;
					Location ss = null;
					for (Location l : c.keySet()) {
						if (l.equalsGeocode(cl.location)) {
							has = true;
							ss = l;
						}
					}
					if (!has) {
						c.put(cl.location, cl);
					} else {
						c.get(ss).entry.addAll(cl.entry);
					}
				}
			}
		}
		cs = new ArrayList<>(c.values());
		Collections.sort(cs);
		Collections.reverse(cs);
		System.out.println(cs.size());
		return cs;
  	}
}