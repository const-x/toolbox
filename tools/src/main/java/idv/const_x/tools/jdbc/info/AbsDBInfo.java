package idv.const_x.tools.jdbc.info;

import idv.const_x.tools.jdbc.DBType;

/**
 * 数据库连接方案
 * 
 * @since 6.3
 * @version 2014-03-05 14:40:17
 * @author const.x
 */
public abstract class AbsDBInfo {

	protected String host;

	protected String passwd;

	protected String port;

	protected String server;

	protected String user;

	public AbsDBInfo(String host, String port, String server, String user, String passwd) {
		this.host = host;
		this.port = port;
		this.server = server;
		this.user = user;
		this.passwd = passwd;
	}

	public AbsDBInfo() {

	}

	public abstract String getDriver();

	public String getHost() {
		return this.host;
	}

	public String getPort() {
		return this.port;
	}

	public String getServer() {
		return this.server;
	}

	public String getUser() {
		return this.user;
	}

	public String getPasswd() {
		return this.passwd;
	}

	public abstract DBType getType();

	public abstract String getUrl();

	@Override
	public String toString() {
		return getUrl();
	}
	
	

}
