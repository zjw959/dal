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
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.ICityDatingTimeOutCheckScript;
import logic.role.bean.Role;
import utils.TimeUtil;

/***
 * 
 * 出游约会工具脚本
 * 
 * @author lihongji
 *
 */

public class BaseTripDatingTriggerScript implements IBaseDatingScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.BASETRIPDATING_SCRIPT.Value();
    }

    /**
     * 获取约会类型
     */
    @Override
    public int getDatingType() {
        return DatingTypeConstant.DATING_TYPE_OUT;
    }

    /** 当前类型约会中,精灵是否可用 */
    @Override
    public boolean isRoleValid(Role role, Player player) {
        // 是否存在同类型约会
        DatingManager dm = player.getDatingManager();
        // dm.getByDatingTypeRoleId(getDatingType(), role.getCid());
        return dm.OccupyRoleId(role.getCid()) == null;
    }

    /** 当前类型约会是否已经过期 */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public ICityDatingTimeOutCheckScript getTimeoutChecker(Player player,Logger LOGGER) {
        // 从离散配置获取触发与约会时间段
        Map discreteData =
                GameDataManager.getDiscreteDataCfgBean((DiscreteDataID.DATING)).getData();
        Map<Integer, Integer> datingOutTime =
                (Map<Integer, Integer>) discreteData.get(DiscreteDataKey.DATING_OUT_TIME);
        // 获取超时分钟数
        int outDatingTime = datingOutTime.get(DatingTypeConstant.DATING_TYPE_OUT);
        // 组装检测对象
        return new ICityDatingTimeOutCheckScript() {
            int outDatingTime;

            public ICityDatingTimeOutCheckScript setOutDatingTime(int outDatingTime) {
                this.outDatingTime = outDatingTime;
                return this;
            }

            @Override
            public boolean timeout(CityDatingBean dating, Player player, Date now) {
                // 超时
                if ((int) ((now.getTime() - dating.getTriggerTime())
                        / TimeUtil.MINUTE) >= outDatingTime) {
                    // 移除约会
                    player.getDatingManager().deleteDating(dating);
                    return true;
                }
                return false;
            }
        }.setOutDatingTime(outDatingTime);
    }
}
