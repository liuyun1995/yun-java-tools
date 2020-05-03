package com.liuyun.github.alarmpush;

import com.liuyun.github.utils.ThreadPoolRepo;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;
import static com.liuyun.github.alarmpush.AlarmConfig.EmailConfig;

/**
 * @Author: liuyun18
 * @Date: 2018/10/22 下午4:16
 */
@Data
@Slf4j
public class EmailPusher {

    private EmailConfig emailConfig;
    private static EmailPusher emailPusher;

    /**
     * 构造器
     * @param emailConfig
     */
    public EmailPusher(EmailConfig emailConfig) {
        this.emailConfig = emailConfig;
    }

    /**
     * 获取实例方法
     * @param emailConfig
     * @return
     */
    public static EmailPusher instance(EmailConfig emailConfig) {
        if(emailPusher == null) {
            emailPusher = new EmailPusher(emailConfig);
        }
        return emailPusher;
    }

    /**
     * 发送文本消息
     * @param msg
     * @param emails
     */
    public void sendTextMail(String msg, String... emails) {
        Email email = new Email();
        email.setTOAddress(emails);
        email.setSubject("告警邮件");
        email.setContent(msg);
        sendTextMail(email);
    }

    /**
     * 发送文本信息
     * @param email
     */
    public void sendTextMail(Email email) {
        ThreadPoolRepo.getThreadPool().execute(() -> {
            try{
                Message message = buildMessage(email);
                message.setText(email.getContent());
                Transport.send(message);
            } catch (Exception e){
                log.error("发送邮件失败", e);
            }
        });
    }

    /**
     * 发送HTML消息
     * @param msg
     * @param emails
     */
    public void sendHtmlMail(String msg, String... emails) {
        Email email = new Email();
        email.setTOAddress(emails);
        email.setSubject("告警邮件");
        email.setContent(msg);
        sendHtmlMail(email);
    }

    /**
     * 发送HTML信息
     * @return
     */
    public void sendHtmlMail(Email email) {
        ThreadPoolRepo.getThreadPool().execute(() -> {
            try{
                Message message = buildMessage(email);
                Multipart multipart = new MimeMultipart();

                //设置HTML文本内容
                BodyPart html = new MimeBodyPart();
                html.setContent(email.getContent(), "text/html;charset=utf-8");
                multipart.addBodyPart(html);

                //设置附件
                if(email.getAttachments() != null && !email.getAttachments().isEmpty()) {
                    for (File file : email.getAttachments()) {
                        DataSource source = new FileDataSource(file);
                        BodyPart attachments = new MimeBodyPart();
                        attachments.setDataHandler(new DataHandler(source));
                        multipart.addBodyPart(attachments);
                    }
                }
                message.setContent(multipart);
                message.saveChanges();
                Transport.send(message);
            } catch (Exception e){
                log.error("发送邮件失败", e);
            }
        });
    }

    /**
     * 构建消息
     * @param email
     * @return
     */
    private Message buildMessage(Email email) {
        try {
            //设置身份验证
            MailAuthenticator authenticator = new MailAuthenticator(emailConfig.getUserName(), emailConfig.getPassword());
            Session session = Session.getDefaultInstance(getProperties(), authenticator);
            Message message = new MimeMessage(session);
            //设置发送者地址
            message.setFrom(new InternetAddress(emailConfig.getFromAddress()));
            //设置接收者地址
            message.setRecipients(Message.RecipientType.TO, email.getTOAddress());
            //设置抄送者地址
            if(email.getCCAddress() != null && email.getCCAddress().length > 0) {
                message.setRecipients(Message.RecipientType.CC, email.getCCAddress());
            }
            //设置密抄送者地址
            if(email.getBCCAddress() != null && email.getBCCAddress().length > 0) {
                message.setRecipients(Message.RecipientType.BCC, email.getBCCAddress());
            }
            //设置主题
            message.setSubject(email.getSubject());
            //设置发送时间
            message.setSentDate(new Date());
            return message;
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return null;
        }
    }

    /**
     * 获取配置属性
     * @return
     * @throws GeneralSecurityException
     */
    private Properties getProperties() throws GeneralSecurityException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", emailConfig.getServerHost());
        properties.put("mail.smtp.port", emailConfig.getServerPort());
        properties.put("mail.smtp.auth", true);
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        return properties;
    }

    /**
     * 权限验证类
     */
    public class MailAuthenticator extends Authenticator {
        private String userName;
        private String password;

        public MailAuthenticator(String userName, String password) {
            this.userName = userName;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }

}
