package dbTests;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dbWrapper.Database_BigDawg;

public class db_debugSingleTest {
	private static Database_BigDawg functional;
	public static String DBNAME = "booktown";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setUpBefore() {
		functional = new Database_BigDawg();
		functional.connectToDB("bigdawg", "http://128.52.183.245:8080/",
				"postgres", "123");
	}

	@Test
	public void asynchtestthing() {
		try {
			functional.sendSansResponseMeow("ugh",
					"select * from mimic2v26.d_patients limit 5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
