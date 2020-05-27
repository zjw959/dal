package logic.dating.script;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.role.bean.Role;
import script.IScript;

/***
 * 
 * 预定约会
 * 
 * @author lihongji
 *
 */

public interface IReserveDatingTriggerScript extends IScript {

    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER);

    public Set<Integer> getValidBuildings(Player player);

    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt);

    public int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player);

    public boolean datingTimeUpdate(CityDatingBean record, Date date, Player player,Logger LOGGER);

    /** 迟到 */
    public boolean isLateDating(Date date, long endTime, CityDatingBean record);

    /** 进行 */
    public boolean isBeginningDating(Date date, long beginTime, long endTime,
            CityDatingBean record);

    /** 发送失败奖励 **/
    public void sendFailAward(Player player, CityDatingBean record);

    /** 接受邀请 **/
    public void acceptDating(boolean accept, Player player,Logger LOGGER);

    /** 检查是否超时 **/
    public boolean checkReserveDating(Player player);

    public int getDampingRate(Role role, Player player);

    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt);

    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt);

    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt);

    public int getDatingType();
    
    public void addDatingLog(Player player, CityDatingBean record,Logger LOGGER);
    
    public void addFailLog(Player player, CityDatingBean record, Logger LOGGER,int reasonType);

}
