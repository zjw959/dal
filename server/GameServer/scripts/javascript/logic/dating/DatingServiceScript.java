package javascript.logic.dating;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.EScriptIdDefine;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.script.IDatingServiceScript;

/***
 * 
 * 约会逻辑处理服务对象脚本
 * 
 * @author lihongi
 *
 */

public class DatingServiceScript implements IDatingServiceScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DATINGSERVICE_SCRIPT.Value();
    }

    /**
     * 根据看板娘Id、约会类型，获取约会配置
     * 
     * @param roleId
     * @param datingType
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleId, int datingType) {
        return GameDataManager.getDatingRuleCfgBeans().stream()
                .filter(cfg -> cfg.getType() == datingType)
                .filter(cfg -> cfg.getEnterCondition()
                        .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS) != null)
                .filter(cfg -> ((List<Integer>) cfg.getEnterCondition()
                        .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)).size() == 1
                        || cfg.getEnterCondition()
                                .get(DatingConstant.ENTER_CDTION_KEY_SINGLE) != null)
                .filter(cfg -> ((List<Integer>) cfg.getEnterCondition()
                        .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)).contains(roleId))
                .collect(Collectors.toList());
    }

    /** 旧代码遗留,仅支持值为整型的类型 */
    @Override
    public int getDatingRuleEnterCondtionByType(DatingRuleCfgBean ruleCfg, String type) {
        if (ruleCfg.getEnterCondition() == null)
            return 0;
        Integer typeValue = (Integer) ruleCfg.getEnterCondition().get(type);
        if (typeValue != null) {
            return typeValue;
        }
        return 0;
    }

    /** 约会城市清理 */
    @SuppressWarnings("rawtypes")
    @Override
    public void clearCity(Player player, DatingRuleCfgBean cfg) {
        DatingManager dm = player.getDatingManager();
        int cityCid =
                getDatingRuleEnterCondtionByType(cfg, DatingConstant.ENTER_CDTION_KEY_CITY_CID);

        int roleCid = (Integer) ((List) cfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)).get(0);
        // 清楚当前不
        player.getCityRoleManager().removeOccupyBuilding(roleCid);

    }

    /**
     * 对城市约会非正常情况结算情况下缓存驻留情况的判定,缓存存在但可能已过期
     * 
     * @param now 根据给定时间结算驻留时间。
     * @param calLimit 若calLimit为false则超过结束时间即删除，用于玩家离开游戏世界
     */
    @Override
    public boolean cityDatingFailed(Player player, CityDatingBean dating, Date now) {
        // 约会时间已过并且当前已经超过驻留极限时间
        if (dating.getState() == DatingConstant.RESERVE_DATING_STATE_DATING_TIME_START
                && now.getTime() > dating.getDatingEndTime())
            return true;
        return false;
    }

    /**
     * 对城市约会非正常情况判定
     * <p>
     * 玩家进入约会后未正常结算导致缓存驻留,这里做极限判定后清除
     */
    @Override
    public void handleFailedCityDating(Player player, Date now) {
        if (player.getDatingManager().getCurrentDatings() == null
                || player.getDatingManager().getCurrentDatings().size() <= 0)
            return;
        Iterator<Entry<Long, CurrentDatingBean>> iterators =
                player.getDatingManager().getCurrentDatings().entrySet().iterator();
        while (iterators.hasNext()) {
            CurrentDatingBean cudating = iterators.next().getValue();
            if (cudating instanceof CityDatingBean) {
                CityDatingBean dating = (CityDatingBean) cudating;
                if (now.getTime() > dating.getDatingEndTime()) {
                    iterators.remove();
                }
            }
        }
    }
}
