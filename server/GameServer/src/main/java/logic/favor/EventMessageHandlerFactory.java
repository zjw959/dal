package logic.favor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.bean.FavorMessageCfgBean;
import data.bean.FavorScriptCfgBean;
import logic.character.bean.Player;
import logic.favor.structs.FavorDatingConst;
import logic.favor.structs.FavorDatingData;


public class EventMessageHandlerFactory {
	EventMessageHandlerFactory next;

	public Map<Integer, EventMessageHandler> createEventMessageHandlers() {
		Map<Integer, EventMessageHandler> handlers = new HashMap<Integer, EventMessageHandler>();
		
        // 文本提示
        handlers.put(FavorDatingConst.EVENT_MESSAGE_TYPE_TIPS, new EventMessageHandler() {
			@Override
            public boolean execute(Player player, FavorDatingData osd, FavorMessageCfgBean fmc,
                    FavorScriptCfgBean fsc, Object option, Map<Integer, Integer> items,
					Map<Integer, Integer> costItems,int roleId,int favorDatingId) {
				return true;
			}
		});
		
        // 选择
        handlers.put(FavorDatingConst.EVENT_MESSAGE_TYPE_CHOICE, new EventMessageHandler() {
			
			@SuppressWarnings("unchecked")
			@Override
            public boolean execute(Player player, FavorDatingData osd, FavorMessageCfgBean fmc,
                    FavorScriptCfgBean fsc, Object option, Map<Integer, Integer> items,
					Map<Integer, Integer> costItems,int roleId,int favorDatingId) {
                // 取选项
				int optionChoice = (Integer)option;
				Map<Integer, Integer> cost = null;
				Map<Integer, Integer> reward = null;
                Map<Integer, Integer> quality = null;
				List<Integer> costMark = null;
				List<Integer> rewardMark = null;
				switch(optionChoice){
                    case FavorDatingConst.EVENT_MESSAGE_CHOICE_1:
                        cost = (Map<Integer, Integer>) fmc.getOptionCost1()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        costMark = (List<Integer>) fmc.getOptionCost1()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                        reward = (Map<Integer, Integer>) fmc.getOptionReward1()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        quality = (Map<Integer, Integer>) fmc.getOptionReward1()
                                .get(FavorDatingConst.SCRIPT_CONDITION_QUALITY);
                        rewardMark = (List<Integer>) fmc.getOptionReward1()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
					break;
                    case FavorDatingConst.EVENT_MESSAGE_CHOICE_2:
                        cost = (Map<Integer, Integer>) fmc.getOptionCost2()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        costMark = (List<Integer>) fmc.getOptionCost2()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
                        reward = (Map<Integer, Integer>) fmc.getOptionReward2()
                                .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                        quality = (Map<Integer, Integer>) fmc.getOptionReward2()
                                .get(FavorDatingConst.SCRIPT_CONDITION_QUALITY);
                        rewardMark = (List<Integer>) fmc.getOptionReward2()
                                .get(FavorDatingConst.SCRIPT_CONDITION_MARK);
					break;
				}
                if (cost != null
                        && !player.getFavorDatingManager().isItemsEnough(player, cost, osd))
					return false;
                if (costMark != null
                        && !player.getFavorDatingManager().isMarksEnough(costMark, osd))
					return false;
				if(cost != null) {
                    // 扣除物品
                    player.getFavorDatingManager().removeItems(cost, osd);
                    player.getFavorDatingManager().combineValueCount(costItems, cost, true);
				}
				if(costMark != null){
					for (Integer mark : costMark) {
                        player.getFavorDatingManager().removeMark(osd, mark);
					}
				}
				if(reward != null) {
                    // 获取物品
                    player.getFavorDatingManager().addItems(osd, reward);
                    player.getFavorDatingManager().combineValueCount(items, reward, true);
				}
				if(rewardMark != null) {
					for (Integer mark : rewardMark) {
                        player.getFavorDatingManager().addMark(osd, mark);
					}
				}
                if (quality != null) {
                    player.getFavorDatingManager().addQuality(osd, quality);
                }
                // 移除入口
                if(fsc.getDelete()){
                    player.getFavorDatingManager().removeEntrance(player, osd, fsc.getId());
                    player.getFavorDatingManager().sendParagraghChange(player, osd, roleId, favorDatingId);
                }
				return true;
			}
		});
		
        // 直接奖励
        handlers.put(FavorDatingConst.EVENT_MESSAGE_TYPE_REWARD, new EventMessageHandler() {
			
			@SuppressWarnings("unchecked")
			@Override
            public boolean execute(Player player, FavorDatingData osd, FavorMessageCfgBean omc,
                    FavorScriptCfgBean osc, Object option, Map<Integer, Integer> items,
					Map<Integer, Integer> costItems,int roleId,int favorDatingId) {
                Map<Integer, Integer> reward = (Map<Integer, Integer>) omc.getReward()
                        .get(FavorDatingConst.SCRIPT_CONDITION_ITEM);
                List<Integer> mark =
                        (List<Integer>) omc.getReward().get(FavorDatingConst.SCRIPT_CONDITION_MARK);
				if(reward != null) {
                    // 获取物品
                    player.getFavorDatingManager().addItems(osd, reward);
                    player.getFavorDatingManager().combineValueCount(items, reward, true);
				}
				if(mark != null) {
					for (Integer markId : mark) {
                        player.getFavorDatingManager().addMark(osd, markId);
					}
				}
                // 移除入口
                if(osc.getDelete()){
                    player.getFavorDatingManager().removeEntrance(player, osd, osc.getId()); 
                    player.getFavorDatingManager().sendParagraghChange(player, osd, roleId, favorDatingId);
                }
				return true;
			}
		});
		
		if (next != null)
			handlers.putAll(next.createEventMessageHandlers());
		return handlers;
	}

	public EventMessageHandlerFactory getNext() {
		return next;
	}

	public void setNext(EventMessageHandlerFactory next) {
		this.next = next;
	}

}
