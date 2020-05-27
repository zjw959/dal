package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import server.GameServer;
import utils.ExceptionEx;
import utils.FileEx;

/**
 * Mysql数据库连接静态工厂
 */
public enum DBFactory {
    // 游戏数据库
    GAME_DB("gameDB", "config/db-game-config.xml", false);
    // GAME_DATA_DB("gameDataDB", "data/db-game-data-config.xml", true),
    // 日志库
    // GAME_LOG_DB("gameLogDB", "config/db-game-log-config.xml", false);

    private final Logger logger;
    private final String name;
    private SqlSessionFactory sessionFactory;

    /**
     * 初始化一个DB实例.
     * 
     * @param config mybatis配置文件的相对路径
     * @param fromJar mybatis配置文件是否来自于jar包
     */
    DBFactory(String name, String config, boolean fromJar) {
        this.name = name;
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


    /**
     * 获取SQLSeesionFactory.
     * 
     * @return
     */
    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public Logger getLogger() {
        return logger;
    }

    private Properties getProperties() {
        Properties props = new Properties();
        try {
            InputStream in =
                    FileEx.openInputStream("./config/properties/datasource.properties");
            props.load(in);
        } catch (IOException e) {
            logger.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
        
        if(GameServer.getInstance().isIDEMode()) {
            if (GameServer.getInstance().getArgs().length >= 1) {
                String _url_game = (String) props.get("url_game");
                String _url_game_receplace =
                        _url_game.replace("127.0.0.1", GameServer.getInstance().getArgs()[0]);
                props.put("url_game", _url_game_receplace);

                String _url_mail = (String) props.get("url_mail");
                String _url_mail_receplace =
                        _url_mail.replace("127.0.0.1", GameServer.getInstance().getArgs()[0]);
                props.put("url_mail", _url_mail_receplace);

                String _url_global = (String) props.get("url_global");
                String _url_global_receplace =
                        _url_global.replace("127.0.0.1", GameServer.getInstance().getArgs()[0]);
                props.put("url_global", _url_global_receplace);
            }
        }
        
        return props;
    }


}
