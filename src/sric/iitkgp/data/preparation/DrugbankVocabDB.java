package sric.iitkgp.data.preparation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DrugbankVocabDB {
	
	
	public static void main(String[] args) throws SQLException {
		String filename = "resources/drugbank vocabulary.csv";
		
		List<DrugNameDao> daoList = readFileIntoDaoList(filename);
		
		System.out.println(daoList.size());
		
		List<DrugNameDao> filteredDaoList = removeDuplicates(daoList);
		
//		for(int i=0;i<10;i++) {
//			System.out.println(filteredDaoList.get(i).getId() + "\t" + filteredDaoList.get(i).getRxcui()
//					+"\t" + filteredDaoList.get(i).getStr());
//		}
		
		System.out.println(filteredDaoList.size());
		
		FilterDrugNames helper = new FilterDrugNames();
		
		daoList = helper.removeCommonWords(filteredDaoList);
		System.out.println(daoList.size());
		
		DrugNameDaoDBUtils persistHelper = new DrugNameDaoDBUtils();
		persistHelper.getReady();
		
		persistHelper.persistDrugNameList(daoList);
		
		System.out.println("Program Execution Finished !!");
	
	
	}

	public static List<DrugNameDao> readFileIntoDaoList(String name) {
		
		List<DrugNameDao> daoList = new ArrayList<DrugNameDao>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(name));
			int i = 1;
			String line = br.readLine().trim(); // Since First line contains column names
			line = br.readLine().trim();
			List<DrugNameDao> allDao = getDaoFromLine(line, i);
			
			if (allDao != null) daoList.addAll(allDao);
			i++;
			while (line != null) {
				line = br.readLine().trim();
				if ("".equals(line))
					continue;
				allDao = getDaoFromLine(line, i);
				if (allDao != null) daoList.addAll(allDao);
				i++;
			}
			
			br.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return daoList;
	}
	
	public static List<DrugNameDao> getDaoFromLine (String line, int i){
		List<DrugNameDao> daoList = new ArrayList<DrugNameDao>();
		
		
		String[] values = line.split(",");
		
		String drugbank_id = values[0];
		
		String str = values[2];
		
		String other_ids = values[1];
		
		String other_names = values[5].trim();
		
		other_names = other_names.replace("'", "");
		other_names = other_names.replace("\"", "");
		other_names = other_names.replaceAll("\\(.*\\)", "").trim();
		
//		System.out.println(other_names);
		
		String[] synonyms = other_names.toLowerCase().split("\\|");
		
		DrugNameDao dao = new DrugNameDao(i, drugbank_id.trim(), drugbank_id.trim(), str.trim().toLowerCase());
		dao.setLat("ENG");
		dao.setSab("DRUGBANK_VOCAB");
		dao.setTty("VOCAB");
		dao.setCode(drugbank_id.trim());
		daoList.add(dao);
		
		for(int j=0;j<synonyms.length;j++) {
			String syn = synonyms[j];
			if(syn == null || "".equals(syn.trim()) || "'".equals(syn.trim()) || "\"".equals(syn.trim()) ) continue;
			dao = new DrugNameDao(i, drugbank_id.trim() + "_"+ (j+1) , drugbank_id.trim() + "_"+ (j+1), syn.trim());
			dao.setLat("ENG");
			dao.setSab("DRUGBANK_VOCAB");
			dao.setTty("VOCAB");
			dao.setCode(drugbank_id.trim());
			daoList.add(dao);
		}
		
		return daoList;
	}

	public static List<DrugNameDao> removeDuplicates(List<DrugNameDao> daoList){
		
		List<DrugNameDao> filteredDaoList = new ArrayList<DrugNameDao>();
		
		HashSet<String> wordSet = new HashSet<String>();
		
		boolean isPresent = false;
		
		for(DrugNameDao dao : daoList) {
			
			isPresent= wordSet.add(dao.getStr());
			if (isPresent) filteredDaoList.add(dao);
		}
		
		return filteredDaoList;
		
	}
}
