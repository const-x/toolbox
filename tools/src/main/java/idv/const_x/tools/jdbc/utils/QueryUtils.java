package idv.const_x.tools.jdbc.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryUtils {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Map<String, Object>> query(String sql,
			Connection connnection) throws SQLException {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		PreparedStatement statement = connnection.prepareStatement(sql);
		ResultSet set = statement.executeQuery();
		ResultSetMetaData rsm = set.getMetaData();
		int col = rsm.getColumnCount();
		String[] colNames = new String[col];
		Class[] types = new Class[col];
		for (int i = 0; i < col; i++) {
			colNames[i] = rsm.getColumnLabel(i + 1);
			try {
				types[i] = Class.forName(rsm.getColumnClassName(i + 1));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		while (set.next()) {
			Map<String, Object> line = new HashMap<String, Object>();
			for (int i = 0; i < types.length; i++) {
				Class type = types[i];
				String name = colNames[i];
				if("java.math.BigInteger".equalsIgnoreCase(type.getName())){
					line.put(name, set.getLong(name));
				}else{
					try {
						line.put(name, set.getObject(name, type));
					}catch (SQLFeatureNotSupportedException e){
						line.put(name, set.getObject(name));
					}

				}
			}
			result.add(line);
		}
		return result;
	}
	
	public static void update(Connection conn,String  sql,Object ... values) throws SQLException {
		    PreparedStatement ps = conn.prepareStatement(sql);
		    if(values != null){
		    	for (int i = 0; i < values.length; i++) {
					Object value = values[i];
					if(value == null){
						
					}else{
						if(value instanceof Integer){
							ps.setInt(i, (Integer)value);
						}else if(value instanceof Double){
							ps.setDouble(i, (Double)value);
						}else if(value instanceof Long){
							ps.setLong(i, (Long)value);
						}else{
							ps.setString(i, value.toString());
						}	
					}
				}
		    }
			ps.executeUpdate();
	}

}
