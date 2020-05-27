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

public interface ITripDatingTriggerScript extends IScript {

    public Set<Integer> getValidBuildings(Player player);

    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER);

    public int getDampingRate(Role role, Player player);

    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt);

    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt);

    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt);

    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt);

    public int getDatingType();

    public void addDatingLog(Player player, CityDatingBean record,Logger LOGGER);
}
