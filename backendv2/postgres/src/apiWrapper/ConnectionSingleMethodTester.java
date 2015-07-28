package apiWrapper;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.rules.*;

import apiWrapper.DatabaseConnectionWrapper;

public class ConnectionSingleMethodTester {

	private static DatabaseConnectionWrapper functional;
	private static String FUN_DB = "booktown";
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUpBefore(){
		functional = new DatabaseConnectionWrapper();
		functional.connectToDB(FUN_DB, "localhost", "postgres", "123");
	}
	
	@Test 
	public void testCBDTablePopulation(){
		ResultSet result = functional.executeQueryWithResult("SELECT * FROM seedb_schema", FUN_DB);	
		try {
			functional.printResultSet(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	 
	
	
}
