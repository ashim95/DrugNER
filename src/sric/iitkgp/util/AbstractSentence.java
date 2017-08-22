package sric.iitkgp.util;


import banner.types.Sentence;

public class AbstractSentence extends Sentence{

	private Integer startOffset;
	private Integer endOffset;
	private String originalText;
	
	public AbstractSentence(String sentenceId, String documentId, String text) {
		super(sentenceId, documentId, text.toLowerCase());
		this.setOriginalText(text);
	}
	
	public Integer getStartOffset() {
		return startOffset;
	}

	public Integer getEndOffset() {
		return endOffset;
	}

	public void setStartOffset(Integer startOffset) {
		this.startOffset = startOffset;
	}

	public void setEndOffset(Integer endOffset) {
		this.endOffset = endOffset;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	
}
