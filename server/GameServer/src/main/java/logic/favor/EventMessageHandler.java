package logic.favor;

import java.util.Map;

import data.bean.FavorMessageCfgBean;
import data.bean.FavorScriptCfgBean;
import logic.character.bean.Player;
import logic.favor.structs.FavorDatingData;

public interface EventMessageHandler {
    boolean execute(Player player, FavorDatingData osd, FavorMessageCfgBean fmc,
            FavorScriptCfgBean fsc, Object option, Map<Integer, Integer> items,
            Map<Integer, Integer> costItems,int roleId,int favorDatingId);
	}
