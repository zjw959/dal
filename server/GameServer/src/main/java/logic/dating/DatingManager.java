package logic.dating;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITriggerEvent;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.script.IDatingManagerScript;
import logic.support.LogicScriptsUtils;

/**
 * 约会数据管理器
 * <p>
 * 约会仅由约会模块管理,不应置于看板娘等其他模块以增加耦合
 * 
 * @author Alan
 *
 */
public class DatingManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IAcrossDay, ITriggerEvent, ICreateRoleInitialize {
    /** 约会id生成 */
    AtomicLong datingIdCreator = new AtomicLong();

    /** 已经完成的结局 */
    private Set<Integer> completeEndings = new HashSet<Integer>();

    /** 已经完成的剧本 */
    private Set<Integer> completeDatings = new HashSet<Integer>();

    /** 正在进行的剧本 */
    private Map<Long, CurrentDatingBean> currentDatings = new HashMap<Long, CurrentDatingBean>();

    /** 看板娘对应的触发约会剧本 */
    private Map<Integer, Set<Integer>> roleTriggerScripts = new HashMap<Integer, Set<Integer>>();

    /** 看板娘城市约会次数 */
    private Map<Integer, Integer> roleCityDatingCount = new HashMap<Integer, Integer>();

    /**
     * 对话选择次数记录
     */
    private Map<Integer, Integer> dialogueCount = new HashMap<Integer, Integer>();

    /** 上次预定约会次数刷新时间 */
    private long lastReserveRefreshTime;

    /** 上次预定约会触发的时间段对应的时间点 */
    private Map<Integer, List<Long>> lastTriggerFrame = new HashMap<Integer, List<Long>>();

    /** 日常约会时间限制 **/
    private transient long checkDatingTime;

    /** 日常约会的次数(已使用的) **/
    private int dailyCont;


    private IDatingManagerScript getManagerScript() {
        return LogicScriptsUtils.getIDatingManagerScript();
    }

    public long newDatingId() {
        return datingIdCreator.incrementAndGet();
    }

    /**
     * 获取城市约会
     */
    public CityDatingBean getCityDating(long cityDatingId) {
        return getManagerScript().getCityDating(cityDatingId, currentDatings);
    }

    /**
     * 获取当前所有城市约会
     */
    public List<CityDatingBean> getCurrentCityDatings(int datingType) {
        return getManagerScript().getCurrentCityDatings(datingType, currentDatings);
    }


    /**
     * 获取当前所有城市约会
     */
    public List<CityDatingBean> getAllCityDatings() {
        return getManagerScript().getAllCityDatings(currentDatings);
    }


    /**
     * 放置约会对象
     */
    public void putDatingBean(CurrentDatingBean dating) {
        getManagerScript().putDatingBean(dating, currentDatings);
    }


    /** 看板娘触发约会是否存在 */
    public boolean roleTriggerDatingExists(int roleId, int scriptId) {
        return getManagerScript().roleTriggerDatingExists(roleId, scriptId, roleTriggerScripts);
    }


    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid,long datingId) {
        return getManagerScript().getByDatingTypeRoleId(datingType, roleCid, currentDatings,datingId);
    }

    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid) {
        return getManagerScript().getByDatingTypeRoleId(datingType, roleCid, currentDatings);
    }
    
    /** 精灵是否暂用 **/
    public CurrentDatingBean OccupyRoleId(int roleCid) {
        return getManagerScript().OccupyRoleId(roleCid, currentDatings);
    }

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType,
            List<Integer> roleCidList) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleCidList,
                currentDatings);
    }


    public int getRoleCityDatingCount(int roleCid) {
        return getManagerScript().getRoleCityDatingCount(roleCid, roleCityDatingCount);
    }

    public void addRoleCityDatingCount(int roleCid) {
        getManagerScript().addRoleCityDatingCount(roleCid, roleCityDatingCount);
    }

    @Override
    public void tick() {
        getManagerScript().tick(player);
    }

    /** 检测触发城市约会的时间 **/
    public boolean checkCityDating() {
        return getManagerScript().checkCityDating(this);
    }

    /**
     * 自主处理城市约会
     */
    public void handleCityDating(Date now) {
        getManagerScript().handleCityDating(now, player);
    }


    /** 日常约会次数变化 **/
    public void changeDailyCont(int num) {
        getManagerScript().changeDailyCont(num, this);
    }


    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, isNotify, roleCityDatingCount, player);
    }

    /** 时装和道具触发器 **/
    @Override
    public void trigger(Object object) {
        getManagerScript().trigger(object, player);
    }

    /** 检测喂食是否触发约会 **/
    public void checkDonateGift(Map<String, Object> in) {
        getManagerScript().checkDonateGift(in, player);
    }

    /** 检测时装 **/
    public void checkDress(Map<String, Object> in) {
        getManagerScript().checkDress(in, player);
    }


    @Override
    public void createRoleInitialize() throws Exception {
        getManagerScript().createRoleInitialize(player);
    }


    /** 检测预定约会是否超时 **/
    public boolean checkReserveDating() {
        return getManagerScript().checkReserveDating(player);
    }


    /** 接受邀请 **/
    public void acceptDating(boolean accept, Player player) {
        getManagerScript().acceptDating(accept, player);
    }



    /** 移除手机约会 **/
    public void removePhoneDating(Player player, CurrentDatingBean record) {
        getManagerScript().removePhoneDating(player, record);

    }

    /**
     * 清理相应约会
     */
    public void deleteDating(CurrentDatingBean dating) {
        getManagerScript().deleteDating(dating, currentDatings);
    }

    /**
     * 清理相应约会
     */
    public CityDatingBean deleteCityDating(long datingId) {
        return getManagerScript().deleteCityDating(datingId, currentDatings);
    }



    public long getCheckDatingTime() {
        return checkDatingTime;
    }

    public void setCheckDatingTime(long checkDatingTime) {
        this.checkDatingTime = checkDatingTime;
    }

    public Set<Integer> getCompleteEndings() {
        return completeEndings;
    }

    public void setCompleteEndings(Set<Integer> completeEndings) {
        this.completeEndings = completeEndings;
    }

    public Set<Integer> getCompleteDatings() {
        return completeDatings;
    }

    public void setCompleteDatings(Set<Integer> completeDatings) {
        this.completeDatings = completeDatings;
    }

    public Map<Long, CurrentDatingBean> getCurrentDatings() {
        return currentDatings;
    }

    public void setCurrentDatings(Map<Long, CurrentDatingBean> currentDatings) {
        this.currentDatings = currentDatings;
    }

    public Map<Integer, Set<Integer>> getRoleTriggerScripts() {
        return roleTriggerScripts;
    }

    public void setRoleTriggerScripts(Map<Integer, Set<Integer>> roleTriggerScripts) {
        this.roleTriggerScripts = roleTriggerScripts;
    }

    public long getLastReserveRefreshTime() {
        return lastReserveRefreshTime;
    }

    public void setLastReserveRefreshTime(long lastReserveRefreshTime) {
        this.lastReserveRefreshTime = lastReserveRefreshTime;
    }

    public Map<Integer, Integer> getDialogueCount() {
        return dialogueCount;
    }

    public void setDialogueCount(Map<Integer, Integer> dialogueCount) {
        this.dialogueCount = dialogueCount;
    }


    public List<Long> getLastTriggerFrame(int datingType) {
        return lastTriggerFrame.get(datingType);
    }

    public void setLastTriggerFrame(int datingType, List<Long> timeFrame) {
        lastTriggerFrame.put(datingType, timeFrame);
    }

    public int getDailyCont() {
        return dailyCont;
    }

    public void setDailyCont(int dailyCont) {
        this.dailyCont = dailyCont;
    }

}
