package sric.iitkgp.data.preparation;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


public class MySqlConnect {
	
	private static final String PROPERTIES_FILE = "resources/application.properties";
	
	public static Connection makeConnection(String database) {
	// Read properties and connect to database
	Properties properties = new Properties();
	
	try{
		properties.load(new FileInputStream(new File(PROPERTIES_FILE)));
	}
	catch(Exception e){
		e.printStackTrace();
	}
	String server = properties.getProperty("server");
//	String database = properties.getProperty("database");
	String user = properties.getProperty("user");
	String password = properties.getProperty("password");
	Connection conn = DBConnector.connect(server, database,user, password);
	
	return conn;
	
	}
	
	public static boolean testConnection(Connection conn){
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT count(*) FROM names" );
			if (rs.next()){
//				System.out.println("No. of entries: " + rs.getObject(1));
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
	
