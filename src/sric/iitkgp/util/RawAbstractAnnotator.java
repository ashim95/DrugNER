package sric.iitkgp.util;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import banner.tokenization.Tokenizer;
import banner.types.Mention;
import banner.types.Sentence;
import banner.util.SentenceBreaker;
import sric.iitkgp.data.preparation.DrugMatchDao;
import sric.iitkgp.data.preparation.RawAbstract;

public class RawAbstractAnnotator {
	private SentenceBreaker breaker;

	public RawAbstractAnnotator() {

		breaker = new SentenceBreaker();
	}

	public List<DrugMatchDao> annotateAbstracts(List<RawAbstract> abstractList, CustomDictionaryTagger dictionary,
			Tokenizer tokenizer) {

		Long startTime = System.nanoTime();

		List<DrugMatchDao> matchList = new ArrayList<DrugMatchDao>();
		for (RawAbstract abst : abstractList) {
			List<DrugMatchDao> daoList = annotate(abst, dictionary, tokenizer);
			if (daoList != null && daoList.size() > 0) {
				matchList.addAll(daoList);
			}
		}

		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Abstracts Dictionary Tagging done for this batch !!");
		System.out.println("Time taken for Dictionary Tagging (in s): " + duration);

		return matchList;
	}

	public List<DrugMatchDao> annotate(RawAbstract abst, CustomDictionaryTagger dictionary, Tokenizer tokenizer) {
		AbstractSentenceBreaker abstractSB = new AbstractSentenceBreaker();
		abstractSB.setText(abst);

		List<DrugMatchDao> drugMatchList = new ArrayList<DrugMatchDao>();

		for (AbstractSentence abstSentence : abstractSB.getAbstractSentences()) {
			tokenizer.tokenize((Sentence) abstSentence);
			dictionary.tag((Sentence) abstSentence);
			for (Mention mention : abstSentence.getMentions()) {
				DrugMatchDao drugMatch = new DrugMatchDao();
				drugMatch.setOriginalText(
						abstSentence.getOriginalText().substring(mention.getStartChar(), mention.getEndChar()));
				drugMatch.setPmid(Integer.parseInt(abstSentence.getDocumentId()));
				DrugMatchDao filterMatch = filterAttributes(mention.getConceptId());
				if (filterMatch == null)
					continue;
				drugMatch.setName(filterMatch.getName());
				drugMatch.setRxcui(filterMatch.getRxcui());
				drugMatch.setRxaui(filterMatch.getRxaui());
				drugMatch.setStart(abstSentence.getStartOffset() + mention.getStartChar());
				drugMatch.setEnd(abstSentence.getStartOffset() + mention.getEndChar());
				drugMatchList.add(drugMatch);
				// System.out.println(drugMatch.getName());
				// System.out.println(abst.getAbstractText().substring(drugMatch.getStart(),
				// drugMatch.getEnd()));
			}
		}
		return drugMatchList;
	}

	public static DrugMatchDao filterAttributes(String str) {
		if (str == null || "".equals(str))
			return null;
		String[] strLabels = str.split(";;");
		String name = "";
		String id = "";
		String rxcui = "";
		String rxaui = "";
		if (strLabels.length == 1) {
			String[] ids = (strLabels[0].split(":"));
			if (ids.length != 4)
				return null;
			name = ids[0];
			id = ids[1];
			rxcui = ids[2];
			rxaui = ids[3];
			DrugMatchDao drugMatch = new DrugMatchDao(rxcui, rxaui, name, null, null, null, null);
			return drugMatch;
		}

		return null;

	}

}
