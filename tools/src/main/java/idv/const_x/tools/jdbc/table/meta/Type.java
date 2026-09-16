package idv.const_x.tools.jdbc.table.meta;

public interface Type {

	
	boolean isPremiryKey();
	
	String getJavaType();
	
	String getJdbcType();
	
	int getLength();
	
	int getscale();
	
	
	boolean isSimpleType();
	
}
