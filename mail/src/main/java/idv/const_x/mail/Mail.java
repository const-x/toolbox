package idv.const_x.mail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mail {

	public final static String PRIORITY_HIGH = "High";
	
	public final static String PRIORITY_NORMAL = "Normal";
	
	public final static String PRIORITY_LOW = "Low";
	
	
	
	// 主题
	private String subject = null;
	// 内容
	private String content = null;
	// 是否html内容
	private boolean isHtml = false;
	// 发送账号姓名
	private String fullName = null;
	// 发送至（非必须）
	private List<String> TO = new ArrayList<String>();
	// 抄送至（非必须）
	private List<String> CC = new ArrayList<String>();
	// 密送至（非必须）
	private List<String> BCC = new ArrayList<String>();
	// 回复地址（非必须）
	private List<String> replyTo = new ArrayList<String>();
	// 是否需要回执（非必须）
	private boolean notification = false;
	// 跟踪是否发送成功至（非必须）
	private boolean receipt = false;
	// 重要程度[High|Normal|Low]（非必须）
	private String priority = "Normal";
	// 有效期（非必须）
	private Date expiryDate = null;
	// 附件
	private List<String> archieves = new ArrayList<String>();
	// 编码（默认UTF-8）
	private String encoding = "UTF-8";
	//添加邮件头部内容
	private Map<String,String> headrs;
	
	/**
	 * 添加邮件头部内容
	 * 
	 * @param key
	 * @param value
	 *
	 * @author const.x
	 * @createDate 2014年11月18日
	 */
	public void addHeader(String key,String value){
		if(headrs == null){
			headrs = new HashMap<String,String>();
		}
		headrs.put(key, value);
	}
	
	/**
	 * @return 邮件头部内容
	 *
	 * @author const.x
	 * @createDate 2014年11月18日
	 */
	public Map<String, String> getHeadrs() {
		return headrs;
	}

	/**
	 * @param 添加邮件头部内容
	 *
	 * @author const.x
	 * @createDate 2014年11月18日
	 */
	public void setHeadrs(Map<String, String> headrs) {
		this.headrs = headrs;
	}

	/**
	 * 获取主题
	 * 
	 * @return String 主题
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * 设置主题
	 * 
	 * @param value 主题
	 */
	public void setSubject(String value) {
		this.subject = value;
	}

	/**
	 * 获取内容
	 * 
	 * @return String 内容
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * 设置内容
	 * 
	 * @param value 内容
	 */
	public void setContent(String value) {
		this.content = value;
	}

	/**
	 * 是否是否html内容
	 * 
	 * @return boolean 是否html内容
	 */
	public boolean isHtml() {
		return this.isHtml;
	}

	/**
	 * 设置是否html内容
	 * 
	 * @param value 是否html内容
	 */
	public void setIsHtml(boolean value) {
		this.isHtml = value;
	}


	
	/**
	 * 获取发送账号姓名
	 * 
	 * @return
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * 设置发送账号姓名
	 * 
	 * @param fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * 获取发送至（非必须）
	 * 
	 * @return List<String> 发送至（非必须）
	 */
	public List<String> getTO() {
		return this.TO;
	}

	/**
	 * 添加发送至（非必须）
	 * 
	 * @param value 发送至（非必须）
	 */
	public void addTO(String value) {
		this.TO.add(value);
	}

	/**
	 * 设置发送至（非必须）
	 * 
	 * @param value 发送至（非必须）
	 */
	public void setTO(List<String> value) {
		this.TO = value;
	}

	/**
	 * 获取抄送至（非必须）
	 * 
	 * @return List<String> 抄送至（非必须）
	 */
	public List<String> getCC() {
		return this.CC;
	}

	/**
	 * 设置抄送至（非必须）
	 * 
	 * @param value 抄送至（非必须）
	 */
	public void setCC(List<String> value) {
		this.CC = value;
	}

	/**
	 * 添加抄送至（非必须）
	 * 
	 * @param value 抄送至（非必须）
	 */
	public void addCC(String value) {
		this.CC.add(value);
	}

	/**
	 * 设置密送至（非必须）
	 * 
	 * @param value 密送至（非必须）
	 */
	public void setBCC(List<String> value) {
		this.BCC = value;
	}

	/**
	 * 添加密送至（非必须）
	 * 
	 * @return List<String> 密送至（非必须）
	 */
	public List<String> getBCC() {
		return this.BCC;
	}

	/**
	 * 设置密送至（非必须）
	 * 
	 * @param value 密送至（非必须）
	 */
	public void setBCC(String value) {
		this.BCC.add(value);
	}

	/**
	 * 获取回复地址（非必须）
	 * 
	 * @return List<String> 回复地址（非必须）
	 */
	public List<String> getReplyTo() {
		return this.replyTo;
	}

	/**
	 * 设置回复地址（非必须）
	 * 
	 * @param value 回复地址（非必须）
	 */
	public void setReplyTo(List<String> value) {
		this.replyTo = value;
	}

	/**
	 * 添加回复至（非必须）
	 * 
	 * @param value 回复至（非必须）
	 */
	public void addReplyTo(String value) {
		this.replyTo.add(value);
	}

	/**
	 * 是否是否需要回执（非必须）
	 * 
	 * @return boolean 是否需要回执（非必须）
	 */
	public boolean isNotification() {
		return this.notification;
	}

	/**
	 * 设置是否需要回执（非必须）
	 * 
	 * @param value 是否需要回执（非必须）
	 */
	public void setNotification(boolean value) {
		this.notification = value;
	}

	/**
	 * 是否跟踪是否发送成功至（非必须）
	 * 
	 * @return boolean 跟踪是否发送成功至（非必须）
	 */
	public boolean isReceipt() {
		return this.receipt;
	}

	/**
	 * 设置跟踪是否发送成功至（非必须）
	 * 
	 * @param value 跟踪是否发送成功至（非必须）
	 */
	public void setReceipt(boolean value) {
		this.receipt = value;
	}

	/**
	 * 获取重要程度[High|Normal|Low]（非必须）
	 * 
	 * @return String 重要程度[High|Normal|Low]（非必须）
	 */
	public String getPriority() {
		return this.priority;
	}

	/**
	 * 设置重要程度[High|Normal|Low]（非必须）
	 * 
	 * @param value 重要程度[High|Normal|Low]（非必须）
	 */
	public void setPriority(String value) {
		this.priority = value;
	}

	/**
	 * 获取有效期（非必须）
	 * 
	 * @return String 有效期（非必须）
	 */
	public String getExpiryDate() {
		if(this.expiryDate == null){
			return null;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		return df.format(this.expiryDate);
	}

	/**
	 * 设置有效期（非必须）
	 * 
	 * @param value 有效期（非必须）
	 */
	public void setExpiryDate(Date value) {
		this.expiryDate = value;
	}

	/**
	 * 获取附件
	 * 
	 * @return List<String> 附件
	 */
	public List<String> getArchieves() {
		return this.archieves;
	}

	/**
	 * 设置附件
	 * 
	 * @param value 附件
	 */
	public void setArchieves(List<String> files) {
		this.archieves = files;
	}

	/**
	 * 添加附件
	 * 
	 * @param value 附件
	 */
	public void addArchieves(String file) {
		this.archieves.add(file);
	}

	/**
	 * 获取编码（默认UTF-8）
	 * 
	 * @return String 编码（默认UTF-8）
	 */
	public String getEncoding() {
		return this.encoding;
	}

	/**
	 * 设置编码（默认UTF-8）
	 * 
	 * @param value 编码（默认UTF-8）
	 */
	public void setEncoding(String value) {
		this.encoding = value;
	}

}
