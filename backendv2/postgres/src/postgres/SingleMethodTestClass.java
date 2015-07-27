package postgres;

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
public class SingleMethodTestClass {
	private static Database functional;
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBefore(){
		functional = new Database("booktown","postgres","postgres");
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
