package utils.snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * 
 * @Description 测试雪花算法id是否重复
 * @author LiuJiang
 * @date 2018年8月4日 下午2:21:48
 *
 */
public class SnowflakeTestThread extends Thread {
    private static final Logger LOGGER = Logger.getLogger(SnowflakeTestThread.class);
    int serverId;
    SnowflakeUtil util;
    int count;
    Map<Long, Integer> map;

    public SnowflakeTestThread(SnowflakeUtil util, int serverId, int count, Map<Long, Integer> map) {
        super.setName("SnowflakeTestThread-" + serverId);
        this.serverId = serverId;
        this.count = count;
        this.util = util;
        this.map = map;
    }

    public void run() {
        LOGGER.info(Thread.currentThread().getName() + " 开始执行！");
        boolean isRepeated = false;
        for (int i = 0; i < count; i++) {
            long id = util.next();
            if (map.containsKey(id)) {
                LOGGER.error(Thread.currentThread().getName() + " id重复   id:" + id + " i="
                        + i + "  oldServerId:" + map.get(id));
                isRepeated = true;
                break;
            } else {
                if ((i + 1) % 10000 == 0) {
                    LOGGER.info(Thread.currentThread().getName() + " 执行到第 " + (i + 1) / 10000
                            + " 万次");
                }
                map.put(id, serverId);
            }
        }
        LOGGER.info(Thread.currentThread().getName() + " 执行完毕！isRepeated:" + isRepeated);
    }

    public static void main(String[] args) {
        int serverNum = 10;// 服务器总数（线程数）
        int count = 100000000;// 单个线程获取id执行次数
        Map<Long, Integer> map = new ConcurrentHashMap<Long, Integer>();
        SnowflakeUtil util = new SnowflakeUtil(1);
        for (int i = 0; i < serverNum; i++) {
            int serverId = i + 1;
            util = new SnowflakeUtil(serverId);
            SnowflakeTestThread thread = new SnowflakeTestThread(util, serverId, count, map);
            thread.start();
        }
    }
}
