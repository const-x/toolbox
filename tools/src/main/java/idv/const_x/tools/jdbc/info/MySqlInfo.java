package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;


/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:38
 * @author const.x
 */
public class MySqlInfo extends AbsDBInfo {

  public MySqlInfo(String host, String port, String server, String user,
                   String passwd) {
    super(host, port, server, user, passwd);
  }
  
  private String characterEncoding = "utf-8";
  
  

public String getCharacterEncoding() {
	return characterEncoding;
}


public void setCharacterEncoding(String characterEncoding) {
	this.characterEncoding = characterEncoding;
}

@Override
  public String getDriver() {
    return "com.mysql.jdbc.Driver";
  }

  @Override
  public DBType getType() {
    return DBType.MYSQL;
  }

  public String getUrl() {
	if(characterEncoding != null){
		return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.server + "?useUnicode=true&characterEncoding=" + characterEncoding;
	}  
    return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.server ;
  }
}
