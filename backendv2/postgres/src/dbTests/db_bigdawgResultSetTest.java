package dbTests;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import dbWrapper.BigDawgResultSet;
import dbWrapper.Database_BigDawg;

public class db_bigdawgResultSetTest {
	public static JSONObject testJSON;

	@BeforeClass
	public static void setUpBefore() {
		Database_BigDawg functional = new Database_BigDawg();
		functional.connectToDB("bigdawg", "http://128.52.183.245:8080/",
				"postgres", "123");
		testJSON = functional.executeQueryWithJSON("bigdawg",
				"select * from mimic2v26.d_patients limit 5");
	}

	/*
	 * testing next() making sure to keep inbounds
	 */
	@Test
	public void test_Next_Inbounds() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		int testNext = 0;
		try {
			while (testSet.next()) {
				testNext++;
			}
			assertEquals(5, testNext);
		} catch (SQLException e) {
			assert (false);
			e.printStackTrace();
		}
	}

	/*
	 * testing getString(int) with correct in bounds without calling next
	 */
	@Test
	public void test_GetString_RightIndex_NoNext() {
		@SuppressWarnings("resource")
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String expectedResult = "3147-04-05 00:00:00.0";
		try {
			testSet.next();
			String testResult = testSet.getString(4);
			assertEquals(expectedResult, testResult);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}

	/*
	 * testing getString(column) with correct in bounds without calling next
	 */
	@Test
	public void test_GetString_RightColumn_NoNext() {
		@SuppressWarnings("resource")
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String expectedResult = "3147-04-05 00:00:00.0";
		try {
			testSet.next();
			String testResult = testSet.getString("dod");
			assertEquals(expectedResult, testResult);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}

	/*
	 * testing getInt(column) with correct in bounds without calling next
	 */
	@Test
	public void test_GetInt_RightIndex_NoNext() {
		@SuppressWarnings("resource")
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		int expectedResult = 1039;
		try {
			testSet.next();
			int testResult = testSet.getInt(1);
			assertEquals(expectedResult, testResult);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}

	/*
	 * testing getString(String) with String in bounds and cycle through all
	 * rows
	 */
	@Test
	public void test_GetString_StringRight_Cycle() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String[] expectedResult = { "3147-04-05 00:00:00.0",
				"2688-07-30 00:00:00.0", "2512-03-02 00:00:00.0",
				"2807-11-13 00:00:00.0", "3332-02-08 00:00:00.0" };
		ArrayList<String> testArrList = new ArrayList<String>();
		try {
			while (testSet.next()) {
				testArrList.add(testSet.getString("dod"));
			}
			String[] testResult = testArrList.toArray(new String[testArrList
					.size()]);
			assertArrayEquals(expectedResult, testResult);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}

	/*
	 * testing getString(int) with int out of bounds
	 */
	@Test
	public void test_GetString_OutOfBoundsIndex() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		try {
			testSet.next();
			String meow = testSet.getString(10);
			assert (false);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (true);
		} catch (IndexOutOfBoundsException e) {
			// e.printStackTrace();
			assert (true);
		}
	}

	/*
	 * testing getString(string) with string real
	 */
	@Test
	public void test_GetString_NotRealColumn() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		try {
			testSet.next();
			String meow = testSet.getString("meow");
			assert (false);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (true);
		} catch (IndexOutOfBoundsException e) {
			// e.printStackTrace();
			assert (true);
		}
	}

	/*
	 * testing getInt(int) with int in bounds and calling next
	 */
	@Test
	public void test_GetInt_OutOfBoundsIndex() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		try {
			testSet.next();
			int meow = testSet.getInt(10);
			assert (false);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (true);
		} catch (IndexOutOfBoundsException e) {
			// e.printStackTrace();
			assert (true);
		}
	}

	/*
	 * testing getObject(int) with int in bounds and calling next
	 */
	@Test
	public void test_GetObject_OutOfBoundsIndex() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		try {
			testSet.next();
			Object meow = testSet.getObject(10);
			assert (false);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (true);
		} catch (IndexOutOfBoundsException e) {
			// e.printStackTrace();
			assert (true);
		}
	}

	/*
	 * testing getObject(string) with string real
	 */
	@Test
	public void test_GetObject_NotRealColumn() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		try {
			testSet.next();
			Object meow = testSet.getObject("meow");
			assert (false);
		} catch (SQLException e) {
			//e.printStackTrace();
			assert (true);
		} catch (IndexOutOfBoundsException e) {
			//e.printStackTrace();
			assert (true);
		}
	}

	/*
	 * testing getObject(column) with correct in bounds without calling next
	 */
	@Test
	public void test_GetObject_RightIndex_NoNext() {
		@SuppressWarnings("resource")
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String expectedResult = "3147-04-05 00:00:00.0";
		try {
			testSet.next();
			Object testResult = testSet.getObject(4);
			assertEquals(expectedResult, testResult);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (false);
		}
	}

	// testing getString(column) with correct in bounds without calling next
	@Test
	public void test_GetObject_RightString_NoNext() {
		@SuppressWarnings("resource")
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String expectedResult = "3147-04-05 00:00:00.0";
		try {
			testSet.next();
			Object testResult = testSet.getObject("dod");
			assertEquals(expectedResult, testResult);
		} catch (SQLException e) {
			// e.printStackTrace();
			assert (false);
		}
	}
}
