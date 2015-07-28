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


public class DatabaseConnectionTest {
	private static DatabaseConnectionWrapper functional;
	private static String FUN_DB = "booktown";
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void setUpBefore(){
		functional = new DatabaseConnectionWrapper();
	}
	
	@Test
	public void testingConnectionCreation(){
		functional.connectToDB(FUN_DB, "localhost", "postgres", "123");
		assertTrue(functional.validateDatabaseConnection("booktown"));
	}
	
	@Test 
	public void testCBDTablePopulation(){
		functional.connectToDB(FUN_DB, "localhost", "postgres", "123");
		functional.populateTableInfoForDB(FUN_DB);
		assert(true);
		ResultSet result = functional.executeQueryWithResult("SELECT * FROM seedb_schema", FUN_DB);	
	
	}
	
	@Test 
	public void testPrintfunction(){
		functional.connectToDB(FUN_DB, "localhost", "postgres", "123");
		ResultSet result = functional.executeQueryWithResult("SELECT * FROM seedb_schema", FUN_DB);	
		try {
			functional.printResultSet(result);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		assert(true);
	}
	
//	@Test
//	public void testGetMeasurement 
//
//	 
	
	
}
