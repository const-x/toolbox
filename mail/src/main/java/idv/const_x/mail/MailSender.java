package idv.const_x.mail;


import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.util.Date;
import java.util.Map.Entry;

/**
 * 邮件发送器
 */
public class MailSender {


	/**
	 * 发送邮件
	 *
	 * @param mail 待发送的邮件的信息
	 */
	public static boolean  sendMail(Mail mail,String from, MailService service) {
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(service.getProperties(), service.getAuthenticator());
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 是否需要已读回执
			if (mail.isNotification()) {
				mailMessage.addHeader("Disposition-Notification-To", from);
			}
			// 判断邮件是否已经成功发送（时而不起作用）
			if (mail.isReceipt()) {
				mailMessage.addHeader("Return-Receipt-To", from);
			}
			// 设置重要程度 [High|Normal|Low]
			mailMessage.addHeader("Priority", mail.getPriority());
			// 设置有效期限
			if (mail.getExpiryDate() != null) {
				mailMessage.addHeader("Expiry-Date", mail.getExpiryDate());
			}
			// 设置发送账号姓名
			if (mail.getFullName() != null) {
				mailMessage.addHeader("Full-Name", mail.getFullName());
			}
			if(mail.isHtml()){
				mailMessage.addHeader("Content-Type",  "text/html; charset=" + mail.getEncoding());
			}else{
				mailMessage.addHeader("Content-Type",  "text/plain; charset=" + mail.getEncoding());
			}
			if(mail.getHeadrs() != null){
				for(Entry<String, String> entry : mail.getHeadrs().entrySet()){
					mailMessage.addHeader(entry.getKey(), entry.getValue());
				}
			}

			// 设置邮件消息的发送者
			mailMessage.setFrom(new InternetAddress(from));

			// 设置邮件消息的回复地址
			if (mail.getReplyTo() != null && mail.getReplyTo().size() > 0) {
				Address[] replys = new Address[mail.getReplyTo().size()];
				for (int i = 0; i < mail.getReplyTo().size(); i++) {
					Address replyTo = new InternetAddress(mail.getReplyTo().get(i));
					replys[i] = replyTo;
				}
				mailMessage.setReplyTo(replys);
			}

			// 创建邮件的接收者地址，并设置到邮件消息中
			for (String to : mail.getTO()) {
				Address add = new InternetAddress(to);
				mailMessage.addRecipient(Message.RecipientType.TO, add);
			}
			for (String cc : mail.getCC()) {
				Address add = new InternetAddress(cc);
				mailMessage.addRecipient(Message.RecipientType.CC, add);
			}
			for (String bcc : mail.getBCC()) {
				Address add = new InternetAddress(bcc);
				mailMessage.addRecipient(Message.RecipientType.BCC, add);
			}

			// 设置邮件消息的主题
			mailMessage.setSubject(mail.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());

			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			BodyPart text = new MimeBodyPart();
			if (mail.isHtml()) {
				// 设置HTML内容
				text.setContent(mail.getContent(), "text/html; charset=" + mail.getEncoding());
			} else {
				// 设置邮件消息的主要内容
				text.setText(mail.getContent());
			}
			mainPart.addBodyPart(text);
			// 添加多个附件
			if (mail.getArchieves() != null ) {
				for (int index = 0; index < mail.getArchieves().size(); index++) {
					MimeBodyPart mailArchieve = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(mail.getArchieves().get(index));
					mailArchieve.setDataHandler(new DataHandler(fds));
					mailArchieve.setFileName(MimeUtility.encodeText(fds.getName(), mail.getEncoding(), null));
					mainPart.addBodyPart(mailArchieve);
				}
			}
			mailMessage.setContent(mainPart);

			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

}
