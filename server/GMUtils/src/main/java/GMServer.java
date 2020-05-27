
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import gm.db.mail.bean.PlayerMailDBBean;
import gm.utils.MailUtils;
import server.BaseServer;
import utils.ExceptionEx;

/**
 * 
 * @Description GMServer
 * @author LiuJiang
 * @date 2018年7月7日 下午8:10:40
 *
 */
public class GMServer extends BaseServer {
    private final static Logger LOGGER = Logger.getLogger(GMServer.class);

    @Override
    public void stop() {

    }

    @Override
    public boolean isTestServer() {
        return false;
    }

    /**
     * 获取GMServer的实例对象.
     * 
     * @return
     */
    public static GMServer getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        GMServer processor;

        Singleton() {
            this.processor = new GMServer();
        }

        GMServer getProcessor() {
            return processor;
        }
    }

    public void start(String[] args) {
        System.out.println("begin server start console");
        try {
            System.out.println("---0");
            // 初始化日志配置
            super.start();
            System.out.println("---1");
            // 测试邮件
            List<PlayerMailDBBean> list =
                    MailUtils.selectPlayerMailsByTime(new Date(1), new Date());
            System.out.println("---size=" + list.size());
            for (PlayerMailDBBean mail : list) {
                System.out.println("--mail.id=" + mail.getId());
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }
}
