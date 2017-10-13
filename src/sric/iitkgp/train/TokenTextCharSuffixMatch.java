package sric.iitkgp.train;
/**
	 Add feature value as 1 if token text suffix matches any from the frequent suffix list

@author ashim
*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.*;
import cc.mallet.types.*;
import sric.iitkgp.util.GetFrequentPrefixSuffix;

public class TokenTextCharSuffixMatch extends Pipe implements Serializable {
	private static final List<String> sufffixList = new ArrayList<String>();

	private static String DEFAULT_SUFFIXES = "ine|ide|ate|one|cin|ium|ole|cid|rin|tin|lin|tan|vir|lol|hcl|ane|nol|ene|pam|ron";

	String suffix;
	int sufffixLength;

	public TokenTextCharSuffixMatch(String suffix, int sufffixLength) {
		this.suffix = suffix;
		this.sufffixLength = sufffixLength;
	}

	public TokenTextCharSuffixMatch() {
		this("FREQPREFIX=", 2);
	}

	public Instance pipe(Instance carrier) {
		TokenSequence ts = (TokenSequence) carrier.getData();
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			String s = t.getText();
			if (s.length() > sufffixLength) {
				t.setFeatureValue(suffix + getListMatch(s.toLowerCase().substring(0, sufffixLength)), 1.0);
			}
//			else {
//				t.setFeatureValue((suffix + "NONE"), 1.0);
//			}
		}
		return carrier;
	}

	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
		out.writeObject(suffix);
		out.writeInt(sufffixLength);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		suffix = (String) in.readObject();
		sufffixLength = in.readInt();
	}

	private static void loadDefaultSuffixFixList() {
		sufffixList.clear();
		String[] prefixes = DEFAULT_SUFFIXES.split("|");
		for (String str : prefixes)
			sufffixList.add(str);
	}

	public static boolean loadNewSuffixList(String filename, int sufffixLength, int frequency) {
		if (sufffixLength == 0)
			loadDefaultSuffixFixList();
		if (filename == null || "".equals(filename)) {
			filename = "resources/FrequentDataSet_Prefixes_" + sufffixLength + ".txt";
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int i = 0;
			sufffixList.clear();
			String line = br.readLine().trim();
			String suffix = line.split("\t")[0];
			sufffixList.add(suffix);
			i++;
			line = br.readLine().trim();
			while (line != null && i < frequency) {
				if(line == null || "".equals(line)) break;
				suffix = line.split("\t")[0];
				sufffixList.add(suffix);
				line = br.readLine().trim();
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Could Not find Filename. Hence Using default suffix list");
			GetFrequentPrefixSuffix.getFixes(sufffixLength, false);
			boolean success = loadNewSuffixList(filename, sufffixLength, frequency);
			if(!success) loadDefaultSuffixFixList();
		}
		return true;
	}
	
	private static String getListMatch(String str) {
		return (sufffixList.contains(str)?str:"NONE");
	}
}
