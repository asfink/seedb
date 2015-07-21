package apiWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import postgres.Database;
import postgres.NoDatabaseConnectionException;
 

public class DatabaseConnection {
	private static int DISTINCT_DIMENSION_COUNT=20;
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
	
	/*
	 * Makes a new table in the DB for SeeDB to use for it's analysis.
	 * 
	 * 
	 * @throws SQLException if no database is able to be found or no metadata available.
	 * 20 July 2015
	 */
	public void populateTableInfoForDB(String databaseName){
		Set<String> tablesInDB = null;
		try {
			tablesInDB = databaseConnection.getTables();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		if (tablesInDB==null) return;
		
		/*
		 * CREATE TABLE new_table_name (
		 * 		table_column_title TYPE_OF_DATA column_constraints,
		 * 		next_column_title TYPE_OF_DATA column_constraints,
		 * 		table_constraint
		 * 		table_constraint
		 * ) INHERITS existing_table_to_inherit_from;
		 */
		
		// statement to drop table if it already exists
		@SuppressWarnings("unused")
		String dropTableStatement = "DROP TABLE IF EXISTS seeDBInfo";
		
		//creates table if it doesnt exist already
		String tableCreationStatement = "CREATE TABLE IF NOT EXISTS seeDBInfo ("+
				"tableName TEXT, "+
				"columnName TEXT, "+
				"columnType TEXT, "+
				"seebdType TEXT,"+
				"numDistinctVals NUMERIC "+
				");";
		
		//executing tableCreationStatement on DB
		try {
			databaseConnection.executeStatementNoResult(tableCreationStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		for (String table: tablesInDB){
			try {
				Map<String,String> tableColumnData = databaseConnection.getColumnAttribute(table);
				for (Map.Entry<String,String> columnDataSetEntry: tableColumnData.entrySet()){
					String columnName = columnDataSetEntry.getKey();
					String columnType = columnDataSetEntry.getValue();
					int distinctValues = databaseConnection.getDistinctValueCount(table, columnName);
					String seeDBType = getSeeDBType(columnType,distinctValues);
					
					//build statement for inserting values
					String insertDataStatement = "INSERT INTO seeDBInfo"
							+ "VALUES ("
							+ table + ", "
							+ columnName+", "
							+ columnType+ ","
							+ seeDBType + ", "
							+ distinctValues+");";
					
					databaseConnection.executeStatementNoResult(insertDataStatement);
				}
			} catch (NoDatabaseConnectionException | SQLException e) {
				System.out.println("Unable to access table information");
				e.printStackTrace();
			}
		}
		
	}
	
	/*
	 * Gives a classifier based on the SEEDB column type and number of distinct values
	 * 
	 * @param type - type of the column as assigned by PostgreSQL
	 * @param distinctCount - number of distinct values in a column
	 * 
	 * 21 July 2015
	 */
	private String getSeeDBType(String type, int distinctCount){
		if (distinctCount<=DISTINCT_DIMENSION_COUNT && !type.equalsIgnoreCase("numeric")){
			return "dimension";
		} else if (distinctCount<=DISTINCT_DIMENSION_COUNT && type.equalsIgnoreCase("numeric")){
			return "measure";
		}
		return "other";
	}
	
	/*
	 * Output's the database tables :P
	 */
	public Set<String> getTableInfoForDB(String database){
		try {
			return databaseConnection.getTables();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * 
	 */
	public ResultSet executeQueryWithResult(String query){
		try {
			return databaseConnection.query(query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("query unsuccessful");
			return null;
		}
	}
	
	/*
	 * Queries database for all columns that represent dimensons. 
	 * 
	 * @returns array of strings that are the columns that represent dimensions.
	 * @returns null if query was unsuccessful
	 * 
	 *  21 July 2015
	 */
	public String[] getDimensions(String tableName){
		String queryString = "SELECT columnName FROM seeDBInfo"+
				"WHERE (table ='"+tableName+"') "+
				"AND (seedbType='dimension');";
		ArrayList<String> queryResultArrList = new ArrayList<String>();
		try {
			ResultSet queryResults = databaseConnection.executeStatementWithResult(queryString);
			while(queryResults.next()){
				queryResultArrList.add(queryResults.getString(1));
			}
			return queryResultArrList.toArray(new String[queryResultArrList.size()]);
		} catch (SQLException e) {
			System.out.println("Unable to execute query statement " + queryString);
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Queries database for all columns that represent measurements. 
	 * 
	 * @returns array of strings that are the columns that represent measurements. 
	 * @returns null if query was unsuccessful
	 * 
	 * 21 July 2015
	 */
	public String[] getMeasures(String tableName){
		String queryString = "SELECT columnName FROM seeDBInfo"+
				"WHERE (table ='"+tableName+"') "+
				"AND (seedbType='measure');";
		ArrayList<String> queryResultArrList = new ArrayList<String>();
		try {
			ResultSet queryResults = databaseConnection.executeStatementWithResult(queryString);
			while(queryResults.next()){
				queryResultArrList.add(queryResults.getString(1));
			}
			return queryResultArrList.toArray(new String[queryResultArrList.size()]);
		} catch (SQLException e) {
			System.out.println("Unable to execute query statement " + queryString);
			e.printStackTrace();
		}
		return null;
	}

}
