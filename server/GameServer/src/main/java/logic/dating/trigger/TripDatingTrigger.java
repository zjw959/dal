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
import logic.dating.script.ITripDatingTriggerScript;
import logic.support.LogicScriptsUtils;

/**
 * 出游约会触发器
 * 
 * @author Alan
 *
 */
public class TripDatingTrigger extends DatingTrigger {
    
    private static Logger LOGGER = Logger.getLogger(DatingTrigger.class);
    
    private ITripDatingTriggerScript getManagerScript() {
        return LogicScriptsUtils.getITripDatingTriggerScript();
    }

    @Override
    public Set<Integer> getValidBuildings(Player player) {
        return super.getValidBuildings(player);
    }

    @Override
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER) {
        getManagerScript().refreshCityDating(player, now, baseScipt,LOGGER);
    }

    @Override
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        getManagerScript().triggerDating(player, now, baseScipt);
    }


    @Override
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt) {
        return getManagerScript().getFreeRole(player, baseScipt);
    }

    @Override
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        return getManagerScript().getUsableOutDatingRuleCfg(roleCidList, buildingCidList, player,
                baseScipt);
    }


    @Override
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        return getManagerScript().createCityDatingBean(dm, datingRuleCfg, datingFrame, now,
                baseScipt);
    }

    @Override
    public int getDatingType() {
        return getManagerScript().getDatingType();
    }
    @Override
    public void addDatingLog(Player player, CityDatingBean record) {
         getManagerScript().addDatingLog(player,record,LOGGER);
    }
}
