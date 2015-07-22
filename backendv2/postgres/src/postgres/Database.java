package postgres;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.postgresql.util.PSQLException;

/*
 * http://dev.mysql.com/doc/world-setup/en/
 * http://blog.manoharbhattarai.com.np/2013/04/03/execute-sql-file-from-command-line-in-postgresql/
 * http://stackoverflow.com/questions/3393961/how-to-import-existing-sql-files-in-postgresql-8-4
 * https://wiki.postgresql.org/wiki/Sample_Databases
 */
public class Database {

	private Connection connection = null; 
	private DatabaseMetaData baseMetaData = null;
	private String dbName;
	
	/*
	 * Initializes the PostgreSQL database into the Java VM.
	 * Checks for driver and establishes connection to Postgresql server/db
	 * 
	 * http://www.mkyong.com/jdbc/how-do-connect-to-postgresql-with-jdbc-driver-java/
	 * 
	 * Ali Finkelstein
	 * 9 July 2015
	 */
	public Database(String databaseName, String user, String password){
		//Testing for presence of PostgreSQL Driver
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		
		dbName = databaseName;
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://localhost/"+dbName,user,password);
		} catch (SQLException e){
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		
		try {
			baseMetaData = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("Unable to pull metadata.");
			e.printStackTrace();
			return;
		}

	}
	
	public Database(String databaseName){
		//Testing for presence of PostgreSQL Driver
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
		dbName = databaseName;
	}
	
	/*
	 * database constructor without defining the database name
	 */
	public Database(){
		//Testing for presence of PostgreSQL Driver
		System.out.println("Testing PostgreSQL driver");
		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("PostgreSQL Driver Found");
		} catch(ClassNotFoundException e){
			System.out.println("Driver not found");
			e.printStackTrace();
			return;
		}
	}
	
	/*
	 * Connect to DB given that dbName variable is not defined
	 * 
	 * @param databaseName - name of database to connect to 
	 * @param address - address of the database to connect to
	 * @param user - user to connect to db via
	 * @param password - password to user account to connect to database via
	 */
	public void connect(String databaseName, String address, String user, String password){
		dbName = databaseName;
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://"+address+"/"+dbName,user,password);
		} catch (SQLException e){
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		
		try {
			baseMetaData = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("Unable to pull metadata.");
			e.printStackTrace();
			return;
		}
	}
	
	/*
	 * Connect to DB given that dbName variable is defined
	 * 
	 * @param address - address of the database to connect to
	 * @param user - user to connect to db via
	 * @param password - password to user account to connect to database via
	 */
	public void connect(String address, String user, String password){
		//creating connection to PostgreSQL database	
		try{
			connection = DriverManager.getConnection("jdbc:postgresql://"+address+"/"+dbName,user,password);
		} catch (SQLException e){
			System.out.println("Connection Failed.");
			e.printStackTrace();
			return;
		}
		
		try {
			baseMetaData = connection.getMetaData();
		} catch (SQLException e) {
			System.out.println("Unable to pull metadata.");
			e.printStackTrace();
			return;
		}
	}
	
	/*
	 * Verifies that a connection was made to the database
	 * 
	 * Ali Finkelstein
	 * 15 July 2015
	 */
	public boolean verifyConnection(){
		return connection != null;
	}
	
	/*
	 * Verifies that MetaData object was created
	 * 
	 * Ali Finkelstein
	 * 15 July 2015
	 */	
	public boolean verifyMetaDataObj(){
		return baseMetaData != null;
	}
	
	/*
	 * Queries the Database
	 * 
	 * https://jdbc.postgresql.org/documentation/head/query.html
	 * 
	 * 	//test query: SELECT * FROM authors;
	 * Ali Finkelstein 
	 * 10 July 2015
	 */
	public ResultSet query(String query) throws SQLException{
		Statement queryStatement = connection.createStatement();
		ResultSet queryResult = queryStatement.executeQuery(query);
		queryResult.close();
		queryStatement.close();
		return queryResult;
	}
	
	/*
	 * Gets metadata for database and prints it out. 
	 * 
	 * Ali Finkelstein
	 * 10 July 2015
	 * @Deprecated
	 */
	@Deprecated
	public void metadataFetch() throws SQLException{
		
		//Get Type info for database
		ResultSet typeSet = baseMetaData.getTypeInfo();
		System.out.println("typeSet info ---------------- ");
		while(typeSet.next()){
			System.out.println(typeSet.getString(1));
		}
		
		System.out.println("////////////////////////");
		ResultSet schemaSet = baseMetaData.getSchemas();
		System.out.println("schema set info --------- ");
		while(schemaSet.next()){
			System.out.println(schemaSet.getString(1));
		}	
	}
	
	/*
	 * Get names of all possible accessible tables 
	 * 
	 * Ali Finkelstein
	 * 16 July 2015
	 * [authors_pkey, books_id_pkey, books_title_idx, customers_pkey, employees_pkey, pkey, publishers_pkey, schedules_pkey, shipments_ship_id_key, state_pkey, stock_pkey, subjects_pkey, text_idx, unique_publisher_idx, author_ids, book_ids, shipments_ship_id_seq, subject_ids, pg_aggregate_fnoid_index, pg_am_name_index, pg_am_oid_index, pg_amop_fam_strat_index, pg_amop_oid_index, pg_amop_opr_fam_index, pg_amproc_fam_proc_index, pg_amproc_oid_index, pg_attrdef_adrelid_adnum_index, pg_attrdef_oid_index, pg_attribute_relid_attnam_index, pg_attribute_relid_attnum_index, pg_auth_members_member_role_index, pg_auth_members_role_member_index, pg_authid_oid_index, pg_authid_rolname_index, pg_cast_oid_index, pg_cast_source_target_index, pg_class_oid_index, pg_class_relname_nsp_index, pg_class_tblspc_relfilenode_index, pg_collation_name_enc_nsp_index, pg_collation_oid_index, pg_constraint_conname_nsp_index, pg_constraint_conrelid_index, pg_constraint_contypid_index, pg_constraint_oid_index, pg_conversion_default_index, pg_conversion_name_nsp_index, pg_conversion_oid_index, pg_database_datname_index, pg_database_oid_index, pg_db_role_setting_databaseid_rol_index, pg_default_acl_oid_index, pg_default_acl_role_nsp_obj_index, pg_depend_depender_index, pg_depend_reference_index, pg_description_o_c_o_index, pg_enum_oid_index, pg_enum_typid_label_index, pg_enum_typid_sortorder_index, pg_event_trigger_evtname_index, pg_event_trigger_oid_index, pg_extension_name_index, pg_extension_oid_index, pg_foreign_data_wrapper_name_index, pg_foreign_data_wrapper_oid_index, pg_foreign_server_name_index, pg_foreign_server_oid_index, pg_foreign_table_relid_index, pg_index_indexrelid_index, pg_index_indrelid_index, pg_inherits_parent_index, pg_inherits_relid_seqno_index, pg_language_name_index, pg_language_oid_index, pg_largeobject_loid_pn_index, pg_largeobject_metadata_oid_index, pg_namespace_nspname_index, pg_namespace_oid_index, pg_opclass_am_name_nsp_index, pg_opclass_oid_index, pg_operator_oid_index, pg_operator_oprname_l_r_n_index, pg_opfamily_am_name_nsp_index, pg_opfamily_oid_index, pg_pltemplate_name_index, pg_proc_oid_index, pg_proc_proname_args_nsp_index, pg_range_rngtypid_index, pg_rewrite_oid_index, pg_rewrite_rel_rulename_index, pg_seclabel_object_index, pg_shdepend_depender_index, pg_shdepend_reference_index, pg_shdescription_o_c_index, pg_shseclabel_object_index, pg_statistic_relid_att_inh_index, pg_tablespace_oid_index, pg_tablespace_spcname_index, pg_trigger_oid_index, pg_trigger_tgconstraint_index, pg_trigger_tgrelid_tgname_index, pg_ts_config_cfgname_index, pg_ts_config_map_index, pg_ts_config_oid_index, pg_ts_dict_dictname_index, pg_ts_dict_oid_index, pg_ts_parser_oid_index, pg_ts_parser_prsname_index, pg_ts_template_oid_index, pg_ts_template_tmplname_index, pg_type_oid_index, pg_type_typname_nsp_index, pg_user_mapping_oid_index, pg_user_mapping_user_server_index, sql_features, sql_implementation_info, sql_languages, sql_packages, sql_parts, sql_sizing, sql_sizing_profiles, pg_aggregate, pg_am, pg_amop, pg_amproc, pg_attrdef, pg_attribute, pg_auth_members, pg_authid, pg_cast, pg_class, pg_collation, pg_constraint, pg_conversion, pg_database, pg_db_role_setting, pg_default_acl, pg_depend, pg_description, pg_enum, pg_event_trigger, pg_extension, pg_foreign_data_wrapper, pg_foreign_server, pg_foreign_table, pg_index, pg_inherits, pg_language, pg_largeobject, pg_largeobject_metadata, pg_namespace, pg_opclass, pg_operator, pg_opfamily, pg_pltemplate, pg_proc, pg_range, pg_rewrite, pg_seclabel, pg_shdepend, pg_shdescription, pg_shseclabel, pg_statistic, pg_tablespace, pg_trigger, pg_ts_config, pg_ts_config_map, pg_ts_dict, pg_ts_parser, pg_ts_template, pg_type, pg_user_mapping, pg_toast_11976_index, pg_toast_11981_index, pg_toast_11986_index, pg_toast_11991_index, pg_toast_11996_index, pg_toast_12001_index, pg_toast_12006_index, pg_toast_1255_index, pg_toast_16386_index, pg_toast_16407_index, pg_toast_16417_index, pg_toast_16425_index, pg_toast_16433_index, pg_toast_16439_index, pg_toast_16452_index, pg_toast_16461_index, pg_toast_16468_index, pg_toast_16478_index, pg_toast_16485_index, pg_toast_16508_index, pg_toast_16517_index, pg_toast_16526_index, pg_toast_16537_index, pg_toast_16544_index, pg_toast_16558_index, pg_toast_16567_index, pg_toast_16573_index, pg_toast_16580_index, pg_toast_2396_index, pg_toast_2604_index, pg_toast_2606_index, pg_toast_2609_index, pg_toast_2618_index, pg_toast_2619_index, pg_toast_2620_index, pg_toast_2964_index, pg_toast_3596_index, _pg_foreign_data_wrappers, _pg_foreign_servers, _pg_foreign_table_columns, _pg_foreign_tables, _pg_user_mappings, administrable_role_authorizations, applicable_roles, attributes, character_sets, check_constraint_routine_usage, check_constraints, collation_character_set_applicability, collations, column_domain_usage, column_options, column_privileges, column_udt_usage, columns, constraint_column_usage, constraint_table_usage, data_type_privileges, domain_constraints, domain_udt_usage, domains, element_types, enabled_roles, foreign_data_wrapper_options, foreign_data_wrappers, foreign_server_options, foreign_servers, foreign_table_options, foreign_tables, information_schema_catalog_name, key_column_usage, parameters, referential_constraints, role_column_grants, role_routine_grants, role_table_grants, role_udt_grants, role_usage_grants, routine_privileges, routines, schemata, sequences, table_constraints, table_privileges, tables, triggered_update_columns, triggers, udt_privileges, usage_privileges, user_defined_types, user_mapping_options, user_mappings, view_column_usage, view_routine_usage, view_table_usage, views, pg_available_extension_versions, pg_available_extensions, pg_cursors, pg_group, pg_indexes, pg_locks, pg_matviews, pg_prepared_statements, pg_prepared_xacts, pg_replication_slots, pg_roles, pg_rules, pg_seclabels, pg_settings, pg_shadow, pg_stat_activity, pg_stat_all_indexes, pg_stat_all_tables, pg_stat_archiver, pg_stat_bgwriter, pg_stat_database, pg_stat_database_conflicts, pg_stat_replication, pg_stat_sys_indexes, pg_stat_sys_tables, pg_stat_user_functions, pg_stat_user_indexes, pg_stat_user_tables, pg_stat_xact_all_tables, pg_stat_xact_sys_tables, pg_stat_xact_user_functions, pg_stat_xact_user_tables, pg_statio_all_indexes, pg_statio_all_sequences, pg_statio_all_tables, pg_statio_sys_indexes, pg_statio_sys_sequences, pg_statio_sys_tables, pg_statio_user_indexes, pg_statio_user_sequences, pg_statio_user_tables, pg_stats, pg_tables, pg_timezone_abbrevs, pg_timezone_names, pg_user, pg_user_mappings, pg_views, alternate_stock, authors, book_backup, book_queue, books, customers, daily_inventory, distinguished_authors, editions, employees, favorite_authors, favorite_books, money_example, my_list, numeric_values, publishers, schedules, shipments, states, stock, stock_backup, subjects, text_sorting, recent_shipments, stock_view, pg_toast_11976, pg_toast_11981, pg_toast_11986, pg_toast_11991, pg_toast_11996, pg_toast_12001, pg_toast_12006, pg_toast_1255, pg_toast_16386, pg_toast_16407, pg_toast_16417, pg_toast_16425, pg_toast_16433, pg_toast_16439, pg_toast_16452, pg_toast_16461, pg_toast_16468, pg_toast_16478, pg_toast_16485, pg_toast_16508, pg_toast_16517, pg_toast_16526, pg_toast_16537, pg_toast_16544, pg_toast_16558, pg_toast_16567, pg_toast_16573, pg_toast_16580, pg_toast_2396, pg_toast_2604, pg_toast_2606, pg_toast_2609, pg_toast_2618, pg_toast_2619, pg_toast_2620, pg_toast_2964, pg_toast_3596]
	 */
	@Deprecated
	public String[] accessableTables() throws NoDatabaseConnectionException{
		if (connection == null){
			throw new NoDatabaseConnectionException("No database found to connect");
		}
		
		ArrayList<String> tableArrList = new ArrayList<String>();

		//gotta check metadata connection too. eventually.. i should write a method for this. 
		try {
			ResultSet tableResultSet = baseMetaData.getTables(null,null,"%",null);
			while (tableResultSet.next()){
				tableArrList.add(tableResultSet.getString(3));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tableArrList.toArray(new String[tableArrList.size()]);
	}
	
	/*
	 * Because I dont know what a schema is. 
	 * Ali Finkelstein
	 */
	public String getSchema() throws SQLException{
		return connection.getSchema();
	}
	
	/*
	 * Get total number of columns
	 * 
	 * @param table - the table from the database you want to get information from
	 * @Return a hash map with the key being the name of the column and the value being the column type
	 * Ali Finkelstein 
	 * 15 July 2015
	 */
	public Map<String,String> getColumnAttribute(String tableName) throws NoDatabaseConnectionException, SQLException{
		if (connection == null){
			throw new NoDatabaseConnectionException("No database found to connect");
		}
		ResultSet rs = baseMetaData.getColumns(null,null,tableName,null);
		HashMap<String,String> tableColumnAttrs = new HashMap<String,String>();
		
		while(rs.next()){
			String name = rs.getString("COLUMN_NAME");
			String type = rs.getString("TYPE_NAME");
			tableColumnAttrs.put(name,type);
		}
		return tableColumnAttrs;
	}
	
	/*
	 * Execute a query/statement for the SQL database, and return the resulting data
	 * 
	 * @param statement - SQL query string
	 * @return result set containing query results
	 * @throws SQLException upon failure of query/statement execution
	 */
	public ResultSet executeStatementWithResult(String statement) throws SQLException{
		Statement stmntToExecute = connection.createStatement();
		ResultSet statementResult = stmntToExecute.executeQuery(statement);
		stmntToExecute.close();
		return statementResult;	
	}
	
	/*
	 * Execute a query/statement for the SQL database
	 * 
	 * @param statement - SQL query string
	 * @throws SQLException upon failure of query/statement execution
	 */
	public void executeStatementNoResult(String statement) throws SQLException{
		Statement stmntToExecute = connection.createStatement();
		ResultSet statementResult = stmntToExecute.executeQuery(statement);
		stmntToExecute.close();
		statementResult.close();
	}
	
	/*
	 * Gets the row count for the given table
	 * @param tableName - name of the table in the database to analyze
	 * @throws SQLException if statement could not be created to allow querying of row count
	 * 
	 * Ali Finkelstein
	 * 16 July 2015
	 */
	public int getRowCount(String tableName) throws SQLException{
		Statement pgQuery = connection.createStatement();
		String queryStatement = "SELECT count(*) FROM "+tableName;
		ResultSet queryResult = pgQuery.executeQuery(queryStatement);
		return queryResult.getInt(1);
	}

	/*
	 * From Manasi
	 */
	public Float getVariance(String columnName, String tableName) throws SQLException {
		
		if (connection == null) {
			System.out.println("Connection null. Quit");
		}
			
		String sqlQuery = "SELECT var_pop(" + columnName + ") FROM " + tableName;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
		    rs = stmt.executeQuery(sqlQuery);
	
		    rs.next();
		 	return rs.getFloat(1);
		} catch (PSQLException e) {
			if (e.getMessage().contains("var_pop") && e.getMessage().contains("does not exist")) {
			 	return null;
			} else {
				System.out.println(e.getMessage());
			}
		}
		return null;
	}
	
	/*
	 * Getting the number of distinct values in columnName from tableName
	 * 
	 * @param tableName - table to look into
	 * @param columnName - name of the column
	 * 
	 * @throws SQLException is no connection can be made to the database
	 * 
	 * @return -1 if no values present
	 * @return int number of distinct values present
	 * Ali Finkelstein
	 * 16 July 2015
	 */
	public int getDistinctValueCount(String tableName, String columnName) throws SQLException{
		Statement pgQuery = connection.createStatement();
		String queryStatement = "SELECT COUNT(DISTINCT "+columnName+") FROM "+tableName;
		ResultSet pgResult = pgQuery.executeQuery(queryStatement);
		int count = -1;
		while(pgResult.next()){
			count = pgResult.getInt(1);
		}
		return count;
	}
	
	
	/*
	 * Gets name of all tables in the DB
	 * 
	 * @Throws SQLException if unable to connect to DB or get metadata information
	 * 20 July 2015
	 */
	public Set<String> getTables() throws SQLException{
		Set<String> containedTables = new HashSet<String>();
		String[] tableTypesArray = {"VIEW","TABLE","SEQUENCE"};
		ResultSet metadataTables = baseMetaData.getTables(null,null,"%",tableTypesArray);
		while (metadataTables.next()){
			containedTables.add(metadataTables.getString(3));
		}
		return containedTables;
	}
}