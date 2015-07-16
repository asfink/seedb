package postgres;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * http://dev.mysql.com/doc/world-setup/en/
 * http://blog.manoharbhattarai.com.np/2013/04/03/execute-sql-file-from-command-line-in-postgresql/
 * http://stackoverflow.com/questions/3393961/how-to-import-existing-sql-files-in-postgresql-8-4
 * https://wiki.postgresql.org/wiki/Sample_Databases
 */
public class Database {
//	public static void main(String[] var){
//		Database test = new Database();
//		//test.connect();
//		Scanner sc = new Scanner(System.in);
//		System.out.println("MetaData or Query");
//		String selection = sc.next();
//		if (selection.equalsIgnoreCase("query")){
//			System.out.println("Enter Query");
//			String query = sc.next();
//			try {
//				test.launchQuery(query);
//			} catch (SQLException e) {
//				System.out.println("Query Failed");
//				e.printStackTrace();
//			}
//		} else {
//			try {
//				test.metadataFetch();
//			} catch (SQLException e) {
//				System.out.println("Failed to get metadata");
//				e.printStackTrace();
//			}
//		}
//
//
//	}
	private Connection connection = null; 
	private DatabaseMetaData baseMetaData = null;
	private String dbName;
	
	/*
	 * Initializes the PostgreSQL database into the Java VM.
	 * Checks for driver and establishes connection to Postgresql server/db
	 * 
	 * http://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
	 * 
	 * Ali Finkelstein
	 * 9 July 2015
	 */
	public Database(String databaseName, String user, String password){
		//Testing for presence of PostgreSQL Driver
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		
		dbName = databaseName;
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/"+dbName,user,password);
		} catch (SQLException e){
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		
		try {
			baseMetaData = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("Unable to pull metadata.");
			e.printStackTrace();
			return;
		}

	}
	
	/*
	 * Verifies that a connection was made to the database
	 * 
	 * Ali Finkelstein
	 * 15 July 2015
	 */
	public boolean verifyConnection(){
		return connection != null;
	}
	
	/*
	 * Verifies that MetaData object was created
	 * 
	 * Ali Finkelstein
	 * 15 July 2015
	 */	
	public boolean verifyMetaDataObj(){
		return baseMetaData != null;
	}
	
	/*
	 * Queries the Database
	 * 
	 * https://jdbc.postgresql.org/documentation/head/query.html
	 * 
	 * 	//test query: SELECT * FROM authors;

	 * Ali Finkelstein 
	 * 10 July 2015
	 */
	public void query(String query) throws SQLException{
		Statement queryStatement = connection.createStatement();
		ResultSet queryResult = queryStatement.executeQuery(query);
		while(queryResult.next()){
			System.out.println(queryResult.getString(0));
		}
		queryResult.close();
		queryStatement.close();
	}
	
	/*
	 * Gets metadata for database and prints it out. 
	 * 
	 * Ali Finkelstein
	 * 10 July 2015
	 */
	public void metadataFetch() throws SQLException{
		
		//Get Type info for database
		ResultSet typeSet = baseMetaData.getTypeInfo();
		System.out.println("typeSet info ---------------- ");
		while(typeSet.next()){
			System.out.println(typeSet.getString(1));
		}
		
		System.out.println("////////////////////////");
		ResultSet schemaSet = baseMetaData.getSchemas();
		System.out.println("schema set info --------- ");
		while(schemaSet.next()){
			System.out.println(schemaSet.getString(1));
		}	
	}
	
	
	/*
	 * Get total number of columns
	 * 
	 * Ali Finkelstein 
	 * 15 July 2015
	 */
	public int getColumnCount(){
		
		return 0;
	}
	
}