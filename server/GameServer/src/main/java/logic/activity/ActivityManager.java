package logic.activity;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import event.Event;
import logic.activity.bean.ActivityRecord;
import logic.activity.script.IActivityManagerScript;
import logic.basecore.IAcrossDay;
import logic.basecore.IActivityListener;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 玩家活动记录
 * 
 * @author lihongji
 *
 */
public class ActivityManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, IActivityListener, ICreatePlayerInitialize, IAcrossDay {

    private static final Logger LOGGER = Logger.getLogger(ActivityManager.class);
    
    public static final int REMOVE_DAY=7;


    private IActivityManagerScript getManagerScript() {
        return LogicScriptsUtils.getIActivityManagerScript();
    }

    /** 玩家活动记录 Map<活动id - Map<活动条目,活动条目信息记录> > **/
    Map<Integer, Map<Integer, ActivityRecord>> activityRecord =
            new HashMap<Integer, Map<Integer, ActivityRecord>>();

    boolean open;

    /** 通过活动id 条目id 获取记录 **/
    public ActivityRecord getRecord(Integer activitId, Integer itemId) {
        return getManagerScript().getRecord(activitId, itemId, activityRecord);
    }

    /** 重新添加记录 **/
    public void addRcord(ActivityRecord record) {
        getManagerScript().addRcord(record, activityRecord);
    }

    // 判断条目是否完成领取过
    // 判断条目使能能够完成和领取
    // 领取奖励
    // 修改记录
    // 返回客户端新的数据
    public void getReward(Player player, int activityId, int activitEntryId, String extendData)  {
        getManagerScript().getReward(player, activityId, activitEntryId, extendData, activityRecord,
                LOGGER);
    }

    @Override
    public void execute(Event event) {
        getManagerScript().execute(event, player);
    }

    @Override
    public void createPlayerInitialize() {
        getManagerScript().createPlayerInitialize(player);
    }


    /** 初始化 **/
    public void activityInit() {
        getManagerScript().activityInit(player);
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        try {
            getManagerScript().acrossDay(type, isNotify, activityRecord,player,LOGGER);
        } catch (Exception e) {
            LOGGER.error("activity tick error", e);
        }
    }

    @Override
    public void open() {
        getManagerScript().open(player);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }



}
