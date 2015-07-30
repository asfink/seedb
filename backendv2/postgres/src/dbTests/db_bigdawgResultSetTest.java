package dbTests;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import dbWrapper.BigDawgResultSet;
import dbWrapper.Database_BigDawg;


public class db_bigdawgResultSetTest {
	public JSONObject testJSON;

	@Before	
	public void setUpBefore() {
		Database_BigDawg functional = new Database_BigDawg();
		functional.connectToDB("bigdawg", "http://128.52.183.245:8080/",
				"postgres", "123");
		testJSON = functional.executeQueryWithJSON("bigdawg",
				"select * from mimic2v26.d_patients limit 5");
	}
	
	@Test
	public void setUpResultSetTest(){
		BigDawgResultSet rs = new BigDawgResultSet(testJSON);
	}
}
