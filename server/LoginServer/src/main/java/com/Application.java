package com;

import gm.db.DBFactory;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import script.ScriptManager;
import utils.ExceptionEx;

@SpringBootApplication
public class Application {
    private static final Logger log = Logger.getLogger(Application.class);
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        try {
            ScriptManager.getInstance().initialize("./scripts", "./scriptBin", false);
            context = SpringApplication.run(Application.class, args);
            DBFactory.PAY_DB.getLogger();
            log.info("login server start");
        } catch (Exception e) {
            log.error("start server exception.");
            log.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
    }
}
