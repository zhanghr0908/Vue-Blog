package com.ltj.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class VueblogApplication {

    public static void main(String[] args) {

        // 解决elastic search启动保存的问题
        System.setProperty("es.set.netty.runtime.available.processors", "false");

        SpringApplication.run(VueblogApplication.class, args);

    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }

}
