package wrapperInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import postgres.Database;
import seeDBExceptions.NoDatabaseConnectionException;


public class SeeDB_PostgreSQL implements SeeDB_Backend{
	private static int DISTINCT_DIMENSION_COUNT = 20;
	private Map<String, Database> connectionKeeper;
	
	public SeeDB_PostgreSQL(){
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		
		connectionKeeper = new HashMap<String, Database>();
	}
	
	@Override
	public void connectToDB(String databaseName, String address,
			String username, String password) {
		Database connectionDB = new Database();
		Connection connection = null; 
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
		connectionKeeper.put(databaseName, connectionDB);		
	}

	
	@Override
	public void populateTableInfoForDB(String databaseName) {
		Set<String> tablesInDB = null;
		Database db = connectionKeeper.get(databaseName);
		tablesInDB = db.getTables();
		if (tablesInDB == null) return;

		//System.out.println("Table list received");
		/*
		 * CREATE TABLE new_table_name ( table_column_title TYPE_OF_DATA
		 * column_constraints, next_column_title TYPE_OF_DATA
		 * column_constraints, table_constraint table_constraint ) INHERITS
		 * existing_table_to_inherit_from;
		 */

		if (tablesInDB.contains("seedb_schema")){
			// statement to drop table if it already exists
			String dropTableStatement = "DROP TABLE IF EXISTS seeDB_schema";
			try {
				db.executeStatementNoResult(dropTableStatement);
				System.out.println("Excesss tables Dropped");
				tablesInDB = db.getTables();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// creates table if it doesnt exist already
		String tableCreationStatement = "CREATE TABLE IF NOT EXISTS seeDB_schema ("
				+ "tableName TEXT, "
				+ "columnName TEXT, "
				+ "columnType TEXT, "
				+ "seebdType TEXT,"
				+ "numDistinctVals NUMERIC" + ");";

		// executing tableCreationStatement on DB
		try {
			db.executeStatementNoResult(tableCreationStatement);
			System.out.println("seeDB_schema table created");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		tablesInDB = db.getTables();
		
		
		for (String table : tablesInDB) {
			try {
				Map<String, String> tableColumnData = db
						.getColumnAttribute(table);
				for (Map.Entry<String, String> columnDataSetEntry : tableColumnData
						.entrySet()) {
					String columnName = columnDataSetEntry.getKey();
					String columnType = columnDataSetEntry.getValue();
					int distinctValues = db.getDistinctValueCount(table,
							columnName);
					String seeDBType = getSeeDBType(columnType, distinctValues);

					// build statement for inserting values
					String insertDataStatement = "INSERT INTO seedb_schema"
							+ " VALUES ('" + table + "', '" + columnName + "', '"
							+ columnType + "', '" + seeDBType + "', "
							+ distinctValues + ");";

					db.executeStatementNoResult(insertDataStatement);
				}
			} catch (NoDatabaseConnectionException e) {
				System.out.println("ERR: Unable to access table information");
				e.printStackTrace();
				return;
			} catch (SQLException e){
				System.out.println("ERR: SQLException Thrown");
				e.printStackTrace();
				return;
			}
		}
		
	}

	@Override
	public Set<String> getTableInfoForDB(String databaseName) {
		Database db = connectionKeeper.get(databaseName);
		return db.getTables();
	}

	//NOT SURE WHAT TO MAKE THE RETURN TYPE
	@Override
	public void executeQueryWithResult(String databaseName, String query) {
		Database db = connectionKeeper.get(databaseName);
		try {
			db.executeStatementWithResult(query);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("query unsuccessful");
		}		
	}

	@Override
	public void executeStatement(String databaseName, String query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getMeasures(String databaseName, String tableName) {
		// the query string for getting all columns that fall under measurements
		String queryString = "SELECT columnName FROM seeDB_schema"
				+ "WHERE (table ='" + tableName + "') "
				+ "AND (seedbType='measure');";

		ArrayList<String> queryResultArrList = new ArrayList<String>();
		Database db = connectionKeeper.get(databaseName);
		try {
			ResultSet queryResults = db.executeStatementWithResult(queryString);
			while (queryResults.next()) {
				queryResultArrList.add(queryResults.getString(1));
			}
			return queryResultArrList.toArray(new String[queryResultArrList
					.size()]);
		} catch (SQLException e) {
			System.out.println("Unable to execute query statement "
					+ queryString);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String[] getDimensions(String databaseName, String tableName) {
		String queryString = "SELECT columnName FROM seeDB_schema"
				+ "WHERE (table ='" + tableName + "') "
				+ "AND (seedbType='dimension');";
		ArrayList<String> queryResultArrList = new ArrayList<String>();
		Database db = connectionKeeper.get(databaseName);
		try {
			ResultSet queryResults = db.executeStatementWithResult(queryString);
			while (queryResults.next()) {
				queryResultArrList.add(queryResults.getString(1));
			}
			return queryResultArrList.toArray(new String[queryResultArrList
					.size()]);
		} catch (SQLException e) {
			System.out.println("Unable to execute query statement "
					+ queryString);
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * Gives a classifier based on the SEEDB column type and number of distinct
	 * values
	 * 
	 * @param type - type of the column as assigned by PostgreSQL
	 * 
	 * @param distinctCount - number of distinct values in a column
	 * 
	 * 21 July 2015
	 */
	private String getSeeDBType(String type, int distinctCount) {
		if (distinctCount <= DISTINCT_DIMENSION_COUNT
				&& !type.equalsIgnoreCase("numeric")) {
			return "dimension";
		} else if (distinctCount <= DISTINCT_DIMENSION_COUNT
				&& type.equalsIgnoreCase("numeric")) {
			return "measure";
		}
		return "other";
	}
	

}
