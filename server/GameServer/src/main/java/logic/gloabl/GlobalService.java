package logic.gloabl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;

import gm.db.global.bean.GlobalDBBean;
import gm.utils.GlobalUtils;
import logic.constant.EGlobalIdDefine;
import logic.gloabl.handler.LUpdateGlobalDBBeanHandler;
import thread.player.PlayerProcessorManager;
import thread.sys.base.SysService;
import utils.DateEx;
import utils.ExceptionEx;

public class GlobalService extends SysService {
    private final static Logger LOGGER = Logger.getLogger(GlobalService.class);
    /** 定时获取全局表数据的时间间隔 */
    public static int DELAY = 10 * 1000;

    Map<Integer, GlobalDBBean> globalBeans = Maps.newConcurrentMap();


    /** 单服最大在线人数 (排队阈值，在线人数达到此值后进入排队) */
    public volatile static int ONLINE_MAX = 6000;

    /**
     * 允许缓存的最大玩家数量 OnlineMax的倍数
     */
    public volatile static int MEMORY_MAX = 4;

    /**
     * 内存紧张 关闭登陆开关
     */
    public volatile static boolean MEMORY_BUSY_SWITCH = false;
    
    /** 最大同时登陆人数 */
    public volatile static int LOGIN_QUEUE_MAX = ONLINE_MAX;

    /**
     * 每个玩家线程离线玩家最大缓存
     */
    /** 内测最后根据压测大小进行设置 公测根据内测数据调整 **/
    private volatile static int MAXNUM_OFOFFLINEPLAYER = 150;

    /** 下次tick检查时间 */
    private long nextTickGetTime;

    static {
        // 根据内存计算最大在线人数(25292(26G)->6000人 1M-->0.225人)
        // 注意 并非一个玩家所占内存 还包括其他系统 以及新生代等
        double max = Runtime.getRuntime().maxMemory() / 1024 / 1024d;
        int maxline = ONLINE_MAX;
        if (max < 25000) {
            maxline = (int) (max * 0.225);
            if (maxline < 100) {
                maxline = 100;
            }
            if (maxline > ONLINE_MAX) {
                maxline = ONLINE_MAX;
            }
        }
        ONLINE_MAX = maxline;
        LOGIN_QUEUE_MAX = ONLINE_MAX * 3;

        int offline = MAXNUM_OFOFFLINEPLAYER;
        // 根据内存计算最大在线人数(26600M(28G)->150人 1M-->0.005639人)
        if (max < 25000) {
            offline = (int) (max * 0.005639);
            if (offline < 10) {
                offline = 10;
            }
            if (offline > MAXNUM_OFOFFLINEPLAYER) {
                offline = MAXNUM_OFOFFLINEPLAYER;
            }
        }
        MAXNUM_OFOFFLINEPLAYER = offline;
    }


    /** 离线玩家在内存中保留的最长时间(3小时) */
    private volatile int MAX_OFFLINETIMES = (int) (3 * DateEx.TIME_HOUR);
    /** 玩家定时回存的间隔(5分钟) */
    private volatile int SAVEBACKINTERVAL = (int) (5 * DateEx.TIME_MINUTE);
    /** 回存检测时间间隔 */
    private volatile int SAVEBACKCHECKINTERVAL = (int) (1 * DateEx.TIME_SECOND);
    /** 推荐好友本地缓存与redis比例(5次读一次redis) */
    private volatile int RECOMMAND_FRIEND_HIT_LOCAL_RATE = 5;
    /** 推荐好友redis加载时间间隔 (3分钟) */
    private volatile int RECOMMAND_FRIEND_LOAD_REDIS_INTERVAL = 3;
    /** 推荐好友每个等级读取redis数量 (500) */
    private volatile int RECOMMAND_FRIEND_LOAD_COUNT = 500;


    /** 修改最大在线人数 */
    public void modifyOnlineMax(int num) throws Exception {
        updateGlobalDBBean(EGlobalIdDefine.ONLINE_MAX.Value(), num, null);
        ONLINE_MAX = num;
        PlayerProcessorManager.getInstance().initSaveNum();
    }

    public void modifyMEMORY_MAX(int num) {
        updateGlobalDBBean(EGlobalIdDefine.MEMORY_MAX.Value(), num, null);
        MEMORY_MAX = num;
    }

    public void modifyLOGIN_QUEUE_MAX(int num) {
        updateGlobalDBBean(EGlobalIdDefine.LOGIN_QUEUE_MAX.Value(), num, null);
        LOGIN_QUEUE_MAX = num;
    }

    
    public void modifyMEMORY_BUSY(int num) {
        updateGlobalDBBean(EGlobalIdDefine.MEMORY_BUSY_SWITCH.Value(), num, null);
        boolean _b = num == 1 ? true : false;
        MEMORY_BUSY_SWITCH = _b;
    }
    
    public void modifyOffLineMax(int num) {
        updateGlobalDBBean(EGlobalIdDefine.MAXNUM_OFOFFLINEPLAYER.Value(), num, null);
        MAXNUM_OFOFFLINEPLAYER = num;
    }

    public void modifyMaxOfflineTimes(int num) {
        updateGlobalDBBean(EGlobalIdDefine.MAX_OFFLINETIMES.Value(), num, null);
        MAX_OFFLINETIMES = num;
    }

    public void modifyRecommandFriendHitLocalRate(int num) {
        updateGlobalDBBean(EGlobalIdDefine.RECOMMAND_FRIEND_HIT_LOCAL_RATE.Value(), num, null);
        RECOMMAND_FRIEND_HIT_LOCAL_RATE = num;
    }

    public void modifyRecommandFriendLoadRedisInterval(int num) {
        updateGlobalDBBean(EGlobalIdDefine.RECOMMAND_FRIEND_LOAD_REDIS_INTERVAL.Value(), num, null);
        RECOMMAND_FRIEND_LOAD_REDIS_INTERVAL = num;
    }

    public void modifyRecommandFriendLoadCount(int num) {
        updateGlobalDBBean(EGlobalIdDefine.RECOMMAND_FRIEND_LOAD_COUNT.Value(), num, null);
        RECOMMAND_FRIEND_LOAD_COUNT = num;
    }


    public boolean modifySaveBackInterval(int num) {
        int backNum = SAVEBACKINTERVAL;
        try {
            SAVEBACKINTERVAL = num;
            PlayerProcessorManager.getInstance().initSaveNum();
            updateGlobalDBBean(EGlobalIdDefine.SAVEBACKINTERVAL.Value(), num, null);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            SAVEBACKINTERVAL = backNum;
            try {
                PlayerProcessorManager.getInstance().initSaveNum();
                updateGlobalDBBean(EGlobalIdDefine.SAVEBACKINTERVAL.Value(), num, null);
            } catch (Exception e1) {
                LOGGER.error(ExceptionEx.e2s(e1));
            }
            return false;
        }
    }

    public boolean modifySaveBackCheckInterval(int num) {
        int backNum = SAVEBACKCHECKINTERVAL;
        try {
            SAVEBACKCHECKINTERVAL = num;
            PlayerProcessorManager.getInstance().initSaveNum();
            PlayerProcessorManager.getInstance().initProcess(System.currentTimeMillis());
            updateGlobalDBBean(EGlobalIdDefine.SAVEBACKCHECKINTERVAL.Value(), num, null);
            return true;
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            SAVEBACKCHECKINTERVAL = backNum;
            try {
                PlayerProcessorManager.getInstance().initSaveNum();
                PlayerProcessorManager.getInstance().initProcess(System.currentTimeMillis());
                updateGlobalDBBean(EGlobalIdDefine.SAVEBACKCHECKINTERVAL.Value(), num, null);
            } catch (Exception e1) {
                LOGGER.error(ExceptionEx.e2s(e1));
            }
            return false;
        }
    }

    public static int getOffLineMax() {
        return MAXNUM_OFOFFLINEPLAYER;
    }

    public int getMaxOffLineTimes() {
        return this.MAX_OFFLINETIMES;
    }

    public int getSaveBackInterval() {
        return this.SAVEBACKINTERVAL;
    }

    public int getSavebackCheckInterval() {
        return SAVEBACKCHECKINTERVAL;
    }

    public int getRecommandFriendHitLocalRate() {
        return RECOMMAND_FRIEND_HIT_LOCAL_RATE;
    }

    public int getRecommandFriendLoadRedisInterval() {
        return RECOMMAND_FRIEND_LOAD_REDIS_INTERVAL;
    }

    public int getRecommandFriendLoadCount() {
        return RECOMMAND_FRIEND_LOAD_COUNT;
    }

    /** 初始化 */
    public void init() throws Exception {
        checkUpdate();
    }

    /** 检查更新全局bean */
    public void checkUpdate() throws Exception {
        nextTickGetTime = System.currentTimeMillis() + DELAY;
        List<GlobalDBBean> beans = GlobalUtils.selectAll();
        if (beans == null || beans.size() == 0) {
            return;
        }
        for (GlobalDBBean bean : beans) {
            globalBeans.put(bean.getId(), bean);
        }
        _checkUpdate();
    }

    private void _checkUpdate() throws Exception {
        // 单服最大在线人数
        GlobalDBBean bean = getGlobalDBBean(EGlobalIdDefine.ONLINE_MAX.Value());
        if (bean != null) {
            int newValue = (int) bean.getLongValue();
            if (ONLINE_MAX != newValue) {
                ONLINE_MAX = newValue;
                PlayerProcessorManager.getInstance().initSaveNum();
            }
        }

        GlobalDBBean memBean = getGlobalDBBean(EGlobalIdDefine.MEMORY_MAX.Value());
        if (memBean != null) {
            MEMORY_MAX = (int) memBean.getLongValue();
        }

        GlobalDBBean busyBean = getGlobalDBBean(EGlobalIdDefine.MEMORY_BUSY_SWITCH.Value());
        if (busyBean != null) {
            MEMORY_BUSY_SWITCH = busyBean.getLongValue() == 1 ? true : false;
        }

        GlobalDBBean loginBean = getGlobalDBBean(EGlobalIdDefine.LOGIN_QUEUE_MAX.Value());
        if (loginBean != null) {
            LOGIN_QUEUE_MAX = (int) loginBean.getLongValue();
        }

        GlobalDBBean maxbean = getGlobalDBBean(EGlobalIdDefine.MAX_OFFLINETIMES.Value());
        if (maxbean != null) {
            MAX_OFFLINETIMES = (int) maxbean.getLongValue();
        }

        GlobalDBBean offMaxBean = getGlobalDBBean(EGlobalIdDefine.MAXNUM_OFOFFLINEPLAYER.Value());
        if (offMaxBean != null) {
            MAXNUM_OFOFFLINEPLAYER = (int) offMaxBean.getLongValue();
        }

        GlobalDBBean saveIntCheckBean =
                getGlobalDBBean(EGlobalIdDefine.SAVEBACKCHECKINTERVAL.Value());
        if (saveIntCheckBean != null) {
            SAVEBACKCHECKINTERVAL = (int) saveIntCheckBean.getLongValue();
        }

        GlobalDBBean saveIntBean = getGlobalDBBean(EGlobalIdDefine.SAVEBACKINTERVAL.Value());
        if (saveIntBean != null) {
            SAVEBACKINTERVAL = (int) saveIntBean.getLongValue();
        }
    }

    /** 获取全局bean */
    public GlobalDBBean getGlobalDBBean(int id) {
        return globalBeans.get(id);
    }

    /** 更新全局bean */
    public void updateGlobalDBBean(int id, long longValue, String strValue) {
        this.getProcess().executeInnerHandler(
                new LUpdateGlobalDBBeanHandler(id, longValue, strValue));
    }

    public long getNextTickGetTime() {
        return nextTickGetTime;
    }

    public void setNextTickGetTime(long nextTickGetTime) {
        this.nextTickGetTime = nextTickGetTime;
    }

    public static GlobalService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        GlobalService instance;

        private Singleton() {
            instance = new GlobalService();
        }

        GlobalService getInstance() {
            return instance;
        }
    }


}
