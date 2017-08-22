package sric.iitkgp.data.preparation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DrugMatchDaoDBUtils {
	private static final String DATABASE_DRUG_NAMES = "drugs";
	private static final String TABLE_DRUG_NAMES_ANNOTATIONS = "drug_names";

	public void persistDrugNameList(List<DrugMatchDao> drugMatchList) throws SQLException {
		Connection conn = MySqlConnect.makeConnection(DATABASE_DRUG_NAMES);
		long startTime = System.nanoTime();

		String table = "names";

		Long count = this.countRecords(conn, table);

//		System.out.println("Count: " + count.toString());

		for (DrugMatchDao drugMatch : drugMatchList) {
			PreparedStatement preparedStatement = this.prepareInsertStatement(conn, drugMatch);
			if (preparedStatement == null) {
				// System.out.println("Did not Insert Drug Object: " + i);
				continue;
			}
			preparedStatement.executeUpdate();
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000000;
		System.out.println("Total time taken to insert records : " + duration + " seconds");

		return;
	}

	public PreparedStatement prepareInsertStatement(Connection conn, DrugMatchDao drugMatch) throws SQLException {
		String qry = "";

		qry = "INSERT INTO "
				+ TABLE_DRUG_NAMES_ANNOTATIONS + 
				" (rxcui, rxaui, name, original_text, pmid, start, end) VALUES (?, ?, ?, ?, ?, ?, ?)";

		if (drugMatch.getName() != null && drugMatch.getPmid() != null && drugMatch.getStart() != null
				&& drugMatch.getEnd() != null) {
			PreparedStatement preparedStatement = conn.prepareStatement(qry);
			preparedStatement.setString(1, drugMatch.getRxcui());
			preparedStatement.setString(2, drugMatch.getRxaui());
			preparedStatement.setString(3, drugMatch.getName());
			preparedStatement.setString(4, drugMatch.getOriginalText());
			preparedStatement.setInt(5, drugMatch.getPmid());
			preparedStatement.setInt(6, drugMatch.getStart());
			preparedStatement.setInt(7, drugMatch.getEnd());
			return preparedStatement;
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
}
