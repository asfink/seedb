package apiWrapper;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.postgresql.util.PSQLException;
import org.junit.rules.*;

import apiWrapper.DatabaseConnection;


public class DatabaseConnectionTest {
	private static DatabaseConnection functional;
	private static String FUN_DB = "booktown";
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBefore(){
		functional = new DatabaseConnection();
	}
	
	@Test
	public void testingConnectionCreation(){
		functional.connectToDB(FUN_DB, "localhost", "postgres", "123");
		assertTrue(functional.validateDatabaseConnection("booktown"));
	}
	
	@Test 
	public void testCBDTablePopulation(){
		functional.populateTableInfoForDB(FUN_DB);
		ResultSet result = functional.executeQueryWithResult("", FUN_DB);
		
	}
	
	
	
}
