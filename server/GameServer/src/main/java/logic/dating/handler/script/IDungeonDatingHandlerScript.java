package logic.dating.handler.script;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dungeon.scene.DatingDungeonScene;
import logic.item.bean.Item;
import script.IScript;

public interface IDungeonDatingHandlerScript extends IScript {

    public int getHandlerIdentification();

    public void handleDatingResource(Player player, DatingScriptDTO dto);

    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    public void checkDating(Player player, DatingScriptDTO dto);

    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto);

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long dataingId);

    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record);

    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);

    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record);

    public void sendSettlementMsg(Player player, CurrentDatingBean record, int favor,
            BaseDating cfg, List<Item> itemInfos);

    public DatingDungeonScene getDatingDungeonScene(Player player);

    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record,Logger LOGGER);
}
