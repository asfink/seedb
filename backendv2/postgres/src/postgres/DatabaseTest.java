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
		//thrown.expect(PSQLException.class);
		assertFalse(notReal.verifyConnection());
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
		assertFalse(falsePWRD.verifyConnection());
	}
	
	//should make to try catch
	@Test 
	public void testUniqueColumnValueCount() throws SQLException{
		int countVal = functional.getDistinctValueCount("stock", "cost");
		assertEquals(12, countVal);
	}
	
	@Test
	public void schemaTest() throws SQLException{
		assertEquals("$user",functional.getSchema().toString());
	}
	
	//should make to try catch
	@Test 
	public void columnAttributeTest() throws NoDatabaseConnectionException, SQLException{
		Map<String,String> returnedColumnAttr = functional.getColumnAttribute("stock");
		Map<String,String> expectedColumnAttr = new HashMap<String,String>();
		expectedColumnAttr.put("stock","int4");
		expectedColumnAttr.put("isbn","text");
		expectedColumnAttr.put("cost", "numeric");
		expectedColumnAttr.put("retail", "numeric");
		assertEquals(expectedColumnAttr,returnedColumnAttr);
	}

	//should make to try catch
	@Test
	public void getTablesInDBTest() throws SQLException{
		Set<String> countedTables = functional.getTables();
		String[] expectedTableNames = {"alternate_stock", "author_ids","authors","book_backup","book_ids","book_queue","books", "customers","daily_inventory","distinguished_authors","editions","employees","favorite_authors","favorite_books","money_example","my_list","numeric_values","publishers","recent_shipments","schedules","shipments","shipments_ship_id_seq","states","stock","stock_backup","stock_view","subject_ids","subjects","text_sorting"};
		Set<String> expectedSet = new HashSet<String>(Arrays.asList(expectedTableNames));
		assertEquals(29,countedTables.size());
		assertEquals(expectedSet,countedTables);
	}

	//testing if statements are executed
	@Test
	public void executeStatementResultTest(){
		try {
			ResultSet receivedResult = functional.executeStatementWithResult("SELECT count(*) FROM stock");
			int responseVal = receivedResult.getInt(1);
			assertEquals(16,responseVal);
		} catch (SQLException e) {
			assertFalse(true);
			e.printStackTrace();
		}
	}

	//testing to see if error is thrown on false statement
	@Test (expected=PSQLException.class)
	public void executeStatementResultFailTest(){
		try {
			ResultSet receivedResult = functional.executeStatementWithResult("SELECT count(*) FROM stock");
		} catch (SQLException e) {
			assertTrue(true);
			e.printStackTrace();
		}
	}

	//testing if row count works properly
	@Test
	public void testRowCount()
	{
		try{
			int resultCount = functional.getRowCount("stock");
			assertEquals(16,resultCount);
		}
		catch(SQLException e){
			assertFalse(true);
			e.printStackTrace();
		}
		
	}
}
