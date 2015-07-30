package dbTests;

import static org.junit.Assert.assertArrayEquals;

import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import dbWrapper.BigDawgResultSet;
import dbWrapper.Database_BigDawg;

public class db_debugSingleTest {
	public JSONObject testJSON;

	@Before
	public void setUpBefore() {
		Database_BigDawg functional = new Database_BigDawg();
		functional.connectToDB("bigdawg", "http://128.52.183.245:8080/",
				"postgres", "123");
		testJSON = functional.executeQueryWithJSON("bigdawg",
				"select * from mimic2v26.d_patients limit 5");
	}
	
	// testing getString(int) with int in bounds and calling next
	@Test
	public void testGetStringRightIndexNext() {
		BigDawgResultSet testSet = new BigDawgResultSet(testJSON);
		String[] expectedResult = {"3147-04-05 00:00:00.0","2688-07-30 00:00:00.0","2512-03-02 00:00:00.0","2807-11-13 00:00:00.0","3332-02-08 00:00:00.0"};
		ArrayList<String> testArrList = new ArrayList<String>();
		try {
			while (testSet.next()) {
				testArrList.add(testSet.getString("dod"));
			}
			String[] testResult = testArrList.toArray(new String[testArrList.size()]);
			System.out.println(testResult);
			assertArrayEquals(expectedResult, testResult);
		} catch (SQLException e) {
			e.printStackTrace();
			assert (false);
		}
	}
}
