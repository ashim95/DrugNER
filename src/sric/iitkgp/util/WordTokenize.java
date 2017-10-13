package sric.iitkgp.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import banner.eval.dataset.Dataset;
import banner.tagging.TagFormat;
import banner.tokenization.Tokenizer;
import banner.types.Sentence;
import banner.types.Token;
import banner.types.Mention.MentionType;
import banner.types.Sentence.OverlapOption;

public class WordTokenize {

	public static void main(String[] args) throws IOException {

		String configFileName = "config/word_tokenizer_config.xml";

		HierarchicalConfiguration config;

		try {
			config = new XMLConfiguration(configFileName);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}

		Dataset dataset = getDataset(config);
		System.out.println(dataset.getSentences().size());
		
		List<Sentence> sentences = new ArrayList<Sentence>();
		sentences.addAll(dataset.getSentences());
		
		writeToFileInCONLLFormat(config, sentences);
		
	}

	public static Dataset getDataset(HierarchicalConfiguration config) {
		String datasetName = config.getString("datasetName");
		Tokenizer tokenizer = getTokenizer(config);
		Dataset dataset = null;
		try {
			dataset = (Dataset) Class.forName(datasetName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		dataset.setTokenizer(tokenizer);
		dataset.load(config);
		return dataset;
	}

	public static Tokenizer getTokenizer(HierarchicalConfiguration config) {
		try {
			String tokenizerName = config.getString("tokenizer");
			Tokenizer tokenizer = (Tokenizer) Class.forName(tokenizerName).newInstance();
			return tokenizer;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void writeToFileInCONLLFormat(HierarchicalConfiguration config, List<Sentence> sentences)
			throws IOException {

		TagFormat format = getTagFormat(config);
		Set<MentionType> mentionTypes = getMentionTypes(config);
		OverlapOption sameType = getSameTypeOverlapOption(config);
		OverlapOption differentType = getDifferentTypeOverlapOption(config);

		System.out.println("Writing in CONLL Format ...");

		String filename = config.getString("conllFileName");
		BufferedWriter out = new BufferedWriter(new FileWriter(filename));

		for (Sentence sentence : sentences) {
			List<Token> tokens = sentence.getTokens();
			int size = tokens.size();
			List<String> labels = sentence.getTokenLabels(format, mentionTypes, sameType, differentType);

			for (int i = 0; i < size; i++) {
				String text = tokens.get(i).getText();
				String label = labels.get(i);
				out.write(text + "\t" + label + "\n");
			}

			out.write("\n");
		}

		out.close();
		System.out.println("Finished Writing in CONLL Format. ");
	}

	public static Set<MentionType> getMentionTypes(HierarchicalConfiguration config) {
		String mentionTypesStr = config.getString("mentionTypes");
		if (mentionTypesStr == null)
			throw new RuntimeException("Configuration must contain parameter \"mentionTypes\"");
		Set<MentionType> mentionTypes = new HashSet<MentionType>();
		for (String mentionTypeName : mentionTypesStr.split("\\s+"))
			mentionTypes.add(MentionType.valueOf(mentionTypeName));
		return EnumSet.copyOf(mentionTypes);
	}

	public static TagFormat getTagFormat(HierarchicalConfiguration config) {
		return TagFormat.valueOf(config.getString("tagFormat"));
	}

	public static OverlapOption getSameTypeOverlapOption(HierarchicalConfiguration config) {
		String sameTypeOverlapOption = config.getString("sameTypeOverlapOption");
		if (sameTypeOverlapOption == null)
			throw new RuntimeException("Configuration must contain parameter \"sameTypeOverlapOption\"");
		return OverlapOption.valueOf(sameTypeOverlapOption);
	}

	public static OverlapOption getDifferentTypeOverlapOption(HierarchicalConfiguration config) {
		String differentTypeOverlapOption = config.getString("differentTypeOverlapOption");
		if (differentTypeOverlapOption == null)
			throw new RuntimeException("Configuration must contain parameter \"differentTypeOverlapOption\"");
		return OverlapOption.valueOf(differentTypeOverlapOption);
	}
}
