package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;


/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:32
 * @author const.x
 */
public class DB2Info extends AbsDBInfo {

  public DB2Info(String host, String port, String server, String user,
                 String passwd) {
    super(host, port, server, user, passwd);

  }

  @Override
  public String getDriver() {
    return "com.ibm.db2.jcc.DB2Driver";
  }

  @Override
  public DBType getType() {
    return DBType.DB2;
  }

  @Override
  public String getUrl() {
    return "jdbc:db2://" + this.host + ":" + this.port + "/" + this.server;
  }
}
