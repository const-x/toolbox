package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;


/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:43
 * @author const.x
 */
public class OracleDBInfo extends AbsDBInfo {

  public OracleDBInfo(String host, String port, String server, String user,
                      String passwd) {
    super(host, port, server, user, passwd);

  }

  @Override
  public String getDriver() {
    return "oracle.jdbc.driver.OracleDriver";
  }

  @Override
  public DBType getType() {
    return DBType.ORACLE;
  }

  @Override
  public String getUrl() {
    return "jdbc:oracle:thin:@" + this.host + ":" + this.port + "/"
        + this.server;
  }
}
