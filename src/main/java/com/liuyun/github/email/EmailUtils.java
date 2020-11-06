package com.liuyun.github.email;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.Data;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

@Data
public class EmailUtils {

    /** 服务器主机*/
    private static String serverHost;
    /** 服务器端口*/
    private static String serverPort;
    /** 发送者*/
    private static Address fromAddress;
    /** 用户名*/
    private static String userName;
    /** 密码*/
    private static String password;

    static {
        Properties prop = new Properties();
        InputStream instream = ClassLoader.getSystemResourceAsStream("email.properties");
        try {
            prop.load(instream);
            serverHost = prop.getProperty("serverHost");
            serverPort = prop.getProperty("serverPort");
            fromAddress = new InternetAddress(prop.getProperty("fromAddress"));
            userName = prop.getProperty("userName");
            password = prop.getProperty("password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文本信息
     * @return
     */
    public static boolean sendTextMail(Email email) {
        try{
            Message message = buildMessage(email);
            //设置邮件内容
            message.setText(email.getContent());
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送文本信息
     * @return
     */
    public static boolean sendTextMail(String subject, String msg, String... receivers) {
        try{
            Message message = buildMessage(subject, receivers);
            //设置邮件内容
            message.setText(msg);
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送HTML信息
     * @return
     */
    public static boolean sendHtmlMail(Email email) {
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
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送HTML信息
     * @return
     */
    public static boolean sendHtmlMail(String subject, String msg, String... receivers) {
        try{
            Message message = buildMessage(subject, receivers);
            Multipart multipart = new MimeMultipart();

            //设置HTML文本内容
            BodyPart html = new MimeBodyPart();
            html.setContent(msg, "text/html; charset=utf-8");
            multipart.addBodyPart(html);
            message.setContent(multipart);
            message.saveChanges();
            Transport.send(message);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 构建消息
     * @param email
     * @return
     */
    private static Message buildMessage(Email email) {
        try {
            //设置身份验证
            MailAuthenticator authenticator = new MailAuthenticator(userName, password);
            Session session = Session.getDefaultInstance(getProperties(), authenticator);
            Message message = new MimeMessage(session);
            //设置发送者地址
            message.setFrom(fromAddress);
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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建消息
     * @param subject
     * @param receivers
     * @return
     */
    private static Message buildMessage(String subject, String... receivers) {
        try {
            //设置身份验证
            MailAuthenticator authenticator = new MailAuthenticator(userName, password);
            Session session = Session.getDefaultInstance(getProperties(), authenticator);
            Message message = new MimeMessage(session);
            //设置发送者地址
            message.setFrom(fromAddress);
            //设置接收者地址
            message.setRecipients(Message.RecipientType.TO, buildAddress(receivers));
            //设置主题
            message.setSubject(subject);
            //设置发送时间
            message.setSentDate(new Date());
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建地址
     * @param receivers
     * @return
     */
    private static Address[] buildAddress(String... receivers) {
        try {
            Address[] toAddress = new Address[receivers.length];
            for (int i = 0; i < receivers.length; i++) {
                toAddress[i] = new InternetAddress(receivers[i]);
            }
            return toAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取配置属性
     * @return
     * @throws GeneralSecurityException
     */
    private static Properties getProperties() throws GeneralSecurityException {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", serverHost);
        properties.put("mail.smtp.port", serverPort);
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
    public static class MailAuthenticator extends Authenticator {
        private String userName = null;
        private String password = null;

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
