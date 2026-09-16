package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;


/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:48
 * @author const.x
 */
public class SqlServerInfo extends AbsDBInfo {

  public SqlServerInfo(String host, String port, String server, String user,
                       String passwd) {
    super(host, port, server, user, passwd);

  }

  @Override
  public String getDriver() {
    return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
  }

  @Override
  public DBType getType() {
    return DBType.SQLSERVER;
  }

  @Override
  public String getUrl() {
    return "jdbc:sqlserver://" + this.host + ":" + this.port
        + ";DatabaseName=" + this.server;
  }
}
