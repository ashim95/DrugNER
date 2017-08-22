package sric.iitkgp.data.preparation;

import java.io.Serializable;

public class DrugName implements Serializable{
	public Integer id;
	public String rxcui;
	public String rxaui;
	public String name;
	
	public DrugName() {
	}

	public DrugName(Integer id, String rxcui, String rxaui, String name) {
		this.id = id;
		this.rxcui = rxcui;
		this.rxaui = rxaui;
		this.name = name;
	}
	
	public DrugName(String name){
		this.name = name;
		this.id = -1;
		this.rxaui="";
		this.rxcui="";
	}

	public static DrugName combineDrugName(DrugName part1, DrugName part2){
		if(part1 == null && part2 == null) return null;
		if(part1 == null && part2 != null) return part2;
		if(part1 != null && part2 == null) return part1;
		return (new DrugName(part1.getId(), part1.getRxcui(), part1.getRxcui(), part1.getName() + part2.getName()));
	}
	
	@Override
    public int hashCode() {
		return this.name.hashCode();
		
	}
	
	public boolean equals(Object object){
		if(object instanceof DrugName){
			DrugName drugName = (DrugName) object;
			return (this.name.equals(drugName.getName()));
		}
		return false;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRxcui() {
		return rxcui;
	}

	public void setRxcui(String rxcui) {
		this.rxcui = rxcui;
	}

	public String getRxaui() {
		return rxaui;
	}

	public void setRxaui(String rxaui) {
		this.rxaui = rxaui;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
