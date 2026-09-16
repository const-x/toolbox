package idv.const_x.tools.jdbc.table;

import idv.const_x.tools.jdbc.DBType;

public class GeneratorFactory {

	
	public static ITableGenerator getTableGenerator(DBType type){
		if(type == DBType.MYSQL){
			return new MySqlTableGenerator();
		}
		if(type == DBType.ORACLE){
			return new OracleTableGenerator();
		}
		return null;
	}
}
