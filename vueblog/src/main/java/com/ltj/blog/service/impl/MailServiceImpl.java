package com.ltj.blog.service.impl;

import com.ltj.blog.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailProperties mailProperties;

    /**
     * 发送普通文本邮件
     */
    @Override
    @Async
    public void sendSimpleMail(String toAccount, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(toAccount);
        message.setSubject(subject);
        message.setText(content);
        try {
            javaMailSender.send(message);
            log.info(mailProperties.getUsername() + " 给 " + toAccount + " 的普通文本邮件已发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送HTML邮件
     */
    @Override
    @Async
    public void sendHtmlMail(String toAccount, String subject, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailProperties.getUsername());
            messageHelper.setTo(toAccount);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
            javaMailSender.send(mimeMessage);
            log.info(mailProperties.getUsername() + " 给 " + toAccount + " 的html邮件已发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Value("${mail.fromMail.addr}")
    private String from;

    @Override
    public void sendMailTest(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        try {
            javaMailSender.send(message);
            log.info(mailProperties.getUsername() + " 给 " + to + " 的普通文本邮件已发送");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
