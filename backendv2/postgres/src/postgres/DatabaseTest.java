package postgres;
//
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.postgresql.util.PSQLException;
import org.junit.rules.*;

import postgres.Database;

public class DatabaseTest {
	private static Database functional;
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBefore(){
		functional = new Database("booktown","postgres","postgres");
	}
	
	//Testing if connection is established with functional DB
	@Test
	public void establishConnection(){
		assertTrue(functional.verifyConnection());
	}
	
	//Testing if connection is not established with a nonreal DB
	@Test //(expected=PSQLException.class)
	public void testWrongDB(){
		Database notReal = new Database("False", "postgres","postgres");
		thrown.expect(PSQLException.class);
		assertFalse(notReal.verifyConnection());

	}
	
	//Testing if connection is not established using wrong user
	@Test //(expected=PSQLException.class)
	public void testWrongUSRDB(){
		Database falseUSR = new Database("booktown", "meow", "postgres");
		thrown.expect(PSQLException.class);
		assertFalse(falseUSR.verifyConnection());
	}
	
	//Testing if connection is not established using wrong password
	@Test //(expected=PSQLException.class)
	public void testWrongPWDDB(){
		Database falsePWRD = new Database("booktown","postgres","meow");
		thrown.expect(PSQLException.class);
		assertFalse(falsePWRD.verifyConnection());
	}
	
	@Test
	public void testListingTables(){
		String[] tableArr;
		try {
			tableArr = functional.accessableTables();
			System.out.println(Arrays.toString(tableArr));
		} catch (NoDatabaseConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testUniqueColumnValueCount() throws SQLException{
		int countVal = functional.getDistinctValueCount("stock", "cost");
		assertEquals(12, countVal);
	}
	
	
	@Test
	public void schemaTest() throws SQLException{
		System.out.print("SCHEMLASKDF;ALKSDM;ALSKDMVA;LSKMDFA;LKSMDF-----");
		System.out.println(functional.getSchema());
	}
	
	
	@Test 
	public void columnAttributeTest(){
		System.out.println("CLKSJDF:LKSDMVS:LKDMV:LK ");
		try {
			System.out.println(functional.getColumnAttribute("stock"));
		} catch (NoDatabaseConnectionException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
