package logic.dating.script;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.role.bean.Role;
import script.IScript;

/**
 * 城市约会触发器
 * <p>
 * 封装触发通用逻辑
 * 
 * @author lihongji
 *
 */


public interface IDatingTriggerScript extends IScript {

    /** 刷新城市约会状态 */
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER);

    public void refreshCity(Player player, List<CityDatingBean> outTimeRecordList);

    /** 城市约会的触发逻辑 */
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt);

    /**
     * 获取空闲的精灵列表
     */
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt);

    /**
     * 获取可用的剧本
     * 
     * @param roleCidList 可用的精灵
     * @param buildingCidList 可用的建筑
     * @return
     */
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt);

    /** 当前类型约会中,剧本可用的额外条件 */
    public boolean isRuleValid(DatingRuleCfgBean rule, Player player);



    /**
     * 获取空闲的建筑列表
     */
    public Set<Integer> getFreeBuildingList(Player player);


    /** 获取当前类型约会中可用的建筑 */
    public Set<Integer> getValidBuildings(Player player);


    /** 当前类型约会中,建筑是否可用 */
    public boolean isBuildingValid(int building, Player player);


    /**
     * 获取触发时段 触发时段被所有城市约会共享，在同一触发时段只能触发一次任意类型的城市约会
     * P.S.目前打工约会、预定约会、出游约会的触发时间做成了统一的，如果策划需要改成每种时间不同，就把这个写到它们各自的触发类中去
     * 
     * @return
     */
    public Map<String, List<Integer>> getTimeFrame(Date now);

    /**
     * 检查今日当前时段是否已经触发约会
     *
     * @param player
     * @param timeFrame 时间段
     * @return
     */
    public boolean alreadyTrigger(Player player, Date now, IBaseDatingScript baseScipt);


    /**
     * 筛选约会并进行触发
     */
    public boolean filtrateDating(Player player, List<DatingRuleCfgBean> datingRuleCfgList,
            Map<String, List<Integer>> timeFrame, Date now, IBaseDatingScript baseScipt);



    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt);


    /**
     * 校验触发几率 1.乱入约会只校验约会几率 2.单人约会不仅校验约会几率，还要校验看板娘的几率(逐次递减的几率)
     */
    public boolean checkDatingTriggerRate(Player player, DatingRuleCfgBean datingRuleCfg);

    /**
     * 获得衰减几率
     */
    public int getDampingRate(Role role, Player player);


    public int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player);


    /**
     * 更新建筑信息
     */
    public void updateCity(Player player, DatingRuleCfgBean cfg);

    /**
     * 更新看板娘
     */
    public void updateRole(Player player, DatingRuleCfgBean cfg);



    public void sendDating(Player player, CityDatingBean record);


    /**
     * 增加城市约会次数
     */
    public void addCityDatingCount(Player player, List<Integer> roleIdList);

    /** 接受邀请 **/
    public void acceptDating(boolean accept, Player player);


    /** 检查释放超时 **/
    public boolean checkReserveDating(Player player);


}
