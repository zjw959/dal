package gm.db;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import utils.ExceptionEx;
import utils.FileEx;

/**
 * Mysql数据库连接静态工厂
 */
public class DBFactory {
    // 邮件库
    public static DBFactory MAIL_DB = new DBFactory("MailDB", "gm/db/db-mail-config.xml", true);
    // 充值订单库
    public static DBFactory PAY_DB = new DBFactory("PayDB", "gm/db/db-pay-config.xml", true);
    // 全局库
    public static DBFactory GLOBAL_DB = new DBFactory("GlobalDB",
            "gm/db/db-global-config.xml",
            true);

    private final Logger logger;
    private final String name;
    private SqlSessionFactory sessionFactory;
    private boolean fromJar;
    private String config;
    private String dbUrl;
    private String dbUser;
    private String dbPwd;

    /**
     * 初始化一个DB实例.
     * 
     * @param config mybatis配置文件的相对路径
     * @param fromJar mybatis配置文件是否来自于jar包
     */
    public DBFactory(String name, String config, boolean fromJar) {
        this.name = name;
        this.config = config;
        this.fromJar = fromJar;
        this.logger = Logger.getLogger(name + "Logger");
        try {
            logger.info("DBFactory " + name + "-" + config);
            InputStream in = null;
            if (!fromJar)
                in = new FileInputStream(config);
            else {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                in = classloader.getResourceAsStream(config);
            }

            if (in == null) {
                logger.error("DBFactory failed: " + name + "-无法加载数据库配置文件!" + config);
                return;
            }
            this.sessionFactory = new SqlSessionFactoryBuilder().build(in, getProperties());
            logger.info("DBFactorySuccess " + name + "-" + config);
        } catch (Exception e) {
            logger.error(e, e);
            System.exit(-1);
        }
    }

    /** 重构连接工厂 */
    public boolean rebuildSessionFactory(String dbUrl, String dbUser, String dbPwd) {
        try {
            logger.info(name + " rebuildSessionFactory");
            logger.info(name + " dbUrl=" + dbUrl + " dbUser=" + dbUser + " dbPwd=" + dbPwd);
            if (dbUrl.equals(this.dbUrl) && dbUser.equals(this.dbUser) && dbPwd.equals(this.dbPwd)) {
                logger.info(name + " is same, no need rebuild");
                return true;
            }
            InputStream in = null;
            if (!fromJar)
                in = new FileInputStream(config);
            else {
                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
                in = classloader.getResourceAsStream(config);
            }

            if (in == null) {
                logger.error("DBFactory failed: " + name + "-无法加载数据库配置文件!" + config);
                return false;
            }
            Properties pros = getProperties();
            for (Entry<Object, Object> entry : pros.entrySet()) {
                String key = entry.getKey().toString();
                if (key.startsWith("url_")) {
                    pros.setProperty(key, dbUrl);
                } else if (key.startsWith("username_")) {
                    pros.setProperty(key, dbUser);
                } else if (key.startsWith("password_")) {
                    pros.setProperty(key, dbPwd);
                }
            }
            this.sessionFactory = new SqlSessionFactoryBuilder().build(in, pros);
            logger.info("rebuild DBFactorySuccess " + name + "-" + config);
            this.dbUrl = dbUrl;
            this.dbUser = dbUser;
            this.dbPwd = dbPwd;
            return true;
        } catch (Exception e) {
            logger.error(e, e);
            return false;
        }
    }

    /**
     * 获取SQLSeesionFactory.
     * 
     * @return
     */
    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public String getName() {
        return name;
    }

    public Logger getLogger() {
        return logger;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream in = null;
            String filePath = null;
            String propertiesFile = System.getProperty("propertiesFile");
            if (propertiesFile != null) {
                filePath = propertiesFile;
                ByteArrayInputStream stream = new ByteArrayInputStream(propertiesFile.getBytes());
                props.load(stream);

            } else {
                filePath = "./config/properties/datasource.properties";
                in = FileEx.openInputStream(filePath);
                props.load(in);
            }

        } catch (IOException e) {
            logger.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
        return props;
    }

}
