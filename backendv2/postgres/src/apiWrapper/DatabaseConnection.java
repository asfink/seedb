package apiWrapper;

import postgres.Database;
 

public class DatabaseConnection {

	private Database databaseConnection;

	/*
	 * Establishes ability to connect to database
	 * 
	 * 20 July 2015
	 */
	public DatabaseConnection()
	{
		databaseConnection = new Database();
	}
	
	/*
	 * Establishes connection to database
	 * 
	 * 20 July 2015
	 */
	public void connectToDB(String databaseName, String address, String username, String password){
		databaseConnection.connect(databaseName,address,username,password);
	}
	
	public void populateTableInfoForDB(String databaseName){
		
	}
	
}
