package sric.iitkgp.train;

import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;

import banner.eval.dataset.Dataset;
import banner.util.SentenceBreaker;

public class DBAbstractDataset extends Dataset{
	
	private SentenceBreaker sb;
	
	public DBAbstractDataset() {
		sb = new SentenceBreaker();
	}
	
	@Override
	public void load(HierarchicalConfiguration config) {
		HierarchicalConfiguration localConfig = config.configurationAt(this.getClass().getPackage().getName());
	}

	@Override
	public List<Dataset> split(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
