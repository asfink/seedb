package dbTests;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import dbExceptions.NoDatabaseConnectionException;
import dbWrapper.Database_BigDawg;

public class db_debugSingleTest {
	private static Database_BigDawg functional;
	public static String DBNAME = "booktown";
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@BeforeClass
	public static void setUpBefore() {
		functional = new Database_BigDawg();
		try {
			functional.connectToDB("booktown", "jdbc:postgresql://localhost/",
					"postgres", "123");
		} catch (NoDatabaseConnectionException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void asynchtestthing()
	{
		try {
			functional.sendSansResponse("ugh");
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
}
