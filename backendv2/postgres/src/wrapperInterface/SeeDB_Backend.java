package wrapperInterface;

import java.util.Set;

/*
 * Connect to the seeDB backend to allow for computations to DB? something
 * 
 * @author Ali Finkelstein
 */
public interface SeeDB_Backend {

	/*
	 * Establishes connection to a database.
	 * 
	 * @param databaseName the database to which connections will be established
	 * 
	 * @param address IP address/weblocation of the database to connect to
	 * 
	 * @param username user name for database login information
	 * 
	 * @param password password for database login information
	 * 
	 * @author Ali Finkelstein
	 * 
	 * @date 27 July 2015
	 */
	public void connectToDB(String databaseName, String address,
			String username, String password);

	/*
	 * Populates a table in the Database to use for calculations and searches
	 * for the SeeDB project. Either it creates a new table, if possible, or
	 * uses table that already exists.
	 * 
	 * @param databaseName name of the database to connect to
	 * 
	 * @author Ali Finkelstein
	 * 
	 * @date 27 July 2015
	 */
	public void populateTableInfoForDB(String databaseName);

	/*
	 * Returns a set listing all of the tables available in a given database
	 * 
	 * @param databaseName the database which we are getting information about
	 * 
	 * @return a set of the possible tables
	 * 
	 * @Author Ali Finkelstein
	 * 
	 * @date 27 July 2015
	 */
	public Set<String> getTableInfoForDB(String databaseName);

	/*
	 * Executes a query on a given database and returns the results of the query
	 * if possible.
	 * 
	 * @param databaseName the database which is being queried
	 * 
	 * @return TODO:decide the return type
	 */
	public void executeQueryWithResult(String databaseName, String query);

	/*
	 * Executes query on a database and does not return a result of any type
	 * 
	 * @param databaseName the database the statement will be executed upon
	 * @param query string of the statement to be executed on the DB
	 * 
	 * @Author Ali Finkelstein
	 * @date 27 July 2015
	 */
	public void executeStatement(String databaseName, String query);
	
	/*
	 * Gets the names of columns that are measurements in the table
	 * 
	 * @param databaseName the database than contains the table
	 * @param tableName the name of the table to get the classifications
	 * 
	 * @return an array of strings containing the names of the columns 
	 */
	public String[] getMeasures(String databaseName, String tableName);
		
	/*
	 * Gets the names of columns that are dimensions in the table
	 * 
	 * @param databaseName the database than contains the table
	 * @param tableName the name of the table to get the classifications
	 * 
	 * @return an array of strings containing the names of the columns 
	 */
	public String[] getDimensions(String databaseName, String tableName);
}
