package redis;

import java.util.List;
import logic.support.LogicScriptsUtils;

public class FightRedisOper {

    public static String[] sha = {""};

    /**
     * 匹配
     * 
     * @param queueKey 队列的Key
     * @param endTime 要移除的时间点(即当前时间点以前的玩家都会被移除,包含该时间点)
     * @size 参与匹配的人数
     * @return 本次成功匹配的id列表
     * @throws Exception
     */
    public static List<String> match(String queueKey, long endTime, int size)
            throws Exception {
        return LogicScriptsUtils.getRedisOperScript().match(queueKey, endTime, size, sha);
    }
    
    public static List<String> createTeam(String queueKey, long endTime, int size)
            throws Exception {
        return LogicScriptsUtils.getRedisOperScript().match(queueKey, endTime, size, sha);
    }

    public static void clearSha() {
        sha[0] = "";
    }

}
