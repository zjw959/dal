package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public abstract class BaseServer {

    /**
     * 运维依赖,严禁修改
     */
    public static final String STARTFIELD = "server start field.";

    /**
     * 停止服务器
     */
    public abstract void stop();

    /* 以下其他公用方法 */


    /**
     * 是否是IDE调试模式
     *
     * @return
     */
    public boolean isIDEMode() {
        if (isGetIDEModelFromSystemProperty) {
            return isIDEModel;
        }

        String val = System.getProperty("ideDebug");
        isIDEModel = (val != null && val.equalsIgnoreCase("true"));
        isGetIDEModelFromSystemProperty = true;
        return isIDEModel;
    }

    public boolean isRootDrangServer() {
        if (isGetDrangServerFromSystemProperty) {
            return isRobotDrangServer;
        }
        String val = System.getProperty("isRobotServer");
        isRobotDrangServer = (val != null && val.equals("true"));

        isGetDrangServerFromSystemProperty = true;
        return isRobotDrangServer;
    }

    public abstract boolean isTestServer();

    /**
     * 获得配置文件路径
     *
     * @return
     */
    public String getConfigPath() {
        return new StringBuilder(
                System.getProperty("user.dir") + File.separator + "config" + File.separator)
                        .toString();
    }


    /**
     * 启动服务器
     * 
     * 1. 加载服务器日志配置文件
     * 
     * @throws MalformedURLException
     * @throws FileNotFoundException
     */
    protected void start() throws FileNotFoundException, MalformedURLException {
        loadServerLogConfig();
        LOGGER.info("begin server start");
    }

    protected void startEnd() {
        LOGGER.info("end server start, succeed.");
    }

    /**
     * 日志配置文件地址
     * 
     * @throws MalformedURLException
     * @throws FileNotFoundException
     */
    protected void loadServerLogConfig() throws FileNotFoundException, MalformedURLException {
        String log4jConfigPath;
        if (isIDEMode()) {
            // Ide调试模式下使用这个日志配置
            log4jConfigPath = getConfigPath() + "log4j_devel.xml";
        } else {
            // 正式发布时使用这个日志配置
            log4jConfigPath = getConfigPath() + "log4j_release.xml";
        }
        DOMConfigurator.configureAndWatch(log4jConfigPath);
    }

    /**
     * 是否是机器人压力测试服
     */
    private boolean isRobotDrangServer = false;

    /**
     * 是否从已经系统参数中读取
     * 
     * System.Property 底层实现是HashTable 如果多线程调用则会等锁, 取一次后缓存
     */
    private boolean isGetDrangServerFromSystemProperty = false;

    /**
     * 是否是ide模式下
     */
    private boolean isIDEModel = false;

    /**
     * 是否已经从系统参数中读取
     */
    private boolean isGetIDEModelFromSystemProperty = false;

    private final static Logger LOGGER = Logger.getLogger(BaseServer.class);

}
