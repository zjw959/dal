package logic.dating.script;

import java.util.Date;
import java.util.List;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CityDatingBean;
import script.IScript;

/***
 * 
 * 约会逻辑处理服务对象
 * 
 * @author lihongi
 *
 */

public interface IDatingServiceScript extends IScript {


    /**
     * 根据看板娘Id、约会类型，获取约会配置
     * 
     * @param roleId
     * @param datingType
     * @return
     */
    public List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleId, int datingType);


    /** 旧代码遗留,仅支持值为整型的类型 */
    public int getDatingRuleEnterCondtionByType(DatingRuleCfgBean ruleCfg, String type);


    /** 约会城市清理 */
    public void clearCity(Player player, DatingRuleCfgBean cfg);

    /**
     * 对城市约会非正常情况结算情况下缓存驻留情况的判定,缓存存在但可能已过期
     * 
     * @param now 根据给定时间结算驻留时间。
     * @param calLimit 若calLimit为false则超过结束时间即删除，用于玩家离开游戏世界
     */
    public boolean cityDatingFailed(Player player, CityDatingBean dating, Date now);



    /**
     * 对城市约会非正常情况判定
     * <p>
     * 玩家进入约会后未正常结算导致缓存驻留,这里做极限判定后清除
     */
    public void handleFailedCityDating(Player player, Date now);


}
