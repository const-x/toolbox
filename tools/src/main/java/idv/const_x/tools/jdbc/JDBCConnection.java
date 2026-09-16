package idv.const_x.tools.jdbc;

import idv.const_x.tools.jdbc.info.AbsDBInfo;
import idv.const_x.tools.jdbc.info.SqliteInfo;
import idv.const_x.tools.jdbc.utils.QueryUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * jdbc数据库连接
 * 
 * @since 6.3
 * @version 2013-05-09 19:47:31
 * @author const.x
 */
public class JDBCConnection {

	public static void main(String[] args) throws SQLException {
		SqliteInfo info = new SqliteInfo(System.getProperty("user.home") + "/Library/Application Support/Google/Chrome/Default/Cookies");
		Connection connection = getConnection(info);
		try {
			List<Map<String, Object>> res = QueryUtils.query("select host_key,path from cookies", connection);
			for (Map<String, Object> map : res) {
				System.out.println(map.get("host_key") + ":" + map.get("path"));
			}
		}finally {
			connection.close();
		}
	}

	public static Connection getConnection(AbsDBInfo db) {
		Connection conn = null;
		try {
			Class.forName(db.getDriver());
			if (db.getType() == DBType.SQLITE) {
				conn = DriverManager.getConnection(db.getUrl());
			}else {
				conn = DriverManager.getConnection(db.getUrl(), db.getUser(), db.getPasswd());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return conn;
	}
	
	
	

}
