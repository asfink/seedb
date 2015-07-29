package wrapperInterface;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import seeDBExceptions.NoDatabaseConnectionException;

public class SeeDB_PostgreSQL implements SeeDB_Backend {
	private static int DISTINCT_DIMENSION_COUNT = 20;
	private Map<String, Connection> connectionKeeper;

	public SeeDB_PostgreSQL() {
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		connectionKeeper = new HashMap<String, Connection>();
	}

	@Override
	public void connectToDB(String databaseName, String address,
			String username, String password)
			throws NoDatabaseConnectionException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(address + databaseName,
					username, password);
		} catch (SQLException e) {
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		if (connection == null)
			throw new NoDatabaseConnectionException(
					"No database found to connect");
		connectionKeeper.put(databaseName, connection);
	}

	/*
	 * Verifies that a connection was made to the database
	 * 
	 * Ali Finkelstein 15 July 2015
	 */
	public boolean verifyConnection(String databaseName) {
		Connection conn = connectionKeeper.get(databaseName);
		return conn != null;
	}

	@Override
	public void populateTableInfoForDB(String databaseName) {
		Set<String> tablesInDB = null;
		tablesInDB = getTables(databaseName);
		if (tablesInDB == null)
			return;

		// System.out.println("Table list received");
		/*
		 * CREATE TABLE new_table_name ( table_column_title TYPE_OF_DATA
		 * column_constraints, next_column_title TYPE_OF_DATA
		 * column_constraints, table_constraint table_constraint ) INHERITS
		 * existing_table_to_inherit_from;
		 */

		if (tablesInDB.contains("seedb_schema")) {
			// statement to drop table if it already exists
			String dropTableStatement = "DROP TABLE IF EXISTS seeDB_schema";
			executeStatement(databaseName, dropTableStatement);
			System.out.println("Excesss tables Dropped");
			tablesInDB = getTables(databaseName);
		}

		// creates table if it doesnt exist already
		String tableCreationStatement = "CREATE TABLE IF NOT EXISTS seeDB_schema ("
				+ "tableName TEXT, "
				+ "columnName TEXT, "
				+ "columnType TEXT, "
				+ "seebdType TEXT,"
				+ "numDistinctVals NUMERIC" + ");";

		executeStatement(databaseName, tableCreationStatement);
		System.out.println("seeDB_schema table created");

		tablesInDB = getTables(databaseName);

		for (String table : tablesInDB) {
			try {
				Map<String, String> tableColumnData = getColumnAttribute(
						databaseName, table);
				for (Map.Entry<String, String> columnDataSetEntry : tableColumnData
						.entrySet()) {
					String columnName = columnDataSetEntry.getKey();
					String columnType = columnDataSetEntry.getValue();
					int distinctValues = getDistinctValueCount(databaseName,
							table, columnName);
					String seeDBType = getSeeDBType(columnType, distinctValues);

					// build statement for inserting values
					String insertDataStatement = "INSERT INTO seedb_schema"
							+ " VALUES ('" + table + "', '" + columnName
							+ "', '" + columnType + "', '" + seeDBType + "', "
							+ distinctValues + ");";

					executeStatement(databaseName, insertDataStatement);
				}
			} catch (SQLException e) {
				System.out.println("ERR: SQLException Thrown");
				e.printStackTrace();
				return;
			}
		}

	}

	@Override
	public Set<String> getTableInfoForDB(String databaseName) {
		return getTables(databaseName);
	}

	// NOT SURE WHAT TO MAKE THE RETURN TYPE
	@Override
	public ResultSet executeQueryWithResult(String databaseName, String query) {
		Connection conn = connectionKeeper.get(databaseName);
		Statement stmntToExecute;
		try {
			stmntToExecute = conn.createStatement();
			ResultSet statementResult = stmntToExecute.executeQuery(query);
			return statementResult;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void executeStatement(String databaseName, String statement) {
		Connection conn = connectionKeeper.get(databaseName);
		PreparedStatement stmntToExecute;
		try {
			stmntToExecute = conn.prepareStatement(statement);
			stmntToExecute.executeUpdate();
			stmntToExecute.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getMeasures(String databaseName, String tableName) {
		// the query string for getting all columns that fall under measurements
		String queryString = "SELECT columnName FROM seeDB_schema"
				+ "WHERE (table ='" + tableName + "') "
				+ "AND (seedbType='measure');";

		ArrayList<String> queryResultArrList = new ArrayList<String>();
		try {
			ResultSet queryResults = executeQueryWithResult(databaseName,
					queryString);
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
		try {
			ResultSet queryResults = executeQueryWithResult(databaseName,
					queryString);
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

	/**
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

	/**
	 * Gets the row count for the given table
	 * 
	 * @param tableName - name of the table in the database to analyze
	 * 
	 * @throws SQLException if statement could not be created to allow querying
	 * of row count
	 * 
	 * Ali Finkelstein 16 July 2015
	 */
	public int getRowCount(String databaseName, String tableName)
			throws SQLException {
		Connection conn = connectionKeeper.get(databaseName);
		Statement pgQuery = conn.createStatement();
		String queryStatement = "SELECT count(*) FROM " + tableName;
		ResultSet queryResult = pgQuery.executeQuery(queryStatement);
		queryResult.next();
		int result = queryResult.getInt(1);
		queryResult.close();
		pgQuery.close();
		return result;
	}

	/**
	 * Gets name of all tables in the DB
	 * 
	 * @Throws SQLException if unable to connect to DB or get metadata
	 * information
	 * 
	 * @param databaseName 20 July 2015
	 */
	public Set<String> getTables(String databaseName) {
		Connection conn = connectionKeeper.get(databaseName);
		Set<String> containedTables = new HashSet<String>();
		String[] tableTypesArray = { "VIEW", "TABLE", "SEQUENCE" };
		ResultSet metadataTables;
		try {
			DatabaseMetaData baseMetaData = conn.getMetaData();
			metadataTables = baseMetaData.getTables(null, null, "%",
					tableTypesArray);
			while (metadataTables.next()) {
				containedTables.add(metadataTables.getString(3));
			}
			metadataTables.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return containedTables;
	}

	/**
	 * Gets column information and attributes.
	 * 
	 * @param table - the table from the database you want to get information
	 * from
	 * 
	 * @Return a hash map with the key being the name of the column and the
	 * value being the column type Ali Finkelstein 15 July 2015
	 */
	public Map<String, String> getColumnAttribute(String databaseName,
			String tableName) throws SQLException {
		Connection conn = connectionKeeper.get(databaseName);
		DatabaseMetaData baseMetaData = conn.getMetaData();
		ResultSet rs = baseMetaData.getColumns(null, null, tableName, null);
		HashMap<String, String> tableColumnAttrs = new HashMap<String, String>();

		while (rs.next()) {
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			tableColumnAttrs.put(name, type);
		}
		rs.close();
		return tableColumnAttrs;
	}

	/**
	 * Gets the total number of columns in a database
	 * 
	 * @param databaseName the database that contains the table
	 * 
	 * @param tableName the name of the table to exam
	 * 
	 * @returns an integer that is the total number of columns in the given
	 * table
	 */
	public int getColumnCount(String databaseName, String tableName)
			throws SQLException {
		Connection conn = connectionKeeper.get(databaseName);
		DatabaseMetaData baseMetaData = conn.getMetaData();
		ResultSet rs = baseMetaData.getColumns(null, null, tableName, null);
		int count = 0;
		while (rs.next()) {
			count++;
		}
		rs.close();
		return count;
	}

	/**
	 * Getting the number of distinct values in columnName from tableName
	 * 
	 * @param tableName - table to look into
	 * 
	 * @param columnName - name of the column
	 * 
	 * @throws SQLException is no connection can be made to the database
	 * 
	 * @return -1 if no values present
	 * 
	 * @return int number of distinct values present Ali Finkelstein 16 July
	 * 2015
	 */
	public int getDistinctValueCount(String databaseName, String tableName,
			String columnName) throws SQLException {
		Connection conn = connectionKeeper.get(databaseName);
		Statement pgQuery = conn.createStatement();
		String queryStatement = "SELECT COUNT(DISTINCT " + columnName
				+ ") FROM " + tableName;
		ResultSet pgResult = pgQuery.executeQuery(queryStatement);
		int count = -1;
		while (pgResult.next()) {
			count = pgResult.getInt(1);
		}
		pgQuery.close();
		pgResult.close();
		return count;
	}

}
