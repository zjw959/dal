package logic.dating.handler.script;

import java.util.List;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import script.IScript;

public interface ITriggerDatingHandlerScript extends IScript {

    public int getHandlerIdentification();

    public void checkDating(Player player, DatingScriptDTO dto);

    public void handleDatingResource(Player player, DatingScriptDTO dto);

    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record);

    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);

    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record,Logger LOGGER);

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId);

    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

}
