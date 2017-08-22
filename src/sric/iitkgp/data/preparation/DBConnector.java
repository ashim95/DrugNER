package sric.iitkgp.data.preparation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	public static Connection connect(String server, String database,
			String user, String password) {
		return DBConnector.connectToMySQL(server, database, user, password);
}

	private static Connection connectToMySQL(String server, String database, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e1){
			throw new RuntimeException("Cannot find JDBC driver. Make sure the file mysql-connector-java-x.x.xx-bin.jar is in the path");
		}
		
		String url = "jdbc:mysql://" + server + ":3306/" + database;
		
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e1) {
			throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
		}	
	}
}
