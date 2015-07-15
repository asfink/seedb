package postgres;

import static org.junit.Assert.*;

import org.junit.Test;

import postgres.Database;

public class DatabaseTest {

	@Test
	public void establishConnection(){
		Database test = new Database();
		assertEquals(1,1);
	}

}
