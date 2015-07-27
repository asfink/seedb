package apiWrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import postgres.Database;
import postgres.NoDatabaseConnectionException;

public class DatabaseConnectionWrapper {
	private static int DISTINCT_DIMENSION_COUNT = 20;
	private Map<String, Database> connectionKeeper;

	/*
	 * Establishes ability to connect to database
	 * 
	 * 20 July 2015
	 */
	public DatabaseConnectionWrapper() {
		connectionKeeper = new HashMap<String, Database>();
	}

	/*
	 * Establishes connection to database
	 * 
	 * 20 July 2015
	 */
	public void connectToDB(String databaseName, String address,
			String username, String password) {
		Database connectionDB = new Database();
		connectionDB.connect(databaseName, address, username, password);
		connectionKeeper.put(databaseName, connectionDB);
	}
	
	public Boolean validateDatabaseConnection(String databaseName){
		Database db = connectionKeeper.get(databaseName);
		return db.verifyConnection();
	}

	/*
	 * Makes a new table in the DB for SeeDB to use for it's analysis.
	 * 
	 * 
	 * @throws SQLException if no database is able to be found or no metadata
	 * available. 20 July 2015
	 */
	public void populateTableInfoForDB(String databaseName) {
		Set<String> tablesInDB = null;
		Database db = connectionKeeper.get(databaseName);
		tablesInDB = db.getTables();
		if (tablesInDB == null)
			return;

		System.out.println("Table list received");
		/*
		 * CREATE TABLE new_table_name ( table_column_title TYPE_OF_DATA
		 * column_constraints, next_column_title TYPE_OF_DATA
		 * column_constraints, table_constraint table_constraint ) INHERITS
		 * existing_table_to_inherit_from;
		 */

		// statement to drop table if it already exists
		String dropTableStatement = "DROP TABLE IF EXISTS seeDB_schema";
		try {
			db.executeStatementNoResult(dropTableStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Excesss tables Dropped");
		
		// creates table if it doesnt exist already
		String tableCreationStatement = "CREATE TABLE IF NOT EXISTS seeDB_schema ("
				+ "tableName TEXT, "
				+ "columnName TEXT, "
				+ "columnType TEXT, "
				+ "seebdType TEXT,"
				+ "numDistinctVals NUMERIC " + ");";

		// executing tableCreationStatement on DB
		try {
			db.executeStatementNoResult(tableCreationStatement);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("seeDB_schema table created");


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
					String insertDataStatement = "INSERT INTO seeDB_schema"
							+ "VALUES (" + table + ", " + columnName + ", "
							+ columnType + "," + seeDBType + ", "
							+ distinctValues + ");";

					db.executeStatementNoResult(insertDataStatement);
				}
			} catch (NoDatabaseConnectionException | SQLException e) {
				System.out.println("Unable to access table information");
				e.printStackTrace();
			}
		}

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

	/*
	 * Output's the database tables :P
	 * 
	 * @return a set of strings containing the names of all the tables
	 */
	public Set<String> getTableInfoForDB(String database) {
		Database db = connectionKeeper.get(database);
		return db.getTables();
	}

	/*
	 * 
	 */
	public ResultSet executeQueryWithResult(String query, String database) {
		Database db = connectionKeeper.get(database);
		try {
			return db.query(query);
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
	 * 
	 * @returns null if query was unsuccessful
	 * 
	 * 21 July 2015
	 */
	public String[] getDimensions(String tableName, String database) {
		String queryString = "SELECT columnName FROM seeDB_schema"
				+ "WHERE (table ='" + tableName + "') "
				+ "AND (seedbType='dimension');";
		ArrayList<String> queryResultArrList = new ArrayList<String>();
		Database db = connectionKeeper.get(database);
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
	 * Queries database for all columns that represent measurements.
	 * 
	 * @returns array of strings that are the columns that represent
	 * measurements.
	 * 
	 * @returns null if query was unsuccessful
	 * 
	 * 21 July 2015
	 */
	public String[] getMeasures(String tableName, String dbName) {
		//the query string for getting all columns that fall under measurements
		String queryString = "SELECT columnName FROM seeDB_schema"
				+ "WHERE (table ='" + tableName + "') "
				+ "AND (seedbType='measure');";
		
		ArrayList<String> queryResultArrList = new ArrayList<String>();
		Database db = connectionKeeper.get(dbName);
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

	public Map<String, String[]> getInfoForTable(String databaseName,
			String tableName) {

		return null;
	}
}
