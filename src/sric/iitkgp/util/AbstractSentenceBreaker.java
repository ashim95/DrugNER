package sric.iitkgp.util;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import banner.util.SentenceBreaker;
//import banner.util.SentenceBreaker;
import sric.iitkgp.data.preparation.RawAbstract;

public class AbstractSentenceBreaker {

	private String text;
	private List<AbstractSentence> abstractSentences;
	private List<String> sentences;
	
	public AbstractSentenceBreaker() {
		super();
	}
	
	public void setText(RawAbstract abst) {
		String text = abst.getAbstractText();
//		setText(text);
		sentences = new ArrayList<String>();
		abstractSentences = new ArrayList<AbstractSentence>();
		BreakIterator bi = BreakIterator.getSentenceInstance(Locale.US);
		bi.setText(text);
		int index = 0;
		int depth = 0;
		int i = 0;
		while (bi.next() != BreakIterator.DONE) {
			String sentence = text.substring(index, bi.current());
			if (depth > 0) {
				depth += getParenDepth(sentence);
				int last = sentences.size() - 1;
				sentence = sentences.get(last) + sentence;
				sentences.set(last, sentence);
				
				AbstractSentence lastSent = abstractSentences.get(last);
				AbstractSentence abstSentence = new AbstractSentence(lastSent.getSentenceId(), 
																	lastSent.getDocumentId(), 
																	sentence);
				abstSentence.setStartOffset(lastSent.getStartOffset());
				abstSentence.setEndOffset(bi.current());
				abstractSentences.set(last, abstSentence);
			} else {
				i++;
				String sentenceId = abst.getPmid().toString() + "_" + i;
				AbstractSentence abstSentence = new AbstractSentence(sentenceId, abst.getPmid().toString(), sentence);
				abstSentence.setStartOffset(index);
				abstSentence.setEndOffset(bi.current());
				depth += getParenDepth(sentence);
				abstractSentences.add(abstSentence);
				sentences.add(sentence);
			}
			index = bi.current();
		}
	}
	
	private int getParenDepth(String text) {
		int depth = 0;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '(')
				depth++;
			if (text.charAt(i) == ')')
				depth--;
		}
		return depth;
	}
	
	
	public List<AbstractSentence> getAbstractSentences() {
		return abstractSentences;
//		return Collections.unmodifiableList(abstractSentences);
	}
	
	public List<String> getSentences(){
		return sentences;
	}
	
	// For Testing
	public static void main(String[] args) {
		AbstractSentenceBreaker sb = new AbstractSentenceBreaker();
		sb.setText(new RawAbstract(12, 1, "This is short. Testing (A. B. C. E.) also. And another.", "", 1, ""));
		for (String sentence : sb.getSentences())
			System.out.println(sentence);
	}
}
