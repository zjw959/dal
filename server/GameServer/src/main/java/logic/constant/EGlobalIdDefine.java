package logic.constant;

/**
 * 
 * @Description 全局表id定义
 * @author LiuJiang
 * @date 2018年7月27日 下午6:48:54
 *
 */
public enum EGlobalIdDefine {
    /**
     * 最大在线人数限制
     */
    ONLINE_MAX(1),
    
    /**
     * 每个玩家线程离线玩家最大缓存
     */
    MAXNUM_OFOFFLINEPLAYER(100),
    /**
     * 离线玩家缓存时长
     */
    MAX_OFFLINETIMES(101),

    SAVEBACKINTERVAL(102), 
    
    SAVEBACKCHECKINTERVAL(103),

    MEMORY_MAX(104), MEMORY_BUSY_SWITCH(105), LOGIN_QUEUE_MAX(106),

    
    /** 推荐好友本地缓存与redis比例(5次读一次redis) */
    RECOMMAND_FRIEND_HIT_LOCAL_RATE(201),
    /** 推荐好友redis加载时间间隔 (3分钟) */
    RECOMMAND_FRIEND_LOAD_REDIS_INTERVAL(202),
    /** 推荐好友每个等级读取redis数量 (500)*/
    RECOMMAND_FRIEND_LOAD_COUNT(203);


    private final int value;


    EGlobalIdDefine(int value) {
        this.value = value;
    }

    public int Value() {
        return this.value;
    }
}
