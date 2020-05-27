package logic.dating.trigger;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.IDatingTriggerScript;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;

/**
 * 城市约会触发器
 * <p>
 * 封装触发通用逻辑
 * 
 * @author Alan
 *
 */
public abstract class DatingTrigger {

    private IDatingTriggerScript getManagerScript() {
        return LogicScriptsUtils.getIDatingTriggerScript();
    }

    /** 刷新城市约会状态 */
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER) {
        getManagerScript().refreshCityDating(player, now, baseScipt,LOGGER);
    }

    protected void refreshCity(Player player, List<CityDatingBean> outTimeRecordList) {
        getManagerScript().refreshCity(player, outTimeRecordList);
    }

    /** 城市约会的触发逻辑 */
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        getManagerScript().triggerDating(player, now, baseScipt);
    }

    /**
     * 获取空闲的精灵列表
     */
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
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        return getManagerScript().getUsableOutDatingRuleCfg(roleCidList, buildingCidList, player,
                baseScipt);
    }

    /** 获取当前类型约会中可用的建筑 */
    protected Set<Integer> getValidBuildings(Player player) {
        return getManagerScript().getValidBuildings(player);
    }

    /**
     * 获取触发时段 触发时段被所有城市约会共享，在同一触发时段只能触发一次任意类型的城市约会
     * P.S.目前打工约会、预定约会、出游约会的触发时间做成了统一的，如果策划需要改成每种时间不同，就把这个写到它们各自的触发类中去
     * 
     * @return
     */
    public Map<String, List<Integer>> getTimeFrame(Date now) {
        return getManagerScript().getTimeFrame(now);
    }

    /**生成城市约会记录**/
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        return getManagerScript().createCityDatingBean(dm, datingRuleCfg, datingFrame, now,
                baseScipt);
    }

    /**
     * 获得衰减几率
     */
    int getDampingRate(Role role, Player player) {
        return getManagerScript().getDampingRate(role, player);
    }

    int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player) {
        return getManagerScript().getTriggerRate(role, datingRuleCfg, player);
    }

    /** 接受邀请 **/
    public void acceptDating(boolean accept, Player player) {

    }

    /** 检查释放超时 **/
    public boolean checkReserveDating(Player player) {
        return false;
    }

    public int getDatingType() {
        return 0;
    }
    
    /**增加日志**/
    public void addDatingLog(Player player,CityDatingBean record){
        
    }
    
}
