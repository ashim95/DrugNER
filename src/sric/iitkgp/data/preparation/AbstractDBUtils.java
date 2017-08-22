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

public class AbstractDBUtils {
	
	private static final String PROPERTIES_FILE = "resources/application.properties";
	
	private static final Long DEFAULT_PREVIOUS_ID = (long) 0;
	private static final Integer DEFAULT_FETCH_SIZE = 50000;
	private static final String DATABASE_MEDLINE_NAME = "medline";
	private Long previousId;

	private Integer fetchSize;
	private Integer currentPageNo;
	private Connection conn;
	private String tableName;
	private Integer totalPages;

	// public List<RawAbstract> fetchAbstracts() {
	//
	// Connection conn = MySqlConnect.makeConnection(DATABASE_MEDLINE_NAME);
	//
	// String table = "abstract";
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
	// List<RawAbstract> abstractList = new ArrayList<RawAbstract>();
	//
	// for (int i = 0; i < pages; i++) {
	// List<RawAbstract> abstList = this.fetchNextBatch(conn, table);
	// if (i % 500 == 0)
	// System.out.println("Fetching for Page: " + i);
	// total += abstList.size();
	// abstractList.addAll(abstList);
	// }
	// long endTime = System.nanoTime();
	// long duration = (endTime - startTime) / 1000000;
	//
	// System.out.println("Abstracts Fetched : " + total);
	// System.out.println("Total time taken for fetching all abstracts: " +
	// duration);
	// return abstractList;
	// }

	public void getReady() {
		Connection conn = MySqlConnect.makeConnection(DATABASE_MEDLINE_NAME);
		this.conn = conn;
		String table = "abstract";
		this.tableName = table;

		Long count = this.countRecords(conn, table);
//		count = (long)10000;
		System.out.println("Total Entries present: " + count.toString());
		int pages = (int) (count / this.fetchSize);
		pages = (count % fetchSize == 0) ? pages : pages + 1;
		this.totalPages = pages;

		System.out.println("Total Pages for Abstracts: " + this.totalPages);
		return;
	}

	public List<RawAbstract> fetchNextBatch() {

		if (this.currentPageNo == this.totalPages - 1) {
			return null;
		}
		try {
			Statement stmt = this.conn.createStatement();

			String query = "select pmid, pmid_version, medcit_art_abstract_abstracttext_order, "
					+ "value, label, nlmcategory" + "  from " + this.tableName + " where pmid > "
					+ this.previousId.toString() + " order by pmid limit " + this.fetchSize.toString() + ";";
			ResultSet rs = stmt.executeQuery(query);

			List<RawAbstract> abstractList = new ArrayList<RawAbstract>();

			while (rs.next()) {
				RawAbstract rawAbstract = new RawAbstract();

				rawAbstract.setPmid(rs.getInt("pmid"));
				rawAbstract.setPmidVersion(rs.getInt("pmid_version"));
				rawAbstract.setTextOrder(rs.getInt("medcit_art_abstract_abstracttext_order"));
				rawAbstract.setAbstractText(rs.getString("value"));
				rawAbstract.setLabel(rs.getString("label"));
				rawAbstract.setNlmCategory(rs.getString("nlmcategory"));
				abstractList.add(rawAbstract);

				this.previousId = (long) rawAbstract.getPmid();

			}
			this.currentPageNo += 1;
			return abstractList;

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
		
		
		String abstracts_fetch_size = properties.getProperty("abstracts_fetch_size");
		this.fetchSize = Integer.parseInt(abstracts_fetch_size);
		
		String abstracts_previous_id = properties.getProperty("abstracts_previous_id");
		this.previousId = (long) Integer.parseInt(abstracts_previous_id);
	}
	
	public AbstractDBUtils() {
		this.previousId = DEFAULT_PREVIOUS_ID;
		this.fetchSize = DEFAULT_FETCH_SIZE;
		this.currentPageNo = -1;
		load_properties();
	}

	public AbstractDBUtils(Integer fetchSize) {
		this.previousId = DEFAULT_PREVIOUS_ID;
		this.fetchSize = fetchSize;
		this.currentPageNo = -1;
	}
}
