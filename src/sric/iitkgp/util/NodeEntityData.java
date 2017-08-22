package sric.iitkgp.util;

import java.io.Serializable;

import banner.types.EntityType;
import sric.iitkgp.data.preparation.DrugName;

public class NodeEntityData implements Serializable{
	
	private DrugName drugName;
	private EntityType entityType;
	public DrugName getDrugName() {
		return drugName;
	}
	public EntityType getEntityType() {
		return entityType;
	}
	public void setDrugName(DrugName drugName) {
		this.drugName = drugName;
	}
	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}
	public NodeEntityData(DrugName drugName, EntityType entityType) {
		this.drugName = drugName;
		this.entityType = entityType;
	}
	
	
}
