package idv.const_x.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

public class MailService {

	/**
	 * smtp 用于发送邮件
	 */
	public final static String SERVICE_TYPE_SMTP = "smtp";
	/**
	 * pop3 用于接收邮件
	 */
	public final static String SERVICE_TYPE_POP3 = "pop3";



	private final String host;
	private final String port;
	private final String type;
	private String user;
	private String passwd;

	public MailService(String host,String port,String type,String user,String passwd){
		this.host = host;
		this.port = port;
		this.type = type;
		this.user = user;
		this.passwd = passwd;
	}


    public MailService(String host,String port,String type){
    	this.host = host;
		this.port = port;
		this.type = type;
	}

	public  Properties getProperties() {
		Properties p = System.getProperties();
		p.put("mail."+type+".host", host);
		p.put("mail."+type+".port", port);
		p.put("mail."+type+".auth", user != null);
		return p;
	}

	public  Authenticator getAuthenticator() {
		Authenticator authenticator = null;
		if(user != null){
			authenticator = new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					PasswordAuthentication pas = new PasswordAuthentication(user, passwd);
					return pas;
			}};
		}
		return authenticator;
	}



}
