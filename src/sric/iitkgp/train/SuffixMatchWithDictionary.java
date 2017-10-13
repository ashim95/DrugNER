package sric.iitkgp.train;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

public class SuffixMatchWithDictionary extends Pipe implements Serializable {

	private static String default_suffix_list = "ine|ate|ide|one|cid|act|ium|ole|ion|hcl|num|oil|nol|cin|cal|lin|ina|ose|len|rin";

	private String prefix;
	private int suffixLength;
	private HashSet<String> suffixes;

	public SuffixMatchWithDictionary(String prefix, int suffixLength, HashSet<String> suffixes) {
		this.prefix = prefix;
		this.suffixLength = suffixLength;
		this.suffixes = suffixes;
		if (suffixes == null) loadDefaultHashSet();
	}

	public SuffixMatchWithDictionary() {
		this("SUFMATCH3=", 3, new HashSet<String>());
		loadDefaultHashSet();
	}

	public Instance pipe(Instance carrier) {
		TokenSequence ts = (TokenSequence) carrier.getData();
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			String s = t.getText();
			int slen = s.length();
			String tokenSuffix = null;
			if (slen > suffixLength) {
				tokenSuffix = s.substring(slen - suffixLength, slen);
				t.setFeatureValue(prefix + (isSuffixPresent(tokenSuffix)?1:0), 1.0);
				// t.setFeatureValue((prefix + s.substring(slen - suffixLength,
				// slen)), 1.0);
			}
		}
		return carrier;
	}

	public boolean isSuffixPresent(String str) {
		
		return (suffixes.contains(str));
	}

	public void loadDefaultHashSet(){
		HashSet<String> suffixes = new HashSet<String>();
		String[] split = default_suffix_list.split("|");
		for (int i = 0; i< split.length; i++){
			if(split[i] != "") suffixes.add(split[i].trim());
		}
		this.suffixes = suffixes;
	}
	
	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
		out.writeObject(prefix);
		out.writeInt(suffixLength);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
		prefix = (String) in.readObject();
		suffixLength = in.readInt();
	}
}
