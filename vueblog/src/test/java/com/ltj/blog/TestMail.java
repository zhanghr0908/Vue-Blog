package com.ltj.blog;

import com.ltj.blog.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TestMail {

    @Resource
    private MailService mailService;

    @Test
    public void testMail() {
        mailService.sendMailTest("270947694@qq.com", "test mail send", "hello send mail");
    }
}
