package logic.functionSwitch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import logic.constant.EFunctionType;
import thread.sys.base.SysService;
import data.GameDataManager;
import data.bean.FunctionCfgBean;

/**
 * 
 * @Description 全服功能开关服务类
 * @author LiuJiang
 * @date 2018年8月8日 下午12:37:55
 *
 */
public class FunctionSwitchService extends SysService {
    // /** 定时获取功能开关的时间间隔 */
    // public static int DELAY = 10 * 1000;
    /** 开关集合 */
    Map<Integer, Boolean> switches = new ConcurrentHashMap<Integer, Boolean>();

    // /** 下次tick检查时间 */
    // private long nextTickGetTime;

    /** 功能开关获取tick */
    // public void tickGetFunctionSwitch() {
        // try {
        // long now = System.currentTimeMillis();
        // setNextTickGetTime(now + MarqueeService.DELAY);
        // List<FunctionSwitchDBBean> list = FunctionSwitchUtils.selectAll();
        // if (list.size() > 0) {
        // Set<Integer> types = new HashSet<Integer>();
        // // 检查新增和更新
        // for (FunctionSwitchDBBean bean : list) {
        // types.add(bean.getType());
        // boolean open = (bean.getStatus() == 1);
        // Boolean old = switches.get(bean.getType());
        // if (old != null && old == open) {
        // continue;
        // }
        // switches.put(bean.getType(), open);
        // }
        // } else if (switches.size() > 0) {
        // switches.clear();
        // }
        // } catch (Exception e) {
        // LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        // }
    // }

    /** 功能是否开启 */
    public boolean isOpenFunction(EFunctionType type) {
        checkLoad();
        Boolean b = switches.get(type.value());
        return b != null ? b : true;
    }

    // public long getNextTickGetTime() {
    // return nextTickGetTime;
    // }
    //
    // public void setNextTickGetTime(long nextTickGetTime) {
    // this.nextTickGetTime = nextTickGetTime;
    // }

    public Map<Integer, Boolean> getSwitches() {
        checkLoad();
        return switches;
    }

    /** 清理 */
    public void clear() {
        switches.clear();
    }

    /**
     * 检查加载
     */
    public void checkLoad() {
        if (switches.size() == 0) {
            List<FunctionCfgBean> beans = GameDataManager.getFunctionCfgBeans();
            for (FunctionCfgBean bean : beans) {
                switches.put(bean.getId(), bean.getIsOpen() == 1);
            }
        }
    }

    public static FunctionSwitchService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        FunctionSwitchService instance;

        private Singleton() {
            instance = new FunctionSwitchService();
        }

        FunctionSwitchService getInstance() {
            return instance;
        }
    }
}
