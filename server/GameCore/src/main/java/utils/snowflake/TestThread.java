package utils.snowflake;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @Description 测试雪花算法id是否重复
 * @author LiuJiang
 * @date 2018年8月4日 下午2:21:48
 *
 */
public class TestThread extends Thread {
    int serverId;
    SnowflakeUtil util;
    int count;
    Map<Long, Integer> map;

    public TestThread(SnowflakeUtil util, int serverId, int count, Map<Long, Integer> map) {
        super.setName("TestThread-" + serverId);
        this.serverId = serverId;
        this.count = count;
        this.util = util;
        this.map = map;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " 开始执行！");
        for (int i = 0; i < count; i++) {
            long id = util.next();
            // long id = IdCreator.getUniqueId(2, 1);
            if (map.containsKey(id)) {
                System.out.println(Thread.currentThread().getName() + " id重复   id:" + id + " i="
                        + i + "  oldServerId:" + map.get(id));
                break;
            } else {
                if ((i + 1) % 10000 == 0) {
                    System.out.println(Thread.currentThread().getName() + " 执行到第 " + (i + 1)
                            / 10000
                            + " 万次");
                }
                map.put(id, serverId);
            }
        }
        System.out.println(Thread.currentThread().getName() + " 执行完毕！");
    }

    public static void main(String[] args) {
        int serverNum = 10;// 服务器总数（线程数）
        int count = 1000000;// 单个线程获取id执行次数
        Map<Long, Integer> map = new ConcurrentHashMap<Long, Integer>();
        SnowflakeUtil util = new SnowflakeUtil(1);
        for (int i = 0; i < serverNum; i++) {
            int serverId = i + 1;
            util = new SnowflakeUtil(serverId);
            TestThread thread = new TestThread(util, serverId, count, map);
            thread.start();
        }
    }
}
