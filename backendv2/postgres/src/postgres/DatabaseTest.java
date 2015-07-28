package postgres;
//
import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.BeforeClass;
import org.postgresql.util.PSQLException;
import org.junit.rules.*;

import postgres.Database;
import seeDBExceptions.NoDatabaseConnectionException;
import seeDBExceptions.NoMetaDataFoundException;

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
	@Test (expected=PSQLException.class)
	public void testWrongDB(){
		Database notReal = new Database("False", "postgres","postgres");
	}
	
	//Testing if connection is not established using wrong user
	@Test (expected=PSQLException.class)
	public void testWrongUSRDB(){
		Database falseUSR = new Database("booktown", "meow", "postgres");
		//thrown.expect(PSQLException.class);
		assertFalse(falseUSR.verifyConnection());
	}
	
	//Testing if connection is not established using wrong password
	@Test (expected=PSQLException.class)
	public void testWrongPWDDB(){
		Database falsePWRD = new Database("booktown","postgres","meow");
		//thrown.expect(PSQLException.class);
		assert(falsePWRD.verifyConnection());
	}
	
	//should make to try catch
	@Test 
	public void testUniqueColumnValueCount(){
		try{
			int countVal = functional.getDistinctValueCount("stock", "cost");
			assertEquals(12, countVal);
		} catch(SQLException e){
			assert(false);
		}
	}
	
	@Test
	public void schemaTest() {
		try{
			assertEquals("$user",functional.getSchema().toString());
		} catch (SQLException e) {
			assert(false);
		}
	}
	
	
	@Test 
	public void verifyMetaDataObjectCreated(){
		
	}
	
	//Test to see if the getColumnAttribute() method works properly using a functional DB and table name
	@Test 
	public void columnAttributeTest(){
		Map<String,String> returnedColumnAttr = null;
		try{
			returnedColumnAttr = functional.getColumnAttribute("stock");
		} catch (NoDatabaseConnectionException | SQLException e){
			if (e.getClass().equals(NoDatabaseConnectionException.class)) System.out.println("NoDatabaseConnectionException");
			else if (e.getClass().equals(SQLException.class)) System.out.println("SQLException");
			assert(false);
			return;
		}
		Map<String,String> expectedColumnAttr = new HashMap<String,String>();
		expectedColumnAttr.put("stock","int4");
		expectedColumnAttr.put("isbn","text");
		expectedColumnAttr.put("cost", "numeric");
		expectedColumnAttr.put("retail", "numeric");
		assertEquals(expectedColumnAttr,returnedColumnAttr);
	}

	//should make to try catch
	@Test
	public void getTablesInDBTest(){
		Set<String> countedTables = functional.getTables();
		String[] expectedTableNames = {"alternate_stock", "author_ids","authors","book_backup","book_ids","book_queue","books", "customers","daily_inventory","distinguished_authors","editions","employees","favorite_authors","favorite_books","money_example","my_list","numeric_values","publishers","recent_shipments","schedules","shipments","shipments_ship_id_seq","states","stock","stock_backup","stock_view","subject_ids","subjects","text_sorting"};
		Set<String> expectedSet = new HashSet<String>(Arrays.asList(expectedTableNames));
		assertEquals(29,countedTables.size());
		assertEquals(expectedSet,countedTables);
	}

	//Test to see if the executeStatementWithResult() method works properly, with a functional statement and a proper return set.
	@Test
	public void executeStatementResultTest(){
		ResultSet receivedResult = null;
		try {
			receivedResult = functional.executeStatementWithResult("SELECT count(*) FROM stock;");
			receivedResult.next();
			int responseVal = receivedResult.getInt("count");
			assertEquals(16,responseVal);
			receivedResult.close();
		} catch (SQLException e) {
			//e.printStackTrace();
			assertFalse(true);
		}
	}

	//testing to see if error is thrown on false statement
	@Test
	public void executeStatementResultFailTest(){
		ResultSet receivedResult = null;
		try {
			receivedResult = functional.executeStatementWithResult("SELECT count(*) FROM stocks");
			assertTrue(false);
			receivedResult.close();
		} catch (SQLException e) {
			assertTrue(true);
		}
	}

	//testing if row count works properly
	@Test
	public void testRowCount()
	{
		boolean catchActivated = false;
		try{
			int resultCount = functional.getRowCount("stock");
			assertEquals(16,resultCount);
		}
		catch(SQLException e){
			e.printStackTrace();
			catchActivated = true;
		}
		assert(!catchActivated);
	}
}
