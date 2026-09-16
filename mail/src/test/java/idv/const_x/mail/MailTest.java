package idv.const_x.mail;

/**
 * @Description <pre>
 *
 * </pre>
 * @Author const.x
 * @Date 2024-02-03
 */
public class MailTest {


    public static void main(String[] args){
        Mail mail = new Mail();

        mail.setSubject("test232");
        mail.setContent("content");
//		mail.addArchieves("F://demo//sql//demo//component_init.sql");

        mail.setFullName("测试者");
        mail.addTO("aaa@qq.com");

        mail.setNotification(true);
        mail.setPriority(Mail.PRIORITY_HIGH);
        mail.setReceipt(true);

        MailService service = new MailService("smtp.yeah.net", "25",MailService.SERVICE_TYPE_SMTP, "user", "password");
        MailSender.sendMail(mail,"111@164.net", service);
    }

}
