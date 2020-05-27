package javascript.logic.dating.trigger;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import data.GameDataManager;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EScriptIdDefine;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.handler.logic.DatingHandler;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.ICityDatingTimeOutCheckScript;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 预定约会工具脚本
 * 
 * @author lihongji
 *
 */
@SuppressWarnings("unused")
public class BaseReserveDatingTriggerScript implements IBaseDatingScript {
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.BASERESERVEDATING_SCRIPT.Value();
    }

    /**
     * 获取约会类型
     */
    @Override
    public int getDatingType() {
        return DatingTypeConstant.DATING_TYPE_RESERVE;
    }

    /** 当前类型约会中,精灵是否可用 */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean isRoleValid(Role role, Player player) {
        // 是否存在同类型约会
        DatingManager dm = player.getDatingManager();
        CurrentDatingBean dating = dm.OccupyRoleId(role.getCid());
        if (dating != null)
            return false;
        // 心情值符合条件
        Map data = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING).getData();
        if (role.getMood() > (Integer) data.get(DiscreteDataKey.MOOD))
            return true;
        return false;
    }

    /** 当前类型约会是否已经过期 */
    @Override
    public ICityDatingTimeOutCheckScript getTimeoutChecker(Player player,Logger LOGGER) {
        // 组装检测对象
        return new ICityDatingTimeOutCheckScript() {

            public boolean timeout(CityDatingBean record, Player player, Date now) {
                return LogicScriptsUtils.getIReserveDatingTriggerScript().datingTimeUpdate(record,
                        new Date(), player,LOGGER);
            }
        };
    }

}
