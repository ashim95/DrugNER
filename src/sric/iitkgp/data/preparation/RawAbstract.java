package sric.iitkgp.data.preparation;

public class RawAbstract {
	private Integer pmid;
	
	private Integer pmidVersion;
	
	private String abstractText;
	
	private String label;
	
	private Integer textOrder;
	
	private String nlmCategory;

	public Integer getPmid() {
		return pmid;
	}

	public void setPmid(Integer pmid) {
		this.pmid = pmid;
	}

	public Integer getPmidVersion() {
		return pmidVersion;
	}

	public void setPmidVersion(Integer pmidVersion) {
		this.pmidVersion = pmidVersion;
	}

	public String getAbstractText() {
		return abstractText;
	}

	public void setAbstractText(String abstractText) {
		this.abstractText = abstractText;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getTextOrder() {
		return textOrder;
	}

	public void setTextOrder(Integer textOrder) {
		this.textOrder = textOrder;
	}

	public String getNlmCategory() {
		return nlmCategory;
	}

	public void setNlmCategory(String nlmCategory) {
		this.nlmCategory = nlmCategory;
	}

	public RawAbstract() {
		super();
	}

	public RawAbstract(Integer pmid, Integer pmidVersion, String abstractText, String label, Integer textOrder,
			String nlmCategory) {
		this.pmid = pmid;
		this.pmidVersion = pmidVersion;
		this.abstractText = abstractText;
		this.label = label;
		this.textOrder = textOrder;
		this.nlmCategory = nlmCategory;
	}

//	public RawAbstract(String pmid, String pmidVersion, String abstractText, String label, String textOrder,
//			String nlmCategory) {
//		this.pmid = Integer.parseInt(pmid);
//		this.pmidVersion = Integer.parseInt(pmidVersion);
//		this.abstractText = abstractText;
//		this.label = label;
//		this.textOrder = Integer.parseInt(textOrder);;
//		this.nlmCategory = nlmCategory;
//	}
	
	
}
