package com;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableCaching // 标注启动缓存.
public class Application {
    public static ConfigurableApplicationContext context;

    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        /*
         * Spring-boot已经集成了tomcat，main函数被执行时，SpringApplication引导应用启动spring 进而启动tomcat启动应用
         */
        context = SpringApplication.run(Application.class, args);
        log.info("server start success,{}", new Date());
    }
}
