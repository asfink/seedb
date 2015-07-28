package postgres;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class DBShared {
	private static int DISTINCT_DIMENSION_COUNT;

	/*
	 * Given a type and the number of distinct values of a column, a
	 * qualification for the SeeDB system is give
	 * 
	 * @param type the variable type according to SQL
	 * 
	 * @param distinctCount the number of distinct values in the column
	 * 
	 * @return a string of either "dimension", "measure", or "other" as assigned
	 */
	public String getSeeDBType(String type, int distinctCount) {
		if (distinctCount <= DISTINCT_DIMENSION_COUNT
				&& !type.equalsIgnoreCase("numeric")) {
			return "dimension";
		} else if (distinctCount <= DISTINCT_DIMENSION_COUNT
				&& type.equalsIgnoreCase("numeric")) {
			return "measure";
		}
		return "other";
	}

	/*
	 * Prints out the result of the result set
	 * 
	 * @param rs a ResultSet to print out
	 * 
	 * 26 July 2015
	 */
	public void printResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int colNumber = rsmd.getColumnCount();
		System.out.println(Integer.toString(colNumber));
		for (int i = 1; i <= colNumber; i++) {
			if (i > 1 && i <= colNumber)
				System.out.print(" - ");
			System.out.print(rsmd.getColumnName(i));
		}
		System.out.println("");
		while (rs.next()) {
			for (int i = 1; i <= colNumber; i++) {
				if (i > 1 && i <= colNumber)
					System.out.print(" - ");
				String val = rs.getString(i);
				System.out.print(val);
			}
			System.out.println("");
		}
	}

}
