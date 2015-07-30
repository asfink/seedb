package dbWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.json.JSONException;
import org.json.JSONObject;

import dbExceptions.NoDatabaseConnectionException;

/*
 * http://128.52.183.245:8000/
 * curl -v -H "Content-Type: application/json" -X POST -d '{"query":"RELATION(select * from mimic2v26.d_patients limit 5)","authorization":{},"tuplesPerPage":1,"pageNumber":1,"timestamp":"2012-04-23T18:25:43.511Z"}' http://128.52.183.245:8080/bigdawg/query
 */
public class Database_BigDawg implements Database {
	private static int DISTINCT_DIMENSION_COUNT = 20;
	private Map<String, String> connectionKeeper;

	public Database_BigDawg() {
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch (ClassNotFoundException e) {
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		connectionKeeper = new HashMap<String, String>();
	}

	@Override
	public void connectToDB(String databaseName, String address,
			String username, String password) {
		connectionKeeper.put(databaseName, address + databaseName);
	}

	/**
	 * Verifies that a connection was made to the database
	 * 
	 * @param databaseName
	 *            name of the database to verify
	 * @author Ali Finkelstein
	 * @date 15 July 2015
	 */
	// public boolean verifyConnection(String databaseName) {
	// Connection conn = connectionKeeper.get(databaseName);
	// return conn != null;
	// }

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


	/**
	 * For connecting to the thing WORKS http://requestb.in/1l1ff841
	 * 
	 * @param meow
	 * @throws IOException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	public void sendSansResponseMeow(String postURL, String query)
			throws IOException, InterruptedException, ExecutionException,
			JSONException {
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
			httpclient.start();
			HttpPost httppost = new HttpPost(
					"http://128.52.183.245:8080/bigdawg/query");

			httppost.addHeader("Content-Type", "application/json");
			JSONObject json = new JSONObject();
			json.put("query", "RELATION(" + query + ")");

			System.out.println(json.toString());

			StringEntity se = new StringEntity(json.toString());
			httppost.setEntity(se);
			Future<HttpResponse> future = httpclient.execute(httppost, null);
			HttpResponse response = future.get();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			System.out.println(builder.toString());
			System.out.println("Response: " + response.getStatusLine());
			System.out.println("Shutting down");
		} finally {
			httpclient.close();
		}
	}

	// NOT SURE WHAT TO MAKE THE RETURN TYPE
	@Override
	public ResultSet executeQueryWithResult(String databaseName, String query) {
		String url = connectionKeeper.get(databaseName);
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
			httpclient.start();
			HttpPost httppost = new HttpPost(url + "/query");
			httppost.addHeader("Content-Type", "application/json");

			JSONObject json = new JSONObject();
			json.put("query", "RELATION(" + query + ")");

			StringEntity se = new StringEntity(json.toString());
			httppost.setEntity(se);

			Future<HttpResponse> future = httpclient.execute(httppost, null);
			HttpResponse response = future.get();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));

			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			System.out.println(builder.toString());
			System.out.println("Response: " + response.getStatusLine());
			System.out.println("Shutting down");
			
			
		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException thrown");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("JSONException thrown");
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			System.out.println("UnsupportedOperationException thrown");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException thrown");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("InterruptedException thrown");
			e.printStackTrace();
		} catch (ExecutionException e) {
			System.out.println("ExecutionException thrown");
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				System.out.println("HTTPClient unable to close. IO Exception thrown.");
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void executeStatement(String databaseName, String statement) {
		String url = connectionKeeper.get(databaseName);
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try {
			httpclient.start();
			HttpPost httppost = new HttpPost(url + "/query");
			httppost.addHeader("Content-Type", "application/json");

			JSONObject json = new JSONObject();
			json.put("query", "RELATION(" + statement + ")");

			StringEntity se = new StringEntity(json.toString());
			httppost.setEntity(se);

			Future<HttpResponse> future = httpclient.execute(httppost, null);
			HttpResponse response = future.get();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent(), "UTF-8"));

			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			System.out.println(builder.toString());
			System.out.println("Response: " + response.getStatusLine());
			System.out.println("Shutting down");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	 * @param type
	 *            - type of the column as assigned by PostgreSQL
	 * 
	 * @param distinctCount
	 *            - number of distinct values in a column
	 * 
	 * @date 21 July 2015
	 * @author Ali Finkelstein
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
	 * @param tableName
	 *            - name of the table in the database to analyze
	 * 
	 * @throws SQLException
	 *             if statement could not be created to allow querying of row
	 *             count
	 * 
	 * @author Ali Finkelstein
	 * @date 16 July 2015
	 */
	public int getRowCount(String databaseName, String tableName)
			throws SQLException {
		String queryString = "SELECT count(*) FROM " + tableName;
		ResultSet queryResult = executeQueryWithResult(databaseName,queryString);
		queryResult.next();
		int result = queryResult.getInt(1);
		queryResult.close();
		return result;
	}

	/**
	 * Gets name of all tables in the DB
	 * 
	 * @Throws SQLException if unable to connect to DB or get metadata
	 *         information
	 * 
	 * @param databaseName
	 *            the name of the database
	 * 
	 * @date 20 July 2015
	 *  RELATION()
	 *  
	 *  get rid of anything with pg_ or sql_ in the beginning
	 */
	public Set<String> getTables(String databaseName) {
		String queryString = "SELECT table_name FROM information_schema.tables WHERE table_type='BASE TABLE';";
		ResultSet queryResult = executeQueryWithResult(databaseName,queryString);
		Set<String> containedTables = new HashSet<String>();
		String pg = "pg_";
		String sql = "sql_";
		try {
			while (queryResult.next()){
				String tableName = queryResult.getString(1);
				if (!tableName.toLowerCase().contains(pg.toLowerCase()) && !tableName.toLowerCase().contains(sql.toLowerCase())){
					containedTables.add(tableName);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Unable to get tables from the requested DB");
		}
		return containedTables;
	}

	/**
	 * Gets column information and attributes.
	 * 
	 * @param table
	 *            - the table from the database you want to get information from
	 * 
	 * @Return a hash map with the key being the name of the column and the
	 *         value being the column type Ali Finkelstein 15 July 2015
	 *         
	 *         Select *  from tableName limit 0; <- gives the schema and the types
	 */
	public Map<String, String> getColumnAttribute(String databaseName,
			String tableName) throws SQLException {
		String queryString = "Select *  FROM "+tableName+" LIMIT 0;";
		ResultSet queryResult = executeQueryWithResult(databaseName,queryString);
		HashMap<String, String> tableColumnAttrs = new HashMap<String, String>();
		while (queryResult.next()) {
			String name = queryResult.getString("COLUMN_NAME");
			String type = queryResult.getString("TYPE_NAME");
			tableColumnAttrs.put(name, type);
		}
		return tableColumnAttrs;
	}

	/**
	 * Gets the total number of columns in a database
	 * 
	 * @param databaseName
	 *            the database that contains the table
	 * 
	 * @param tableName
	 *            the name of the table to exam
	 * 
	 * @returns an integer that is the total number of columns in the given
	 *          table
	 *          
	 *          Select * From tableName limit 0; <- gives the schema and the types
	 */
	public int getColumnCount(String databaseName, String tableName)
			throws SQLException {
		String queryString = "SELECT * FROM "+tableName+" LIMIT 0;";
		ResultSet queryResult = executeQueryWithResult(databaseName,queryString);
		int count = 0;
		while (queryResult.next()) {
			count++;
		}
		return count;
	}

	/**
	 * Getting the number of distinct values in columnName from tableName
	 * 
	 * @param tableName
	 *            - table to look into
	 * 
	 * @param columnName
	 *            - name of the column
	 * 
	 * @throws SQLException
	 *             is no connection can be made to the database
	 * 
	 * @return -1 if no values present
	 * 
	 * @return number of distinct values present
	 * @author Ali Finkelstein
	 * @date 16 July 2015
	 */
	public int getDistinctValueCount(String databaseName, String tableName,
			String columnName) throws SQLException {
		String queryString = "SELECT COUNT(DISTINCT " + columnName
				+ ") FROM " + tableName;
		ResultSet pgResult = executeQueryWithResult(databaseName,queryString);
		int count = -1;
		while (pgResult.next()) {
			count = pgResult.getInt(1);
		}
		return count;
	}

}
