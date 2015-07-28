package postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.postgresql.util.PSQLException;

import seeDBExceptions.NoDatabaseConnectionException;
import seeDBExceptions.NoMetaDataFoundException;

/*
 * http://dev.mysql.com/doc/world-setup/en/
 * http://blog.manoharbhattarai.com.np/2013/04/03/execute-sql-file-from-command-line-in-postgresql/
 * http://stackoverflow.com/questions/3393961/how-to-import-existing-sql-files-in-postgresql-8-4
 * https://wiki.postgresql.org/wiki/Sample_Databases
 */
public class Database {

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
		
//		try {
//			baseMetaData = connection.getMetaData();
//		} catch (SQLException e) {
//			System.out.println("Unable to pull metadata.");
//			e.printStackTrace();
//			return;
//		}
	}

	/*
//	public Database(String databaseName, String user, String password, Boolean debug){
//		//Testing for presence of PostgreSQL Driver
//		System.out.println("Testing PostgreSQL driver");
//		try {
//			Class.forName("org.postgresql.Driver");
//			System.out.println("PostgreSQL Driver Found");
//		} catch(ClassNotFoundException e){
//			System.out.println("Driver not found");
//			e.printStackTrace();
//			return;
//		}
//		
//		dbName = databaseName;
//		//creating connection to PostgreSQL database	
//		try{
//			connection = DriverManager.getConnection("jdbc:postgresql://localhost/"+dbName,user,password);
//		} catch (SQLException e){
//			System.out.println("Connection Failed.");
//			e.printStackTrace();
//			return;
//		}
//		
////		try {
////			baseMetaData = connection.getMetaData();
////		} catch (SQLException e) {
////			System.out.println("Unable to pull metadata.");
////			e.printStackTrace();
////			return;
////		}
//	}
	*/
	
	public Database(String databaseName){
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
	}
	
	/*
	 * database constructor without defining the database name
	 */
	public Database(){
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
	}
	
	/*
	 * Connect to DB given that dbName variable is not defined
	 * 
	 * @param databaseName - name of database to connect to 
	 * @param address - address of the database to connect to
	 * @param user - user to connect to db via
	 * @param password - password to user account to connect to database via
	 */
	public void connect(String databaseName, String address, String user, String password){
		dbName = databaseName;
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://"+address+"/"+dbName,user,password);
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
	 * Connect to DB given that dbName variable is defined
	 * 
	 * @param address - address of the database to connect to
	 * @param user - user to connect to db via
	 * @param password - password to user account to connect to database via
	 */
	public void connect(String address, String user, String password){
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://"+address+"/"+dbName,user,password);
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
	 * @param query the string statement or query to be executed.
	 * @return the resultset from the query. Be sure to close the resultset once you are finished with it. 
	 * Ali Finkelstein 
	 * 10 July 2015
	 */
	public ResultSet query(String query) throws SQLException{
		Statement queryStatement = connection.createStatement();
		ResultSet queryResult = queryStatement.executeQuery(query);
		queryResult.close();
		queryStatement.close();
		return queryResult;
	}
	
	
	/*
	 * Because I dont know what a schema is. 
	 * Ali Finkelstein
	 */
	public String getSchema() throws SQLException{
		return connection.getSchema();
	}
	
	/*
	 * Get total number of columns
	 * 
	 * @param table - the table from the database you want to get information from
	 * @Return a hash map with the key being the name of the column and the value being the column type
	 * Ali Finkelstein 
	 * 15 July 2015
	 */
	public Map<String,String> getColumnAttribute(String tableName) throws NoDatabaseConnectionException, SQLException{
		if (connection == null){
			throw new NoDatabaseConnectionException("No database found to connect");
		}
		if (baseMetaData == null){
			baseMetaData = connection.getMetaData();
		}
		ResultSet rs = baseMetaData.getColumns(null,null,tableName,null);
		HashMap<String,String> tableColumnAttrs = new HashMap<String,String>();
		
		while(rs.next()){
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			tableColumnAttrs.put(name,type);
		}
		rs.close();
		return tableColumnAttrs;
	}
	
	/*
	 * Execute a query/statement for the SQL database, and return the resulting data
	 * 
	 * @param statement - SQL query string
	 * @return result set containing query results. be sure to close the resultset once you are completed with it. 
	 * @throws SQLException upon failure of query/statement execution
	 */
	public ResultSet executeStatementWithResult(String statement) throws SQLException{
		Statement stmntToExecute = connection.createStatement();
		ResultSet statementResult = stmntToExecute.executeQuery(statement);
		return statementResult;	
	}
	
	/*
	 * Execute a query/statement for the SQL database
	 * 
	 * @param statement - SQL query string
	 * @throws SQLException upon failure of query/statement execution
	 */
	public void executeStatementNoResult(String statement) throws SQLException{
		PreparedStatement stmntToExecute = connection.prepareStatement(statement);
		stmntToExecute.executeUpdate();
		stmntToExecute.close();
	}
	
	/*
	 * Gets the row count for the given table
	 * @param tableName - name of the table in the database to analyze
	 * @throws SQLException if statement could not be created to allow querying of row count
	 * 
	 * Ali Finkelstein
	 * 16 July 2015
	 */
	public int getRowCount(String tableName) throws SQLException{
		Statement pgQuery = connection.createStatement();
		String queryStatement = "SELECT count(*) FROM "+tableName;
		ResultSet queryResult = pgQuery.executeQuery(queryStatement);
		queryResult.next();
		int result = queryResult.getInt(1);
		queryResult.close();
		pgQuery.close();
		return result;
	}

	/*
	 * From Manasi
	 */
	public Float getVariance(String columnName, String tableName) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT var_pop(" + columnName + ") FROM " + tableName;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	/*
	 * Getting the number of distinct values in columnName from tableName
	 * 
	 * @param tableName - table to look into
	 * @param columnName - name of the column
	 * 
	 * @throws SQLException is no connection can be made to the database
	 * 
	 * @return -1 if no values present
	 * @return int number of distinct values present
	 * Ali Finkelstein
	 * 16 July 2015
	 */
	public int getDistinctValueCount(String tableName, String columnName) throws SQLException{
		Statement pgQuery = connection.createStatement();
		String queryStatement = "SELECT COUNT(DISTINCT "+columnName+") FROM "+tableName;
		ResultSet pgResult = pgQuery.executeQuery(queryStatement);
		int count = -1;
		while(pgResult.next()){
			count = pgResult.getInt(1);
		}
		pgQuery.close();
		pgResult.close();
		return count;
	}
	
	/*
	 * Prints out the result of the result set
	 * 
	 * SRC: https://coderwall.com/p/609ppa/printing-the-result-of-resultset
	 * 
	 * 26 July 2015
	 */
	public void printResultSet(ResultSet rs) throws SQLException{
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNumber = rsmd.getColumnCount();
		System.out.println(Integer.toString(colNumber));
		for (int i=1; i<=colNumber;i++){
			if(i>1 && i<=colNumber) System.out.print(" - ");
			System.out.print(rsmd.getColumnName(i));
		}
		System.out.println("");
		while (rs.next()){
			for(int i = 1; i<=colNumber; i++){
				if(i>1 && i<=colNumber) System.out.print(" - ");
				String val = rs.getString(i);
				System.out.print(val);
			}
			System.out.println("");
		}
	}
	
	/*
	 * Gets name of all tables in the DB
	 * 
	 * @Throws SQLException if unable to connect to DB or get metadata information
	 * 20 July 2015
	 */
	public Set<String> getTables(){
		Set<String> containedTables = new HashSet<String>();
		String[] tableTypesArray = {"VIEW","TABLE","SEQUENCE"};
		ResultSet metadataTables;
		try {
			metadataTables = baseMetaData.getTables(null,null,"%",tableTypesArray);
			while (metadataTables.next()){
				containedTables.add(metadataTables.getString(3));
			}
			metadataTables.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return containedTables;
	}
}