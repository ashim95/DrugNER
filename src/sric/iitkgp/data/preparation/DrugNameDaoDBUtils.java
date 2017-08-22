package sric.iitkgp.data.preparation;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DrugNameDaoDBUtils {
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
	private String insertTableName;

	public void getReady() {
		Connection conn = MySqlConnect.makeConnection(DATABASE_DRUG_NAMES);
		this.conn = conn;
		String table = this.tableName;
		
//		System.out.println(table);
		
		Long count = this.countRecords(conn, table);
		// count = (long) 10;
		System.out.println("Total Entries present in names: " + count.toString());
		int pages = (int) (count / this.fetchSize);
		pages = (count % this.fetchSize == 0) ? pages : pages + 1;
		this.totalPages = pages;
		System.out.println("Total Pages for Names: " + this.totalPages);
		return;
	}

	public void persistDrugNameList(List<DrugNameDao> daoList) throws SQLException {
		long startTime = System.nanoTime();
		for (DrugNameDao dao : daoList) {
			PreparedStatement preparedStatement = this.prepareInsertStatement(conn, dao);
			if (preparedStatement == null) {
				// System.out.println("Did not Insert Drug Name Object: " + i);
				continue;
			}
			preparedStatement.executeUpdate();
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Total time taken to insert records : " + duration + " seconds");

		return;
	}

	public PreparedStatement prepareInsertStatement(Connection conn, DrugNameDao dao) throws SQLException {
		String qry = "";

		qry = "INSERT INTO "
				+ this.insertTableName + 
				" (RXCUI, LAT, TS, LUI, STT, SUI, ISPREF, "
				+ "RXAUI, SAUI, SCUI, SDUI, SAB, TTY, CODE, STR, "
				+ "SRL, SUPPRESS, CVF ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		if (dao.getRxcui() != null && dao.getRxaui() != null && dao.getStr() != null) {
			
			
			PreparedStatement preparedStatement = conn.prepareStatement(qry);
			preparedStatement.setString(1, dao.getRxcui());
			preparedStatement.setString(2, dao.getLat() );
			preparedStatement.setString(3, dao.getTs() );
			preparedStatement.setString(4, dao.getLui() );
			preparedStatement.setString(5, dao.getStt() );
			preparedStatement.setString(6, dao.getSui() );
			preparedStatement.setString(7, dao.getIsPref() );
			preparedStatement.setString(8, dao.getRxaui() );
			preparedStatement.setString(9, dao.getSaui() );
			preparedStatement.setString(10, dao.getScui() );
			preparedStatement.setString(11, dao.getSdui() );
			preparedStatement.setString(12, dao.getSab() );
			preparedStatement.setString(13, dao.getTty() );
			preparedStatement.setString(14, dao.getCode() );
			preparedStatement.setString(15, dao.getStr() );
			preparedStatement.setString(16, dao.getSrl() );
			preparedStatement.setString(17, dao.getSuppress() );
			preparedStatement.setString(18, dao.getCvf() );
			return preparedStatement;
		}

		return null;
	}

	public List<DrugNameDao> fetchNextBatch() {

		if (this.currentPageNo == this.totalPages - 1) {
			return null;
		}

		try {
			Statement stmt = this.conn.createStatement();

			String query = "select ID, RXCUI, LAT, TS, LUI , STT , SUI , ISPREF , RXAUI , SAUI , SCUI , SDUI , SAB , TTY , CODE , STR , SRL , SUPPRESS , CVF  from "
					+ this.tableName + " where ID > " + this.previousId.toString() + " order by ID limit "
					+ this.fetchSize.toString() + ";";
			ResultSet rs = stmt.executeQuery(query);

			List<DrugNameDao> drugNameDaoList = new ArrayList<DrugNameDao>();

			while (rs.next()) {
				DrugNameDao drugNameDao = new DrugNameDao();

				drugNameDao.setId(rs.getInt("ID"));
				drugNameDao.setRxcui(rs.getString("RXCUI"));
				drugNameDao.setLat(rs.getString("LAT"));
				drugNameDao.setTs(rs.getString("TS"));
				drugNameDao.setLui(rs.getString("LUI"));
				drugNameDao.setStt(rs.getString("STT"));
				drugNameDao.setSui(rs.getString("SUI"));
				drugNameDao.setIsPref(rs.getString("ISPREF"));
				drugNameDao.setRxaui(rs.getString("RXAUI"));
				drugNameDao.setSaui(rs.getString("SAUI"));
				drugNameDao.setScui(rs.getString("SCUI"));
				drugNameDao.setSdui(rs.getString("SDUI"));
				drugNameDao.setSab(rs.getString("SAB"));
				drugNameDao.setTty(rs.getString("TTY"));
				drugNameDao.setCode(rs.getString("CODE"));
				drugNameDao.setStr(rs.getString("STR"));
				drugNameDao.setSrl(rs.getString("SRL"));
				drugNameDao.setSuppress(rs.getString("SUPPRESS"));
				drugNameDao.setCvf(rs.getString("CVF"));

				drugNameDaoList.add(drugNameDao);

				this.previousId = (long) drugNameDao.getId();

			}
			this.currentPageNo += 1;
			return drugNameDaoList;

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

		try {
			properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String names_fetch_size = properties.getProperty("names_fetch_size");
		this.fetchSize = Integer.parseInt(names_fetch_size);

		String names_previous_id = properties.getProperty("names_previous_id");
		this.previousId = (long) Integer.parseInt(names_previous_id);

		String insertTableName = properties.getProperty("drug_names_insert_table_name");
		this.insertTableName = insertTableName;
		
		String tableName = properties.getProperty("drug_names_table_name");
		this.tableName = tableName;
	}

	public DrugNameDaoDBUtils() {
		this.previousId = DEFAULT_PREVIOUS_ID;
		this.fetchSize = DEFAULT_FETCH_SIZE;
		this.currentPageNo = -1;
		load_properties();
	}

	public DrugNameDaoDBUtils(Integer fetchSize) {
		this.previousId = DEFAULT_PREVIOUS_ID;
		load_properties();
		this.fetchSize = fetchSize;
		this.currentPageNo = -1;
	}
}
