package logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import logic.basecore.IAcrossDay;
import logic.basecore.ICheckUnlockBuilding;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.city.build.bean.BuildEvent;
import logic.city.script.INewBuildingManagerScript;
import logic.constant.EAcrossDayType;
import logic.support.LogicScriptsUtils;

/***
 * 新城建管理器
 * 
 * @author lihongji
 *
 */
public class NewBuildingManager extends PlayerBaseFunctionManager implements ICheckUnlockBuilding,
        IRoleJsonConverter, ICreateRoleInitialize, ICreatePlayerInitialize, IAcrossDay {

    /** 解锁的建筑 **/
    private Set<Integer> validbuilding = new HashSet<Integer>();
    /** 解锁的功能 **/
    private Map<Integer, Set<Integer>> validFunIds = new HashMap<Integer, Set<Integer>>();

    /** 事件记录器(建筑id-建筑event) **/
    private Map<Integer, ArrayList<BuildEvent>> buildingEvent =
            new HashMap<Integer, ArrayList<BuildEvent>>();

    /** 白天还是黑夜 **/
    private transient int dayType;
    /** 下一次的验证时间 **/
    private transient long nextChangeTime;


    private INewBuildingManagerScript getManagerScript() {
        return LogicScriptsUtils.getINewBuildingManagerScript();
    }

    /** 建筑解锁 **/
    public Map<Integer, ArrayList<Integer>> checkUnlock(boolean notice) {
        return getManagerScript().checkUnlock(notice, player, validbuilding, validFunIds);
    }

    /** 建筑检测 **/
    public void checkUnlockBuilding(ArrayList<BuildEvent> events,
            Map<Integer, ArrayList<Integer>> buildingsFundIds) {
        getManagerScript().checkUnlockBuilding(events, buildingsFundIds, validbuilding, player);
    }

    /** 检测功能行开放 **/
    public void checkUnlockFunIds(ArrayList<BuildEvent> events,
            Map<Integer, ArrayList<Integer>> buildingsFundIds) {
        getManagerScript().checkUnlockFunIds(events, buildingsFundIds, validbuilding, player,
                validFunIds);
    }



    /** 创建事件 **/
    public void createfireEvent() {
        getManagerScript().createfireEvent(player, validbuilding, validFunIds);
    }


    /** 移除事件 **/
    public void removeEvent(int eventType) {
        getManagerScript().removeEvent(eventType, player, buildingEvent);
    }

    @Override
    public void tick() {
        getManagerScript().tick(this);
    }

    /** 检测时间点 **/
    public void checkDayType() {
        getManagerScript().checkDayType(this);
    }



    @Override
    public void loginInit() {

    }

    @Override
    public void check(Object object) {
        getManagerScript().check(object, player, validbuilding, validFunIds);
    }

    @Override
    public void createRoleInitialize() throws Exception {
        getManagerScript().createPlayerInitialize(player, validbuilding, validFunIds);
    }

    public int getDayType() {
        return dayType;
    }


    public void setDayType(int dayType) {
        this.dayType = dayType;
    }

    public Set<Integer> getValidbuilding() {
        return validbuilding;
    }

    public void setValidbuilding(Set<Integer> validbuilding) {
        this.validbuilding = validbuilding;
    }

    public Map<Integer, ArrayList<BuildEvent>> getBuildingEvent() {
        return buildingEvent;
    }

    public void setBuildingEvent(Map<Integer, ArrayList<BuildEvent>> buildingEvent) {
        this.buildingEvent = buildingEvent;
    }

    public Map<Integer, Set<Integer>> getValidFunIds() {
        return validFunIds;
    }

    public void setValidFunIds(Map<Integer, Set<Integer>> validFunIds) {
        this.validFunIds = validFunIds;
    }


    public long getNextChangeTime() {
        return nextChangeTime;
    }

    public void setNextChangeTime(long nextChangeTime) {
        this.nextChangeTime = nextChangeTime;
    }

    @Override
    public void createPlayerInitialize() {
        getManagerScript().createPlayerInitialize(player, validbuilding, validFunIds);
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {

    }

}
