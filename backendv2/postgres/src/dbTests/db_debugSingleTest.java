package dbTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import dbWrapper.Database_BigDawg;

public class db_debugSingleTest {
	private static Database_BigDawg functional;


	@Before
	public void setUpBefore() {
		Database_BigDawg functional = new Database_BigDawg();
		functional.connectToDB("bigdawg", "http://172.16.4.57/",
				"postgres", "123");
	}
	
	@Test
	public void executeStatementResultTest() {
		ResultSet receivedResult = null;
		try {
			receivedResult = functional.executeQueryWithResult("bigdawg",
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
}
