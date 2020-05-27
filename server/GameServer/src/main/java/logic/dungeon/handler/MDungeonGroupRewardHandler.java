package logic.dungeon.handler;

import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.bean.GroupRewardTO;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;

import exception.AbstractLogicModelException;

/**
 * 领取副本组奖励 code = 1802
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.GetLevelGroupReward.class)
public class MDungeonGroupRewardHandler extends MessageHandler {
	private static final String REWARD_SEPARATOR = "_";
	private static final int REWARD_BLANK_HEAD = 1;
	private static final int REWARD_INFO_ID = 0;
	private static final int REWARD_INFO_STAR = 1;
	private static final int REWARD_INFO_HEART = 2;
	private static final Logger LOGGER = Logger
			.getLogger(MDungeonGroupRewardHandler.class);

    @Override
	public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.NORMAL_DUNGEON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:normal_dungeon");
        }
		logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
		if (player == null) {
			LOGGER.error(this.getClass().getName() + " can not find player ");
			return;
		}
		C2SDungeonMsg.GetLevelGroupReward msg = (C2SDungeonMsg.GetLevelGroupReward) getMessage()
				.getData();
		int levelGroupCid = msg.getCid();
		int difficulty = msg.getDifficulty();
		String rewardInfo = msg.getStarNum();

		// 档位奖励id特殊规则
		// 跳过第一个字符,客户端不能table key不能以数字开头,因此第一个字符为占位字符
		String starInfo = rewardInfo.substring(REWARD_BLANK_HEAD);
		String[] starInfos = starInfo.split(REWARD_SEPARATOR);
		int rewardId = Integer.valueOf(starInfos[REWARD_INFO_ID]);
		int starNums = Integer.valueOf(starInfos[REWARD_INFO_STAR]);
		int heartNums = Integer.valueOf(starInfos[REWARD_INFO_HEART]);
		// 组装数据
        GroupRewardTO rewardTo =
                new GroupRewardTO(levelGroupCid, difficulty, rewardInfo, starInfo, rewardId,
                        starNums, heartNums);
        // 逻辑模块调用
        DungeonGroupBean levelGroup = player.getDungeonManager().handleGroupReward(rewardTo);
		S2CDungeonMsg.GetLevelGroupReward.Builder builder = DungeonMsgBuilder
				.getLevelGroupReward(levelGroupCid, difficulty, rewardId,
						levelGroup);

		logic.support.MessageUtils.send(player, builder);
	}
}
