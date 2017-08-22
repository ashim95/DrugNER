package sric.iitkgp.data.preparation;

/**
 * DAO to map all details of a drug from "names" table in database. 
 * DrugName Class is for getting only some details
 * @author ashim
 *
 */

public class DrugNameDao {
	private Integer id;
	private String rxcui;
	private String lat;
	private String ts;
	private String lui      ;
	private String stt      ;
	private String sui      ;
	private String isPref   ;
	private String rxaui    ;
	private String saui     ;
	private String scui     ;
	private String sdui     ;
	private String sab      ;
	private String tty      ;
	private String code     ;
	private String str      ;
	private String srl      ;
	private String suppress ;
	private String cvf      ;
	
	
	public DrugNameDao(DrugNameDao drugNameDao) {
		this.id = drugNameDao.getId();
		this.rxcui = drugNameDao.getRxcui();
		this.lat = drugNameDao.getLat();
		this.ts = drugNameDao.getTs();
		this.lui = drugNameDao.getLui();
		this.stt = drugNameDao.getStt();
		this.sui = drugNameDao.getSui();
		this.isPref = drugNameDao.getIsPref();
		this.rxaui = drugNameDao.getRxaui();
		this.saui = drugNameDao.getSaui();
		this.scui = drugNameDao.getScui();
		this.sdui = drugNameDao.getSdui();
		this.sab = drugNameDao.getSab();
		this.tty = drugNameDao.getTty();
		this.code = drugNameDao.getCode();
		this.str = drugNameDao.getStr();
		this.srl = drugNameDao.getSrl();
		this.suppress = drugNameDao.getSuppress();
		this.cvf = drugNameDao.getCvf();
	}
	
	
	public DrugNameDao() {
	}

	public DrugNameDao(Integer id, String rxcui, String lat, String ts, String lui, String stt, String sui,
			String isPref, String rxaui, String saui, String scui, String sdui, String sab, String tty, String code,
			String str, String srl, String suppress, String cvf) {
		this.id = id;
		this.rxcui = rxcui;
		this.lat = lat;
		this.ts = ts;
		this.lui = lui;
		this.stt = stt;
		this.sui = sui;
		this.isPref = isPref;
		this.rxaui = rxaui;
		this.saui = saui;
		this.scui = scui;
		this.sdui = sdui;
		this.sab = sab;
		this.tty = tty;
		this.code = code;
		this.str = str;
		this.srl = srl;
		this.suppress = suppress;
		this.cvf = cvf;
	}

	public DrugNameDao(Integer id, String rxcui, String rxaui, String str) {
		this.id = id;
		this.rxcui = rxcui;
		this.rxaui = rxaui;
		this.str = str;
	}

	public Integer getId() {
		return id;
	}

	public String getRxcui() {
		return rxcui;
	}

	public String getLat() {
		return lat;
	}

	public String getTs() {
		return ts;
	}

	public String getLui() {
		return lui;
	}

	public String getStt() {
		return stt;
	}

	public String getSui() {
		return sui;
	}

	public String getIsPref() {
		return isPref;
	}

	public String getRxaui() {
		return rxaui;
	}

	public String getSaui() {
		return saui;
	}

	public String getScui() {
		return scui;
	}

	public String getSdui() {
		return sdui;
	}

	public String getSab() {
		return sab;
	}

	public String getTty() {
		return tty;
	}

	public String getCode() {
		return code;
	}

	public String getStr() {
		return str;
	}

	public String getSrl() {
		return srl;
	}

	public String getSuppress() {
		return suppress;
	}

	public String getCvf() {
		return cvf;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setRxcui(String rxcui) {
		this.rxcui = rxcui;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public void setLui(String lui) {
		this.lui = lui;
	}

	public void setStt(String stt) {
		this.stt = stt;
	}

	public void setSui(String sui) {
		this.sui = sui;
	}

	public void setIsPref(String isPref) {
		this.isPref = isPref;
	}

	public void setRxaui(String rxaui) {
		this.rxaui = rxaui;
	}

	public void setSaui(String saui) {
		this.saui = saui;
	}

	public void setScui(String scui) {
		this.scui = scui;
	}

	public void setSdui(String sdui) {
		this.sdui = sdui;
	}

	public void setSab(String sab) {
		this.sab = sab;
	}

	public void setTty(String tty) {
		this.tty = tty;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public void setSrl(String srl) {
		this.srl = srl;
	}

	public void setSuppress(String suppress) {
		this.suppress = suppress;
	}

	public void setCvf(String cvf) {
		this.cvf = cvf;
	}


}
