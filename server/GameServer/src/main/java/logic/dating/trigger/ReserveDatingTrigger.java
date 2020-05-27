package logic.dating.trigger;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.IReserveDatingTriggerScript;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;

/**
 * 预定约会触发器
 * 
 * @author Alan
 *
 */
public class ReserveDatingTrigger extends DatingTrigger {
    
    private static Logger LOGGER = Logger.getLogger(ReserveDatingTrigger.class);

    private IReserveDatingTriggerScript getManagerScript() {
        return LogicScriptsUtils.getIReserveDatingTriggerScript();
    }
    
    /** 刷新城市约会状态 */
    @Override
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER) {
        getManagerScript().refreshCityDating(player, now, baseScipt,LOGGER);
    }

    /**获取可以用建筑**/
    @Override
    protected Set<Integer> getValidBuildings(Player player) {
        return getManagerScript().getValidBuildings(player);
    }

    /** 城市约会的触发逻辑 */
    @Override
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        getManagerScript().triggerDating(player, now, baseScipt);
    }

    /**获取空闲的精灵列表**/
    @Override
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt) {
        return getManagerScript().getFreeRole(player, baseScipt);
    }

    /**
     * 获取可用的剧本
     * 
     * @param roleCidList 可用的精灵
     * @param buildingCidList 可用的建筑
     * @return
     */
    @Override
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        return getManagerScript().getUsableOutDatingRuleCfg(roleCidList, buildingCidList, player,
                baseScipt);
    }

    
    /**生成城市约会记录**/
    @Override
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        return getManagerScript().createCityDatingBean(dm, datingRuleCfg, datingFrame, now,
                baseScipt);
    }

    /**获取约会类型**/
    @Override
    public int getDatingType() {
        return getManagerScript().getDatingType();
    }


    @Override
    int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player) {
        return getManagerScript().getTriggerRate(role, datingRuleCfg, player);
    }

    /**
     * 更新约会状态,返回是否过期
     */
    public boolean datingTimeUpdate(CityDatingBean record, Date date, Player player) {
        return getManagerScript().datingTimeUpdate(record, date, player,LOGGER);
    }

    /** 发送失败奖励 **/
    public void sendFailAward(Player player, CityDatingBean record) {
        getManagerScript().sendFailAward(player, record);
    }

    /** 接受邀请 **/
    @Override
    public void acceptDating(boolean accept, Player player) {
        getManagerScript().acceptDating(accept, player,LOGGER);
    }

    /** 检查是否超时 **/
    @Override
    public boolean checkReserveDating(Player player) {
        return getManagerScript().checkReserveDating(player);
    }

    @Override
    public void addDatingLog(Player player, CityDatingBean record) {
         getManagerScript().addDatingLog(player,record,LOGGER);
    }

}
