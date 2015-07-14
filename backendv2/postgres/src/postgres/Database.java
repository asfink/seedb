package postgres;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Database {
	public static void main(String[] var){
		Database test = new Database();
		//test.connect();
		Scanner sc = new Scanner(System.in);
		System.out.println("MetaData or Query");
		String selection = sc.next();
		if (selection.equalsIgnoreCase("query")){
			System.out.println("Enter Query");
			String query = sc.next();
			try {
				test.launchQuery(query);
			} catch (SQLException e) {
				System.out.println("Query Failed");
				e.printStackTrace();
			}
		} else {
			try {
				test.metadataFetch();
			} catch (SQLException e) {
				System.out.println("Failed to get metadata");
				e.printStackTrace();
			}
		}


	}
	private Connection connection = null; 
	/*
	 * Initializes the PostgreSQL database into the Java VM.
	 * Checks for driver and establishes connection to Postgresql server/db
	 * 
	 * http://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
	 * 
	 * Ali Finkelstein
	 * 9 July 2015
	 */
	public Database(){
		//Testing for presence of PostgreSQL Driver
		System.out.println("Testing PostgreSQL driver");
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		
		System.out.println("PostgreSQL Driver Found");
		
		//creating connection to PostgreSQL database		
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/booktown","postgres","postgres");
		} catch (SQLException e){
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		
		//verify that a connection was made
		if (connection != null) {
			System.out.println("Connection Successful");
		} else{
			System.out.println("Attempt to make connection Failed.");
		}	
	}
	
	/*
	 * Queries the Database
	 * 
	 * https://jdbc.postgresql.org/documentation/head/query.html
	 * 
	 * Ali Finkelstein 
	 * 10 July 2015
	 */
	
	public void launchQuery(String query) throws SQLException{
		Statement queryStatement = connection.createStatement();
		ResultSet queryResult = queryStatement.executeQuery(query);
		while(queryResult.next()){
			System.out.println(queryResult.getString(1));
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
		DatabaseMetaData baseMetaData = connection.getMetaData();
		
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
	
//	public void connect(){
//		//Testing for presence of PostgreSQL Driver
//		System.out.println("Testing PostgreSQL driver");
//		
//		try {
//			Class.forName("org.postgresql.Driver");
//		} catch(ClassNotFoundException e){
//			System.out.println("Driver not found");
//			e.printStackTrace();
//			return;
//		}
//		
//		System.out.println("PostgreSQL Driver Found");
//		
//		//creating connection to PostgreSQL database
//		Connection connection = null; 
//		
//		try{
//			connection = DriverManager.getConnection("jdbc:postgresql://localhost/test","postgres","postgres");
//		} catch (SQLException e){
//			System.out.println("Connection Failed.");
//			e.printStackTrace();
//			return;
//		}
//		
//		//verify that a connection was made
//		if (connection != null) {
//			System.out.println("Connection Successful");
//		} else{
//			System.out.println("Attempt to make connection Failed.");
//		}	
//	}
	
}
