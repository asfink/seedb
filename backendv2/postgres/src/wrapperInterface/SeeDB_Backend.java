package wrapperInterface;

/*
 * Connect to the seeDB backend to allow for computations to DB? Something something something
 */
public interface SeeDB_Backend {

	/*
	 * Establishes connection to a database.
	 * 
	 * @param database name of the database to connect to
	 * 
	 * @param address ip address/weblocation of the database to connect to
	 * 
	 * @param username username for database login information
	 * 
	 * @param password password for database login information
	 * 
	 * @author Ali Finkelstein
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
	 * @date 27 July 2015
	 */
	public void populateTableInfoForDB(String databaseName);
}
