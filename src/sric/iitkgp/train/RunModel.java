package sric.iitkgp.train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import banner.eval.BANNER;
import banner.eval.BANNER.MatchCriteria;
import banner.eval.BANNER.Performance;
import banner.eval.dataset.Dataset;
import banner.tagging.CRFTagger;
import banner.tokenization.Tokenizer;
import banner.types.EntityType;
import banner.types.Mention;
import banner.types.Sentence;
import dragon.nlp.tool.Tagger;
import dragon.nlp.tool.lemmatiser.EngLemmatiser;

public class RunModel {

	private static Tokenizer tokenizer;
	private static CRFTagger tagger;

	public static void main(String[] args) {
		HierarchicalConfiguration config;

		try {
			config = new XMLConfiguration(args[0]);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}

		long start = System.currentTimeMillis();
		EngLemmatiser lemmatiser = BANNER.getLemmatiser(config);
		Tagger posTagger = BANNER.getPosTagger(config);
		tokenizer = BANNER.getTokenizer(config);

		HierarchicalConfiguration localConfig = config.configurationAt(BANNER.class.getPackage().getName());
		String modelFilename = localConfig.getString("modelFilename");
		System.out.println("modelFilename=" + modelFilename);

		Dataset dataset = BANNER.getDataset(config);
		List<Sentence> sentences = new ArrayList<Sentence>(dataset.getSentences());

		changeType(sentences);

		Collections.sort(sentences, new Comparator<Sentence>() {
			@Override
			public int compare(Sentence s1, Sentence s2) {
				return s1.getSentenceId().compareTo(s2.getSentenceId());
			}
		});

		System.out.println("Completed input: " + (System.currentTimeMillis() - start) + " ms");

		try {
			tagger = CRFTagger.load(new File(modelFilename), lemmatiser, posTagger, null);

			List<Sentence> processedSentences = process(sentences);
			changeType(processedSentences);
			System.out.println("===============");
			System.out.println("Performance with BANNER:");
			System.out.println("===============");
			checkPerformance(sentences, processedSentences);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Execution of the program finished !!");
	}

	private static void changeType(List<Sentence> sentences) {
		for (Sentence s : sentences) {
			for (Mention m : s.getMentions()) {
				m.setEntityType(EntityType.getType("DRUG"));
			}
		}
	}

	private static List<Sentence> process(List<Sentence> sentences) {
		int count = 0;
		List<Sentence> sentences2 = new ArrayList<Sentence>();
		for (Sentence sentence : sentences) {
			if (count % 1000 == 0)
				System.out.println(count);
			Sentence sentence2 = sentence.copy(false, false);
			tokenizer.tokenize(sentence2);
			tagger.tag(sentence2);
			sentences2.add(sentence2);
			count++;
		}
		return sentences2;
	}

	private static void checkPerformance(List<Sentence> annotatedSentences, List<Sentence> processedSentences) {
		Performance performance = new Performance(MatchCriteria.Strict);
		for (int i = 0; i < annotatedSentences.size(); i++) {
			Sentence annotatedSentence = annotatedSentences.get(i);
			Sentence processedSentence = processedSentences.get(i);
			double prec = performance.getOverall().getPrecision();
			performance.update(annotatedSentence, processedSentence);
			double prec2 = performance.getOverall().getPrecision();
//			if (prec2 < prec) {
//				System.out.println(annotatedSentence.getMentions().toString());
//				System.out.println(processedSentence.getMentions().toString());
//			}
		}
		performance.print();
	}
}
