package sric.iitkgp.train;
/**
	 Add feature value as 1 if token text prefix matches any from the frequent prefix list

@author ashim
*/

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.pipe.*;
import cc.mallet.types.*;
import sric.iitkgp.util.GetFrequentPrefixSuffix;

public class TokenTextCharPrefixMatch extends Pipe implements Serializable {
	private static final List<String> prefixList = new ArrayList<String>();

	private static String DEFAULT_PREFIXES = "met|ant|pro|cef|flu|sod|chl|clo|phe|eth|sul|tri|dex|pen|lev|car|ami|nor|iso|dip";

	String prefix;
	int prefixLength;

	public TokenTextCharPrefixMatch(String prefix, int prefixLength) {
		this.prefix = prefix;
		this.prefixLength = prefixLength;
	}

	public TokenTextCharPrefixMatch() {
		this("FREQPREFIX=", 2);
	}

	public Instance pipe(Instance carrier) {
		TokenSequence ts = (TokenSequence) carrier.getData();
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			String s = t.getText();
			if (s.length() > prefixLength) {
				t.setFeatureValue(prefix + getListMatch(s.toLowerCase().substring(0, prefixLength)), 1.0);
			}
//			else {
//				t.setFeatureValue((prefix + "NONE"), 1.0);
//			}
		}
		return carrier;
	}

	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
		out.writeObject(prefix);
		out.writeInt(prefixLength);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		prefix = (String) in.readObject();
		prefixLength = in.readInt();
	}

	private static void loadDefaultPrefixFixList() {
		prefixList.clear();
		String[] prefixes = DEFAULT_PREFIXES.split("|");
		for (String str : prefixes)
			prefixList.add(str);
	}

	public static boolean loadNewPrefixList(String filename, int prefixLength, int frequency) {
		if (prefixLength == 0)
			loadDefaultPrefixFixList();
		if (filename == null || "".equals(filename)) {
			filename = "resources/FrequentDataSet_Prefixes_" + prefixLength + ".txt";
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int i = 0;
			prefixList.clear();
			String line = br.readLine().trim();
			String prefix = line.split("\t")[0];
			prefixList.add(prefix);
			i++;
			line = br.readLine().trim();
			while (line != null && i < frequency) {
				if(line == null || "".equals(line)) break;
				prefix = line.split("\t")[0];
				prefixList.add(prefix);
				line = br.readLine().trim();
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Could Not find Filename. Hence Using default prefix list");
			GetFrequentPrefixSuffix.getFixes(prefixLength, false);
			boolean success = loadNewPrefixList(filename, prefixLength, frequency);
			if(!success) loadDefaultPrefixFixList();
		}
		return true;
	}
	
	private static String getListMatch(String str) {
		return (prefixList.contains(str)?str:"NONE");
	}
}
