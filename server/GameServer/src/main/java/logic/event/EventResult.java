package logic.event;

import java.util.Map;

import data.bean.MultiConditionEventCfgBean;
import event.Event;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.TriggerEventResult;
import logic.role.bean.Role;
import logic.support.MessageUtils;
import utils.ToolMap;

/**
 * 事件结果
 */
public abstract class EventResult {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void eventResult(Player player ,Event event,Map result,Object obj){
		int eventResultType = ToolMap.getInt(EventConditionKey.EVENT_RESULT_TYPE, result);
		Map data = ToolMap.getMap(EventConditionKey.EVENT_RESULT_DATA, result);
		switch (eventResultType) {
		//1.解锁礼物
		case TriggerEventResult.UNBLOCKING_GIFT:
			int roleCid = ToolMap.getInt(EventConditionKey.ROLE_CID, data);
			int giftCid = ToolMap.getInt(EventConditionKey.GIFT_CID, data);
			player.getRoleManager().unlockGift(roleCid, giftCid);
			break;
        //3.约会
		case TriggerEventResult.DATING:
			int scriptCid = ToolMap.getInt(EventConditionKey.SCRIPT_ID, data);
			int roleId = ToolMap.getInt(EventConditionKey.ROLE_CID, data);
			Role role = player.getRoleManager().getRole(roleId);
			role.getTriggerDating().add(scriptCid);
			org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.Builder builder = 
					org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating.newBuilder();
			builder.setRoleId(roleId).addAllDatingRuleCid(role.getTriggerDating()).build();
			MessageUtils.send(player, builder);
			break;
	    //4.打工奖励
		case TriggerEventResult.WORK:
			MultiConditionEventCfgBean cfg = (MultiConditionEventCfgBean) obj;
			break;
		default:
			break;
		}
	};
}
