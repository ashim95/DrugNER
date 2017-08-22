package sric.iitkgp.data.preparation;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NameDBUtils {
	
	private static final String PROPERTIES_FILE = "resources/application.properties";
	
	private static final Long DEFAULT_PREVIOUS_ID = (long) 0;
	private static final Integer DEFAULT_FETCH_SIZE = 100;
	private static final String DATABASE_DRUG_NAMES = "drugs";
	private Long previousId;
	private Integer fetchSize;
	private Integer currentPageNo;
	private Connection conn;
	private String tableName;
	private Integer totalPages;

	// public List<DrugName> fetchAllNames() {
	//
	// Connection conn = MySqlConnect.makeConnection(DATABASE_DRUG_NAMES);
	//
	// String table = "names";
	//
	// Long count = this.countRecords(conn, table);
	//
	// System.out.println("Count: " + count.toString());
	//
	// int pages = (int) (count / fetchSize);
	// pages = (count % fetchSize == 0) ? pages : pages + 1;
	//
	// long total = 0;
	//
	// long startTime = System.nanoTime();
	//
	//// pages = 2;
	// System.out.println("Total Pages: " + pages);
	// List<DrugName> drugNameList = new ArrayList<DrugName>();
	//
	// for (int i = 0; i < pages; i++) {
	// List<DrugName> drugtList = this.fetchNextBatch(conn, table);
	// if (i % 500 == 0)
	// System.out.println("Fetching for Page: " + i);
	// total += drugtList.size();
	// drugNameList.addAll(drugtList);
	// }
	// long endTime = System.nanoTime();
	// long duration = (endTime - startTime) / 1000000;
	//
	// System.out.println("Abstracts Fetched : " + total);
	// System.out.println("Total time taken for fetching all abstracts: " +
	// duration);
	// return drugNameList;
	// }

	public void getReady() {
		Connection conn = MySqlConnect.makeConnection(DATABASE_DRUG_NAMES);
		this.conn = conn;
		String table = "names";
		this.tableName = table;

		Long count = this.countRecords(conn, table);
//		count = (long) 10;
		System.out.println("Total Entries present in names: " + count.toString());
		int pages = (int) (count / this.fetchSize);
		pages = (count % this.fetchSize == 0) ? pages : pages + 1;
		this.totalPages = pages;
		System.out.println("Total Pages for Names: " + this.totalPages);
		return;
	}

	public List<DrugName> fetchNextBatch() {
		
		if (this.currentPageNo == this.totalPages -1){
			return null;
		}
		
		try {
			Statement stmt = this.conn.createStatement();

			String query = "select ID, RXCUI, RXAUI, STR from " + this.tableName + " where ID > "
					+ this.previousId.toString() + " order by ID limit " + this.fetchSize.toString() + ";";
			ResultSet rs = stmt.executeQuery(query);

			List<DrugName> drugNameList = new ArrayList<DrugName>();

			while (rs.next()) {
				DrugName drugName = new DrugName();

				drugName.setId(rs.getInt("ID"));
				drugName.setRxcui(rs.getString("RXCUI"));
				drugName.setRxaui(rs.getString("RXAUI"));
				drugName.setName(rs.getString("STR"));
				drugNameList.add(drugName);

				this.previousId = (long) drugName.getId();

			}
			this.currentPageNo += 1;
			return drugNameList;

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Long countRecords(Connection conn, String table) {
		Long count = (long) -1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT count(*) FROM " + table);
			if (rs.next()) {
				// System.out.println("No. of entries: " + rs.getObject(1));
				count = (long) rs.getInt(1);
			}
			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void load_properties() {
		// Read properties and connect to database
		Properties properties = new Properties();
		
		try{
			properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		String names_fetch_size = properties.getProperty("names_fetch_size");
		this.fetchSize = Integer.parseInt(names_fetch_size);
		
		String names_previous_id = properties.getProperty("names_previous_id");
		this.previousId = (long) Integer.parseInt(names_previous_id);
	}
	
	public NameDBUtils() {
		this.previousId = DEFAULT_PREVIOUS_ID;
		this.fetchSize = DEFAULT_FETCH_SIZE;
		this.currentPageNo = -1;
		load_properties();
	}

	public NameDBUtils(Integer fetchSize) {
		this.previousId = DEFAULT_PREVIOUS_ID;
		this.fetchSize = fetchSize;
		this.currentPageNo = -1;
	}
}
