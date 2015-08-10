package dbTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import dbExceptions.NoDatabaseConnectionException;
import dbWrapper.Database_BigDawg;

public class db_bigdawgTests {
	private static Database_BigDawg functional;
	public static String DBNAME = "booktown";

	@BeforeClass
	public static void setUpBefore() {
		functional = new Database_BigDawg();
		functional.connectToDB("bigdawg/query", "http://172.16.4.57/",
				"postgres", "123");
	}

	// Testing if connection is established with functional DB
	@Test
	public void establishConnection() {
		assertTrue(functional.verifyConnection(DBNAME));
	}


	// should make to try catch
	@Test
	public void testUniqueColumnValueCount() {
		try {
			int countVal = functional.getDistinctValueCount(DBNAME, "stock",
					"cost");
			assertEquals(12, countVal);
		} catch (SQLException e) {
			assert (false);
		}
	}

	// Test to see if the getColumnAttribute() method works properly using a
	// functional DB and table name
	@Test
	public void columnAttributeTest() {
		Map<String, String> returnedColumnAttr = null;
		try {
			returnedColumnAttr = functional.getColumnAttribute(DBNAME, "stock");
		} catch (SQLException e) {
			if (e.getClass().equals(NoDatabaseConnectionException.class))
				System.out.println("NoDatabaseConnectionException");
			else if (e.getClass().equals(SQLException.class))
				System.out.println("SQLException");
			assert (false);
			return;
		}
		Map<String, String> expectedColumnAttr = new HashMap<String, String>();
		expectedColumnAttr.put("stock", "int4");
		expectedColumnAttr.put("isbn", "text");
		expectedColumnAttr.put("cost", "numeric");
		expectedColumnAttr.put("retail", "numeric");
		assertEquals(expectedColumnAttr, returnedColumnAttr);
	}

	// should make to try catch
	@Test
	public void getTablesInDBTest() {
		Set<String> countedTables = functional.getTables(DBNAME);
		String[] expectedTableNamesA = { "alternate_stock", "author_ids",
				"authors", "book_backup", "book_ids", "book_queue", "books",
				"customers", "daily_inventory", "distinguished_authors",
				"editions", "employees", "favorite_authors", "favorite_books",
				"money_example", "my_list", "numeric_values", "publishers",
				"recent_shipments", "schedules", "shipments",
				"shipments_ship_id_seq", "states", "stock", "stock_backup",
				"stock_view", "subject_ids", "subjects", "text_sorting" };
		String[] expectedTableNamesB = { "seedb_schema", "alternate_stock",
				"author_ids", "authors", "book_backup", "book_ids",
				"book_queue", "books", "customers", "daily_inventory",
				"distinguished_authors", "editions", "employees",
				"favorite_authors", "favorite_books", "money_example",
				"my_list", "numeric_values", "publishers", "recent_shipments",
				"schedules", "shipments", "shipments_ship_id_seq", "states",
				"stock", "stock_backup", "stock_view", "subject_ids",
				"subjects", "seedb_schema", "text_sorting" };
		if (countedTables.contains("seedb_schema")) {
			Set<String> expectedSetB = new HashSet<String>(
					Arrays.asList(expectedTableNamesB));
			assertEquals(30, countedTables.size());
			assertEquals(expectedSetB, countedTables);
		} else {
			Set<String> expectedSetA = new HashSet<String>(
					Arrays.asList(expectedTableNamesA));
			assertEquals(29, countedTables.size());
			assertEquals(expectedSetA, countedTables);

		}
	}

	// Test to see if the executeStatementWithResult() method works properly,
	// with a functional statement and a proper return set.
	@Test
	public void executeStatementResultTest() {
		ResultSet receivedResult = null;
		try {
			receivedResult = functional.executeQueryWithResult(DBNAME,
					"SELECT count(*) FROM stock;");
			receivedResult.next();
			int responseVal = receivedResult.getInt("count");
			assertEquals(16, responseVal);
			receivedResult.close();
		} catch (SQLException e) {
			// e.printStackTrace();
			assertFalse(true);
		}
	}

	// testing to see if error is thrown on false statement
	@Test
	public void executeStatementResultFailTest() {
		ResultSet receivedResult = functional.executeQueryWithResult(DBNAME,
				"SELECT count(*) FROM stocks");
		assertEquals(receivedResult, null);
	}

	// testing if row count works properly
	@Test
	public void testRowCount() {
		boolean catchActivated = false;
		try {
			int resultCount = functional.getRowCount(DBNAME, "stock");
			assertEquals(16, resultCount);
		} catch (SQLException e) {
			// e.printStackTrace();
			catchActivated = true;
		}
		assert (!catchActivated);
	}

	@Test
	public void testCBDTablePopulation() throws SQLException {
		functional.populateTableInfoForDB(DBNAME);
		int rowCount = functional.getRowCount(DBNAME, "seedb_schema");
		int colCount = functional.getColumnCount(DBNAME, "seedb_schema");
		assertEquals(120, rowCount);
		assertEquals(5, colCount);

	}
	

	
}
