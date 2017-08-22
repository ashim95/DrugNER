package sric.iitkgp.data.preparation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * This Class is used to process and filter Drug Names from "names" table in the
 * database. </br>
 * Uses {@link DrugNameDao} Class.
 * 
 * 
 * @author ashim
 *
 */

public class FilterDrugNames {

	private static final String PROPERTIES_FILE = "resources/application.properties";
	private static final String DEFAULT_WORDS_FILE = "resources/words_1w.txt";
	private static final Integer DEFAULT_NO_OF_WORDS_TO_LOAD = 30000;

	private String wordsFile;
	private Integer noOfWordsToLoad;
	private HashSet<String> wordSet;

	public static void main(String[] args) throws SQLException {

		// testImplementationForRegexProcessor();

		FilterDrugNames helper = new FilterDrugNames();
		
		helper.runFilter();
		
//		helper.testImplementationForWordRemoval();

	}

	public void testImplementationForWordRemoval() {

		DrugNameDao dao = new DrugNameDao(1, "123", "223",
				"inv-metformin or placebo / ledipasvir/sofosbuvir (csp #2002 va impact)");

		List<DrugNameDao> daoList = new ArrayList<DrugNameDao>();
		daoList.add(dao);

		dao = new DrugNameDao(2, "124", "224", "the");
		daoList.add(dao);
		List<DrugNameDao> filteredDaoList = null;

		filteredDaoList = this.removeCommonWords(daoList);

		for (DrugNameDao newDao : filteredDaoList) {
			System.out.println(newDao.getStr() + "  \t" + newDao.getRxaui());
		}
	}

	public void testImplementationForRegexProcessor() {

		DrugNameDao dao = new DrugNameDao(1, "123", "223",
				"inv-metformin or placebo / ledipasvir/sofosbuvir (csp #2002 va impact)");
		List<DrugNameDao> daoList = new ArrayList<DrugNameDao>();
		daoList.add(dao);
		List<DrugNameDao> filteredDaoList = null;

		List<String> regexList = new ArrayList<String>();
		regexList.add("/");
		regexList.add("\\bor\\b");

		for (String regex : regexList) {
			filteredDaoList = processDaoList(daoList, regex);
			daoList = filteredDaoList;
		}

		for (DrugNameDao newDao : filteredDaoList) {
			System.out.println(newDao.getStr() + "  \t" + newDao.getRxaui());
		}

	}

	public void runFilter() throws SQLException {

		DrugNameDaoDBUtils helper = new DrugNameDaoDBUtils();
		helper.getReady();

		List<DrugNameDao> drugNameDaoList = null;

		boolean allNames = false;

		// while (!allNames && (i < count)) {
		while (!allNames) {

			drugNameDaoList = helper.fetchNextBatch();
			if (drugNameDaoList == null || drugNameDaoList.size() == 0) {
				System.out.println("No New Names found or End reached. \n");
				allNames = true;
				break;
			}

			List<DrugNameDao> filteredDaoList = null;

			List<String> regexList = new ArrayList<String>();
			regexList.add("/");
			regexList.add("\\bor\\b");

			for (String regex : regexList) {
				filteredDaoList = processDaoList(drugNameDaoList, regex);
				drugNameDaoList = filteredDaoList;
			}
			
			filteredDaoList = this.removeCommonWords(drugNameDaoList);
			
			helper.persistDrugNameList(filteredDaoList);
			
			System.out.println("Processing completed for this set !! \n Moving to next ...");

			// for (DrugNameDao newDao : filteredDaoList){
			// System.out.println(newDao.getStr());
			// }

		}
	}

	public List<DrugNameDao> processDaoList(List<DrugNameDao> daoList, String regex) {

		List<DrugNameDao> filteredDaoList = new ArrayList<DrugNameDao>();

		for (DrugNameDao dao : daoList) {
			List<DrugNameDao> newDaoList = processDao(dao, regex);
			if (newDaoList == null)
				continue;
			filteredDaoList.addAll(newDaoList);
		}
		daoList.clear();

		return filteredDaoList;
	}

	public List<DrugNameDao> processDao(DrugNameDao dao, String regex) {
		if (regex == null)
			throw new IllegalArgumentException("Regex String cannot be null");
		if (dao == null)
			return null;
		if (dao.getId() == null || dao.getStr() == null || dao.getRxaui() == null || dao.getRxcui() == null)
			return null;

		dao.setStr(dao.getStr().replaceAll("\\(.*\\)", "").trim());

		String[] str = dao.getStr().split(regex);

		List<DrugNameDao> daoList = new ArrayList<DrugNameDao>();

		if (str.length == 1 || str.length == 0) {
			daoList.add(dao);
			return daoList;
		}

		for (int i = 0; i < str.length; i++) {
			DrugNameDao newDao = new DrugNameDao(dao);
			newDao.setStr(str[i].trim());
			newDao.setRxaui(dao.getRxaui() + "_" + i + 1);
			newDao.setRxcui(dao.getRxcui() + "_" + i + 1);
			daoList.add(newDao);
		}

		return daoList;
	}

	public List<DrugNameDao> removeCommonWords(List<DrugNameDao> daoList) {
//		HashSet<String> wordSet = this.readWordsList();
		
		
		System.out.println("Using " + this.noOfWordsToLoad + " from Common words file");
		List<DrugNameDao> filteredDaoList = new ArrayList<DrugNameDao>();
		
//		System.out.println(wordSet.contains("the"));
		
		for (DrugNameDao dao : daoList) {
			String str = dao.getStr().trim();
			if (str == null)
				continue;
			if (this.wordSet.contains(str.toLowerCase()))
				continue;
			filteredDaoList.add(dao);
		}

		return filteredDaoList;
	}

	public HashSet<String> readWordsList() {
		// List<String> words = new ArrayList<String>();

		HashSet<String> wordSet = new HashSet<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(this.wordsFile));
			int i = 1;
			System.out.println("Reading Common Words list ...");
			String line = br.readLine().trim();
			wordSet.add(line.toLowerCase());

			while (line != null && i < this.noOfWordsToLoad) {
				line = br.readLine().trim();
				if ("".equals(line))
					continue;
				wordSet.add(line.toLowerCase());
				i++;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished Reading " + this.noOfWordsToLoad + " Common Words !!");
		return wordSet;
	}

	public void load_properties() {
		// Read properties and connect to database
		Properties properties = new Properties();

		try {
			properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String noOfWordsToLoad = properties.getProperty("no_of_words_to_load");
		this.noOfWordsToLoad = Integer.parseInt(noOfWordsToLoad);

		String common_words_file = properties.getProperty("common_words_file");
		this.wordsFile = common_words_file;
	}

	public FilterDrugNames() {
		this.noOfWordsToLoad = DEFAULT_NO_OF_WORDS_TO_LOAD;
		this.wordsFile = DEFAULT_WORDS_FILE;
		this.wordSet = new HashSet<String>();
		load_properties();
		
		this.wordSet = this.readWordsList();
	}

	public FilterDrugNames(Integer noOfWordsToLoad) {
		this.noOfWordsToLoad = noOfWordsToLoad;
		this.wordsFile = DEFAULT_WORDS_FILE;
		
		this.wordSet = this.readWordsList();
	}

}
