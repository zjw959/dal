package logic.dating;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.handler.logic.DailyDatingHandler;
import logic.dating.handler.logic.DatingHandler;
import logic.dating.handler.logic.DungeonDatingHandler;
import logic.dating.handler.logic.PhoneDatingHandler;
import logic.dating.handler.logic.ReserveDatingHandler;
import logic.dating.handler.logic.TriggerDatingHandler;
import logic.dating.handler.logic.TripDatingHandler;
import logic.dating.script.IDatingServiceScript;
import logic.dating.trigger.DatingTrigger;
import logic.dating.trigger.ReserveDatingTrigger;
import logic.dating.trigger.TripDatingTrigger;
import logic.support.LogicScriptsUtils;

/**
 * 约会逻辑处理服务对象
 * 
 * @author Alan
 *
 */
public class DatingService {

    Map<Integer, DatingHandler> logicHandlers;
    Map<Integer, DatingTrigger> cityTriggers;

    private IDatingServiceScript getManagerScript() {
        return LogicScriptsUtils.getIDatingServiceScript();
    }

    private DatingService() {
        // 初始化逻辑处理器
        resetHandlers();
        initDatingHandler();
        // 初始化城市约会触发器
        resetTriggers();
        initDatingTrigger();
    }

    /**
     * 初始逻辑处理器
     */
    private void initDatingHandler() {
        addHandler(new DailyDatingHandler());
//        addHandler(new MainDatingHandler());
        addHandler(new ReserveDatingHandler());
        addHandler(new TriggerDatingHandler());
        addHandler(new TripDatingHandler());
        addHandler(new DungeonDatingHandler());
        addHandler(new PhoneDatingHandler());
    }

    /**
     * 初始逻辑处理器
     */
    private void initDatingTrigger() {
        addTrigger(new ReserveDatingTrigger());
        addTrigger(new TripDatingTrigger());
    }

    public DatingHandler getLogicHandler(CurrentDatingBean current) {
        return logicHandlers.get(current.getDatingType());
    }

    public DatingHandler getLogicHandler(int handlerId) {
        return logicHandlers.get(handlerId);
    }

    void resetHandlers() {
        if (logicHandlers == null)
            logicHandlers = new HashMap<Integer, DatingHandler>();
        logicHandlers.clear();
    }

    void addHandler(DatingHandler handler) {
        if (logicHandlers == null)
            return;
        logicHandlers.put(handler.getHandlerIdentification(), handler);
    }

    DatingHandler removeHandler(int handlerId) {
        if (logicHandlers == null)
            return null;
        return logicHandlers.remove(handlerId);
    }

    public DatingTrigger getDatingTrigger(CityDatingBean dating) {
        return cityTriggers.get(dating.getDatingType());
    }

    public DatingTrigger getDatingTrigger(int datingType) {
        return cityTriggers.get(datingType);
    }

    void resetTriggers() {
        if (cityTriggers == null)
            cityTriggers = new HashMap<Integer, DatingTrigger>();
        cityTriggers.clear();
    }

    void addTrigger(DatingTrigger trigger) {
        if (cityTriggers == null)
            return;
        cityTriggers.put(trigger.getDatingType(), trigger);
    }

    DatingTrigger removeTrigger(int datingType) {
        if (cityTriggers == null)
            return null;
        return cityTriggers.remove(datingType);
    }

    public static DatingService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    /**
     * 根据看板娘Id、约会类型，获取约会配置
     * 
     * @param roleId
     * @param datingType
     * @return
     */
    public List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleId, int datingType) {
        return getManagerScript().getDatingRuleByRoleIdDatingType(roleId, datingType);
    }

    /** 旧代码遗留,仅支持值为整型的类型 */
    public int getDatingRuleEnterCondtionByType(DatingRuleCfgBean ruleCfg, String type) {
        return getManagerScript().getDatingRuleEnterCondtionByType(ruleCfg, type);
    }

    /** 约会城市清理 */
    public void clearCity(Player player, DatingRuleCfgBean cfg) {
        getManagerScript().clearCity(player, cfg);
    }


    /**
     * 对城市约会非正常情况结算情况下缓存驻留情况的判定,缓存存在但可能已过期
     * 
     * @param now 根据给定时间结算驻留时间。
     * @param calLimit 若calLimit为false则超过结束时间即删除，用于玩家离开游戏世界
     */
    public boolean cityDatingFailed(Player player, CityDatingBean dating, Date now) {
        return getManagerScript().cityDatingFailed(player, dating, now);
    }

    /**
     * 对城市约会非正常情况判定
     * <p>
     * 玩家进入约会后未正常结算导致缓存驻留,这里做极限判定后清除
     */
    public void handleFailedCityDating(Player player, Date now) {
        getManagerScript().handleFailedCityDating(player, now);
    }

    /**
     * 如果不在enum内部进行类成员的封装, 使用enum实例指向类对象仍然无法避免普通类的反射与反序列化造成多实例
     * 
     * @see logic.dungeon.DungeonSceneService.Singleton
     */
    private enum Singleton {
        INSTANCE;
        DatingService instance;

        Singleton() {
            instance = new DatingService();
        }

        public DatingService getInstance() {
            return instance;
        }
    }
}
