package conf;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import core.Log4jManager;
import utils.ExceptionEx;
import utils.FileEx;
import utils.MiscUtils;

/**
 * 
 * @function 运行配置
 */
public final class RunConf {
    private static final Logger LOGGER = Logger.getLogger(RunConf.class);


    private static final String CONFIG_PATH = "config";

    private final static SAXReader READER = new SAXReader();

    /**
     * 当前登录服连接地址
     */
    public static String loginUrl;

    /**
     * 当前选择的服务器配置
     */
    public static ServerConf choosedServerConf;

    /**
     * 当前机器人配置
     */
    public static RobotConf robotConf = new RobotConf();

    /**
     * 服务器配置列表
     */
    public static final List<ServerConf> SERVERCONFS = new ArrayList<ServerConf>();

    /**
     * 初始化配置
     */
    public static void initConf() {
        try {
            loadLog4jConf();
            loadServersConf();

            int selectId = 0;
            if (FileEx.isExists("./config/serverIndex.txt")) {
                String select = FileEx.readAll("./config/serverIndex.txt");
                select = select.trim();
                if (select != null && !select.isEmpty()) {
                    selectId = Integer.valueOf(select);
                }
            }
            choosedServerConf = RunConf.SERVERCONFS.get(selectId);
            choosedServerConf.setIndex(selectId);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            System.exit(1);
        }
    }

    /**
     * 加载服务器配置
     * 
     */
    private static void loadServersConf() throws Exception {
        File serverConf = new File(SystemUtils.USER_DIR + File.separator + CONFIG_PATH
                + File.separator + "serverConf.xml");
        Document document = READER.read(serverConf);
        Element elRoot = document.getRootElement();
        List<Element> list = elRoot.elements();
        for (Element e : list) {
            if (e.getName().equals("servers")) {
                e.elements().forEach(
                        ee -> SERVERCONFS.add(new ServerConf(ee.element("name").getStringValue())));
            } else if (e.getName().equals("loginUrl")) {
                loginUrl = e.element("url").getStringValue();
            }
        }
    }

    /**
     * 加载log4j配置
     * 
     * @throws Exception
     */
    private static void loadLog4jConf() throws Exception {
        StringBuilder pathBuilder = new StringBuilder(
                System.getProperty("user.dir") + File.separator + CONFIG_PATH + File.separator);
        if(MiscUtils.isIDEEnvironment()) {
            pathBuilder.append("log4j_devel.xml");
        } else {
            pathBuilder.append("log4j_release.xml");
        }
        DOMConfigurator.configureAndWatch(pathBuilder.toString());
        Log4jManager.getInstance().init();
    }
}
