package sric.iitkgp.data.preparation;

public class DrugMatchDao {
	public String rxcui;
	public String rxaui;
	public String name;
	public String originalText;
	public Integer pmid;
	public Integer start;
	public Integer end;
	
	
	
	public DrugMatchDao(String rxcui, String rxaui, String name, String originalText, Integer pmid, Integer start,
			Integer end) {
		this.rxcui = rxcui;
		this.rxaui = rxaui;
		this.name = name;
		this.originalText = originalText;
		this.pmid = pmid;
		this.start = start;
		this.end = end;
	}

	

	public DrugMatchDao() {
	}



	public String getRxcui() {
		return rxcui;
	}



	public String getRxaui() {
		return rxaui;
	}



	public String getName() {
		return name;
	}



	public String getOriginalText() {
		return originalText;
	}



	public Integer getPmid() {
		return pmid;
	}



	public Integer getStart() {
		return start;
	}



	public Integer getEnd() {
		return end;
	}



	public void setRxcui(String rxcui) {
		this.rxcui = rxcui;
	}



	public void setRxaui(String rxaui) {
		this.rxaui = rxaui;
	}



	public void setName(String name) {
		this.name = name;
	}



	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}



	public void setPmid(Integer pmid) {
		this.pmid = pmid;
	}



	public void setStart(Integer start) {
		this.start = start;
	}



	public void setEnd(Integer end) {
		this.end = end;
	}

}
