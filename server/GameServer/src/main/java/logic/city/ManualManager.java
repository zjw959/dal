package logic.city;

import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;

import data.bean.HandworkbaseCfgBean;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IUnlockBuilding;
import logic.basecore.PlayerBaseFunctionManager;
import logic.city.build.bean.ManualEvent;
import logic.city.script.IManualManagerScript;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 手工管理器
 * 
 * @author lihongji
 *
 */
public class ManualManager extends PlayerBaseFunctionManager
        implements IUnlockBuilding, IRoleJsonConverter {

    /** 手工事件 **/
    ManualEvent event = new ManualEvent();
    /** 用于解锁手工提醒 **/
    Set<Integer> validManual = new HashSet<Integer>();
    
    private static final Logger LOGGER = Logger.getLogger(ManualManager.class);

    private IManualManagerScript getManagerScript() {
        return LogicScriptsUtils.getIManualManagerScript();
    }

    /** 检测当前是否能进行手工制作 **/
    public void checkManual(HandworkbaseCfgBean handworkbaseCfgBean) {
        getManagerScript().checkManual(handworkbaseCfgBean, player);
    }

    /** 创建手工事件 **/
    public void createManualEvent(int id) {
        getManagerScript().createManualEvent(id, player,LOGGER);
    }


    /** 获取当前奖励 **/
    public S2CNewBuildingMsg.RespGetHandWorkAward.Builder getAward(int manualId) {
        return getManagerScript().getAward(manualId, player,LOGGER);
    }


    /** 检测客户端传递的手工操作积分 **/
    public S2CNewBuildingMsg.RespGetHandWorkInfo.Builder checkOperateIntegral(int manualId,
            int integral) {
        return getManagerScript().checkOperateIntegral(manualId, integral, player);
    }

    /** 检测 **/
    public void checkUnlock(int buildingId) {
        getManagerScript().checkUnlock(buildingId, player, validManual);
    }


    /** 获取自然增长量 **/
    public int getNatureTime(HandworkbaseCfgBean handworkbaseCfgBean) {
        return getManagerScript().getNatureTime(handworkbaseCfgBean, player);
    }


    public ManualEvent getEvent() {
        return event;
    }

    public void setEvent(ManualEvent event) {
        this.event = event;
    }


    /** 解锁手工 **/
    @Override
    public void unlock(Object object) {
        getManagerScript().unlock(object, player, validManual);
    }
}
