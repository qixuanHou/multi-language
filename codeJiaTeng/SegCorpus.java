package com.Seg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
/**
 * Perform word-segmentation on a Chinese text corpus
 * @author jiatengxie
 *
 */
public class SegCorpus {

	private static final String basedir = System.getProperty("SegDemo", "data");
	
	public static void main(String[] args) throws Exception {
		
		//corpus source
		Scanner a = new Scanner(new FileInputStream("corpus_a.txt"));
		//output file for segmented corpuse
		PrintWriter aa = new PrintWriter(new FileOutputStream("corpus_a_seg.txt"));
		
		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", basedir);
		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");
		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		
		CRFClassifier<CoreLabel> segmenter = new CRFClassifier<CoreLabel>(props);
		segmenter.loadClassifierNoExceptions(basedir + "/ctb.gz", props);
		
		while (a.hasNext()) {
			List<String> segmented = segmenter.segmentString(a.nextLine());
			String ans = "";
			for (String s : segmented) {
				ans += s + " ";
			}
			aa.println(ans);
		}
		a.close();
		aa.close();
	}
	
}