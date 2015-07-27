package apiWrapper;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.postgresql.util.PSQLException;
import org.junit.rules.*;

import apiWrapper.DatabaseConnectionWrapper;


public class DatabaseConnectionTest {
	private static DatabaseConnectionWrapper functional;
	private static String FUN_DB = "booktown";
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBefore(){
		functional = new DatabaseConnectionWrapper();
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
		try {
			ResultSetMetaData rsmd = result.getMetaData();
			int colNumber = rsmd.getColumnCount();
			while (result.next()){
				for(int i = 1; i<=colNumber; i++){
					if(i>1) System.out.println(",  ");
					String val = result.getString(1);
					System.out.println(val+" "+rsmd.getColumnName(i));
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
