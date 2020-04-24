package com.xxx.api.apiscaffold.util;

import com.xxx.api.apiscaffold.config.CustomConfig;
import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2018/8/16 16:46
 * @extra code change the world
 * @description
 */
public class MailTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailTool.class);

    private static MailTool mailTool;

    private CustomConfig.Email email;

    private MailTool() {
    }

    private MailTool(CustomConfig.Email email) {
        this.email = email;
    }

    public static MailTool getInstance(CustomConfig.Email email) {
        if (email == null) {
            LOGGER.warn("构造函数参数为空，发送该邮件失败");
            return null;
        }
        if (mailTool == null) {
            synchronized (MailTool.class) {
                if (mailTool == null) {
                    // 由于Java会对指令进行重排序，因此有可能会出现 mailTool 不为 null，但是构造函数还没有执行完的情况，这时如果使用这个对象就有可能出现不可预期的情况，崩溃是一种可能。
                    // 通过临时变量赋值，确保初始化完成之后才赋值给 mailTool
                    MailTool temp = new MailTool(email);
                    mailTool = temp;
                }
            }
        }

        return mailTool;
    }

    public boolean sendHtmlEmail(String to, String content, String subject) {
        LOGGER.info("ready to send html email.");
        if (StringUtils.isBlank(to)) {
            return false;
        }
        LOGGER.info("send html email to {}, subject is {}.", to, subject);

        return sendHtmlEmail(Arrays.asList(new String[]{to}), content, subject);
    }

    public boolean sendHtmlEmail(List<String> toList, String content, String subject) {
        if (toList == null || toList.isEmpty() || StringUtils.isBlank(content)) {
            LOGGER.warn("邮件接收人，邮件正文不能为空");
            return false;
        }

        boolean result;
        try {
            LOGGER.info("正在发送邮件: mail to {} users, subject is {}", toList.size(), subject);
            result = sendEmail(content, subject, toList, null, null, null, true);
        } catch (Exception e) {
            LOGGER.error("邮件发送失败");
            LOGGER.error(e.getMessage(), e);
            result = false;
        }

        return result;
    }

    private boolean sendEmail(String content, String subject, List<String> to, List<String> cc, List<String> scc, List<File> attaches, boolean html) throws Exception {
        Properties props = new Properties();
        // 设置 smtp 属性
        setProps(props);

        String authUser = this.email.getUsername();
        String authPassword = this.email.getPassword();

        Authenticator authenticator = new MyAuthenticator(authUser, authPassword);
        Session session = Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(authUser));
        if (to != null && to.size() > 0) {
            for (String addr : to) {
                InternetAddress toEmail = new InternetAddress(addr);
                message.addRecipient(Message.RecipientType.TO, toEmail);
            }
        }
        //加入抄送
        if(cc != null && !cc.isEmpty()){
            for(String mailcc : cc){
                LOGGER.info("cc -- {}", mailcc);
                InternetAddress ccaddr = new InternetAddress(MimeUtility.encodeText(mailcc));
                message.addRecipient(Message.RecipientType.CC, ccaddr);
            }
        }
        //加入秘送
        if(scc != null && !scc.isEmpty()){
            for(String bcc : scc){
                LOGGER.info("bcc -- {}", bcc);
                InternetAddress bccaddr = new InternetAddress(MimeUtility.encodeText(bcc));
                message.addRecipient(Message.RecipientType.BCC, bccaddr);
            }
        }

        Multipart contentPart = new MimeMultipart();
        if (StringUtils.isNotBlank(content)) {
            BodyPart bodyPart = new MimeBodyPart();
            if(html) {
                bodyPart.setContent(content, "text/html;charset=UTF-8");
            } else {
                bodyPart.setContent(content, "text/plain;charset=UTF-8");
            }
            contentPart.addBodyPart(bodyPart);
        }

        //添加邮件附件
        if(attaches != null && !attaches.isEmpty()){
            for (File attach : attaches) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attach);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                LOGGER.info("附件名称 - {}", attach.getName());
                attachmentBodyPart.setFileName(MimeUtility.encodeText(attach.getName()));
                contentPart.addBodyPart(attachmentBodyPart);
            }
        }
        message.setContent(contentPart);

        if (StringUtils.isBlank(subject)) {
            String[] lines=content.split("\r\n");
            if (lines.length==1) {
                lines=content.split("\n");
            }
            subject=lines[0];
        }
        LOGGER.info("subject-code: {}", subject);
        try {
            subject = URLDecoder.decode(subject, "UTF-8");
            LOGGER.info("subject-decoded: {}", subject);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("subject decode failed");
            LOGGER.error(e.getMessage(), e);
        }
        try {
            message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        message.addHeader("X-Mailer", "notiserver");
        message.setSentDate(new Date());

        message.saveChanges();

        boolean mailResult = true;
        SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
        try {
            transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e){
            mailResult = false;
            LOGGER.error("send mail error. to: {}, subject: {}", to, subject);
        }
        LOGGER.info("send mail return code: {}", transport.getLastReturnCode());
        LOGGER.info("send mail to: {}, subject: {}, return: {}", to, subject, transport.getReportSuccess());
        String result = transport.getLastServerResponse();
        LOGGER.info("mail result: {}", result);

        return mailResult;
    }

    private void setProps(Properties properties) {
        if (properties == null) {
            LOGGER.warn("设置SMTP属性失败，参数properties为空");
            return;
        }

        properties.put("mail.smtp.timeout", 5000);			    // 设置超时时间
        properties.put("mail.smtp.connectiontimeout", 5000);	// 设置超时时间
        properties.put("mail.smtp.host", this.email.getHost());
        properties.put("mail.smtp.port", this.email.getPort());
        properties.put("mail.smtp.sendpartial", "true");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.from", this.email.getUsername());
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.tls.enable", "true");
        properties.put("mail.smtp.starttls.enable", "true");
    }
}

class MyAuthenticator extends Authenticator {

    private String username;

    private String password;

    public MyAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
    }
}