package microbenchmarks;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.CommonOperations;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MultipleAttribute {
	private int working_mem;
	private String table;
	private Connection connection;
	
	public Connection getConnection(String dbType, String dbAddress, String dbUser, String dbPassword) {
		Connection connection = null;
		try {
		    Class.forName("org." + dbType + ".Driver");
		} catch (ClassNotFoundException e) {
			connection = null;
		    System.out.println("DB driver not found");
		    e.printStackTrace();
		    return null;
		}
		
		//attempt to connect
		try {
			connection = DriverManager.getConnection(
					"jdbc:" + dbType + "://" + dbAddress, dbUser,
					dbPassword);
		} catch (SQLException e) {
			connection = null;
			System.out.println("DB connection failed.");
			e.printStackTrace();
			return null;
		}
		return connection;
	}
	
	public MultipleAttribute(String table, int working_mem, String dbType, String dbAddress, String dbUser, String dbPassword) {
		this.table = table;
		this.working_mem = working_mem;
		this.connection = this.getConnection(dbType, dbAddress, dbUser, dbPassword);
	}
	
	public void runMultipleAttributeTest() throws SQLException {

		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
		
		DatabaseMetaData dbmd = null;
		ResultSet rs = null;
		try {
			dbmd = connection.getMetaData();
			rs = dbmd.getColumns(null, null, table, null);

		} catch (SQLException e) {
			System.out.println("Error in executing metadata query");
			e.printStackTrace();
			return;
		}
		
		List<String> dimensionAttributes = Lists.newArrayList();
		HashMap<String, Integer> dimensionAttributeDistinctValues = Maps.newHashMap();
		List<String> measureAttributes = Lists.newArrayList();
		
		while (rs.next()) {
			String attribute = rs.getString("COLUMN_NAME");
			if (attribute.startsWith("dim")) {
				dimensionAttributes.add(attribute);
				dimensionAttributeDistinctValues.put(attribute, Integer.parseInt(attribute.split("_")[1])); // col names are dim10_50
			}
			else if (attribute.startsWith("measure")) measureAttributes.add(attribute);
		}
		
		
		List<List<String>> combinations = CommonOperations.getCombinations(2, dimensionAttributes);
		
		for (String measureAttribute : measureAttributes) {
			for (List<String> combination : combinations) {
				for (String dimensionAttribute : dimensionAttributes) {
					if (! combination.contains(dimensionAttribute)) {
						try {
							Statement stmt = null;
							stmt = connection.createStatement();
							stmt.execute("set work_mem=" + working_mem + ";");
							String distinctQuery = "SELECT DISTINCT " + dimensionAttribute + " FROM " + table + " LIMIT 2";
						    rs = stmt.executeQuery(distinctQuery);
						    
						    List<String> distinctValues = new ArrayList<String>();
						    while (rs.next()) {
						    	distinctValues.add(rs.getString(1));
						    }
						    
						    
						    String individualQuery1 = "SELECT " + Joiner.on(", ").join(combination) + ", SUM(" + measureAttribute + ") FROM " + 
						    table + " WHERE " + dimensionAttribute + " = '" + distinctValues.get(0) + "' GROUP BY " + Joiner.on(", ").join(combination);
						    
						    String individualQuery2 = "SELECT " + Joiner.on(", ").join(combination) + ", SUM(" + measureAttribute + ") FROM " + 
						    table + " WHERE " + dimensionAttribute + " = '" + distinctValues.get(1) + "' GROUP BY " + Joiner.on(", ").join(combination);
						    
//							System.out.println( "\n" + individualQuery1);
//							System.out.println(individualQuery2);
						    
							rs = null;
							stmt.execute("set work_mem=" + working_mem + ";");
							long start = System.nanoTime();
						    rs = stmt.executeQuery(individualQuery1);
						    rs = stmt.executeQuery(individualQuery2);
						    System.out.print("working mem: " + working_mem + ", table: " + table + ", # queries: 2 , groupby: " + combination + ", 2 different values of: " + dimensionAttribute);
						    System.out.println(", Time taken: " + (System.nanoTime() - start));

							String combinedQuery = "SELECT " +  Joiner.on(", ").join(combination)  + ", SUM(" + measureAttribute + "), CASE WHEN " +
													dimensionAttribute + "= '" + distinctValues.get(0) + "' THEN 0 ELSE 1 END AS temp FROM " + table + 
													" WHERE " + dimensionAttribute + " = '" + distinctValues.get(0) + "' OR " +
													dimensionAttribute + " = '" + distinctValues.get(1) + 
													"' GROUP BY " + Joiner.on(", ").join(combination) + ", temp";
							
							
							// System.out.println(combinedQuery);
							start = System.nanoTime();
							rs = stmt.executeQuery(combinedQuery);
						    System.out.print("working mem: " + working_mem + ", table: " + table + ", # queries: 1 , groupby: " + combination + ", 2 different values of: " + dimensionAttribute);
						    System.out.println(", Time taken: " + (System.nanoTime() - start));
						    System.out.println("\n\n\n");
							
						    
						} catch (SQLException e) {
							System.out.println("Error in executing query");
						}
					}
				}
			}
		}
		
		
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
//			for (int i = 0; i < measureAttributes.size() ; i++) {
//				List<List<String>> combinations = CommonOperations.getCombinations(i + 1, measureAttributes);
//				
//				for (List<String> combo : combinations) {
//					List<String> sumCombo = new ArrayList <String>();
//					for (String measure : combo) {
//						sumCombo.add("count(" + measure + ")");
//					}
//				
//					String sqlQuery = "SELECT " + Joiner.on(", ").join(sumCombo) + " FROM " + table + " GROUP BY " + dimensionAttribute;
//					System.out.println( "\n" + sqlQuery);
//					Statement stmt = null;
//					rs = null;
//					try {
//						stmt = connection.createStatement();
//						stmt.execute("set work_mem=" + working_mem + ";");
//						long start = System.nanoTime();
//					    rs = stmt.executeQuery(sqlQuery);
//					    System.out.print("working mem: " + working_mem + ", table: " + table + ", # aggregates: " + sumCombo.size() + ", ");
//					    System.out.println("Time taken: " + (System.nanoTime() - start));
//					} catch (Exception e) {
//						System.out.println("Error in executing query");
//					}
//				}
//			}
//		}	
	}
	

}