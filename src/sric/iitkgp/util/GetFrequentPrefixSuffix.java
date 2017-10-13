package sric.iitkgp.util;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.io.woodstox.ConnectorWoodstox;

public class GetFrequentPrefixSuffix {
	public static final String PATH_TO_TRAIN_FILE = "resources/ddiAllDataInBioC_train.xml";
	public static Integer fixLength;

	public static void main(String[] args) {
		if(fixLength == null || fixLength == 0) fixLength = 3;
		getFixes(fixLength,false);
	}
	
	public static List<String> getFixes(int n, boolean isSuffix){
		
		Map<String, Integer> suffixMap = new HashMap<String, Integer>();
		
		Set<String> stringSet = new HashSet<String>();
		
		List<BioCDocument> bioCDocList = new ArrayList<BioCDocument>();
		
		ConnectorWoodstox connector = new ConnectorWoodstox();
		try {
			connector.startRead(new InputStreamReader(new FileInputStream(PATH_TO_TRAIN_FILE), "UTF-8"));
			while(connector.hasNext()) {
				BioCDocument document = connector.next();
				String docId = document.getID();
				if(docId != null && !("".equals(docId))) bioCDocList.add(document);
			}
			
			
		} catch (UnsupportedEncodingException | FileNotFoundException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		for (BioCDocument doc : bioCDocList) {
			i++;
//			if (i%50 == 0) System.out.println("Processed "+ i + " documents...");
			for(BioCPassage passage : doc.getPassages()) {
				for(BioCAnnotation annotation : passage.getAnnotations()) {
					String annText = annotation.getText().toLowerCase();
					if(stringSet.contains(annText)) continue;
					stringSet.add(annText);
					String fix;
					if(isSuffix) fix = getSuffix(annText, n);
					else {
						fix = getPrefix(annText, n);
					}
					if(!suffixMap.containsKey(fix)) suffixMap.put(fix, 1);
					else {
						suffixMap.replace(fix, suffixMap.get(fix) + 1);
					}
				}
			}
		}
		
//		System.out.println("Printing " + (isSuffix?"Suffixes":"Prefixes") + " ....");
//		for(String key : suffixMap.keySet()) {
//			System.out.println(key + "\t" + suffixMap.get(key));
//		}
		suffixMap.remove(null);
		Map<String, Integer> sortedMap = 
				suffixMap.entrySet().stream()
			    .sorted(Entry.<String, Integer>comparingByValue().reversed())
			    .collect(Collectors.toMap(Entry::getKey, Entry::getValue,
			                              (e1, e2) -> e1, LinkedHashMap::new));
		
//		System.out.println("Printing " + (isSuffix?"Suffixes":"Prefixes") + " ....");
		for(String key : sortedMap.keySet()) {
//			System.out.println(key + "\t" + sortedMap.get(key));
		}
		
		try {
			writeFixesToFile(sortedMap, n, isSuffix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String getSuffix(String str, int n) {
		if(str == null || "".equals(str) || str.length() < n) return null;
		return str.substring(str.length()-n, str.length());
	}
	
	public static String getPrefix(String str, int n) {
		if(str == null || "".equals(str) || str.length() < n) return null;
		return str.substring(0, n);
	}
	
	public static void writeFixesToFile(Map<String, Integer> sortedMap, int n, boolean isSuffix) throws IOException{
		String filename = "resources/FrequentDataSet_" + (isSuffix?"Suffixes":"Prefixes") + "_" + n + ".txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));
		for(String key : sortedMap.keySet()) {
			try {
				out.write(key + "\t" + sortedMap.get(key) + "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.close();
		
	}
}
