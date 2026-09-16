package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;


/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:38
 * @author const.x
 */
public class SqliteInfo extends AbsDBInfo {

    String path = null;

  public SqliteInfo(String path) {
      super();
      this.path = path;
  }
  

  @Override
  public String getDriver() {
    return "org.sqlite.JDBC";
  }

  @Override
  public DBType getType() {
    return DBType.SQLITE;
  }

  @Override
  public String getUrl() {
    return "jdbc:sqlite:" + path;
  }
}
