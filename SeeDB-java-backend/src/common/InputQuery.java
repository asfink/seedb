package common;

import java.util.Collections;
import java.util.List;

/**
 * Represents a SQL input query received by SeeDB
 * We assume that the input query is of the form
 * SELECT * FROM A, B, C WHERE ...;
 * Currently projection, limit, groupby, subqueries are not supported
 * 
 * @author manasi
 */
public class InputQuery {
	public String database;
	public String rawQuery;
	public String fromClause;
	public String whereClause;
	public List<String> tables;

	public boolean queriesSameTables(InputQuery q) {
		if (!q.database.equalsIgnoreCase(this.database)) return false;
		if (q.tables.size() != this.tables.size()) return false;
		Collections.sort(q.tables);
		Collections.sort(this.tables);
		for (int i = 0; i < this.tables.size(); i++) {
			if (!q.tables.get(i).equalsIgnoreCase(this.tables.get(i)))
				return false;
		}
		return true;
	}
	
	public boolean equals(Object o) {
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		else {
			InputQuery o_ = (InputQuery) o;
			return database.equalsIgnoreCase(o_.database) && 
					rawQuery.equalsIgnoreCase(o_.rawQuery) &&
					fromClause.equalsIgnoreCase(o_.fromClause) &&
					whereClause.equalsIgnoreCase(o_.whereClause);
		}
	}
	
	public static InputQuery getDefault() {
		try {
			return QueryParser.parse("select * from table_10_2_2_3_2_1 where measure1 < 10", "seedb_data");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static InputQuery deepCopy(InputQuery q) {
		InputQuery q1 = new InputQuery();
		q1.database = q.database;
		q1.rawQuery = q.rawQuery;
		q1.fromClause = q.fromClause;
		q1.whereClause = q.whereClause;
		q1.tables = Utils.deepCopyList(q.tables);
		return q1;
	}
}