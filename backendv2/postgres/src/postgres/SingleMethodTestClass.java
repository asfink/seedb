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

import seeDBExceptions.NoDatabaseConnectionException;
import seeDBExceptions.NoMetaDataFoundException;
public class SingleMethodTestClass {
	private static Database functional;
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBefore(){
		functional = new Database("booktown","postgres","postgres");
	}
	



	
}
