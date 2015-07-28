package wrapperInterface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import postgres.Database;
import seeDBExceptions.NoDatabaseConnectionException;

///http://128.52.183.245:8000/
public class SeeDB_BigDawg implements SeeDB_Backend {
	private Map<String, Database> connectionKeeper;
	private static int DISTINCT_DIMENSION_COUNT = 20;

	@Override
	public void connectToDB(String databaseName, String address,
			String username, String password) {
		connectionKeeper = new HashMap<String, Database>();		
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
					String insertDataStatement = "RELATION(INSERT INTO seedb_schema"
							+ " VALUES ('" + table + "', '" + columnName + "', '"
							+ columnType + "', '" + seeDBType + "', "
							+ distinctValues + "));";

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getTableInfoForDB(String databaseName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet executeQueryWithResult(String databaseName, String query) {
		return null;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executeStatement(String databaseName, String query) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getMeasures(String databaseName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getDimensions(String databaseName, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}
	



	/*
	 * Given a type and the number of distinct values of a column, a
	 * qualification for the SeeDB system is give
	 * 
	 * @param type the variable type according to SQL
	 * 
	 * @param distinctCount the number of distinct values in the column
	 * 
	 * @return a string of either "dimension", "measure", or "other" as assigned
	 */
	public String getSeeDBType(String type, int distinctCount) {
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
