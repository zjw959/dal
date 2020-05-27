package logic.dungeon.handler;

import java.util.ArrayList;
import java.util.List;

import logic.dungeon.bean.SceneOverTO;
import logic.dungeon.scene.SingleDungeonScene;
import logic.item.bean.Item;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.msgBuilder.ShareMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg.FightOverMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import exception.AbstractLogicModelException;

/**
 * 副本：战斗结束 code = 1794
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.FightOverMsg.class)
public class MSceneOverHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSceneOverHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        FightOverMsg msg = (FightOverMsg) getMessage().getData();
        // required int32 levelCid = 1; //关卡cid
        // required bool isWin = 2; //是否胜利
        // repeated int32 goals = 3; //达成目标的下标
        // required int32 batter = 4;//最大连击数
        // required int32 pickUpTypeCount = 5;//拾取道具种类个数
        // required int32 pickUpCount = 6;//拾取道具个数
        int cid = msg.getLevelCid();
        boolean win = msg.getIsWin();
        List<Integer> goals = msg.getGoalsList();
        int batter = msg.getBatter();
        int pickUpTypeCount = msg.getPickUpTypeCount();
        int pickUpCount = msg.getPickUpCount();
        // 组装数据对象
        SceneOverTO overTo = new SceneOverTO(cid, win, goals, batter, pickUpTypeCount, pickUpCount);
        List<Item> rewards = new ArrayList<Item>();
        SingleDungeonScene sds = player.getDungeonManager().sceneOver(rewards, overTo);
        S2CDungeonMsg.FightOverMsg.Builder builder = null;
        // 失败
        if (!sds.isWin())
            builder = DungeonMsgBuilder.getSceneFailMsg(sds.getDungeonLevel());
        // 成功
        else {
            List<RewardsMsg> rewardsMsgs = new ArrayList<RewardsMsg>();
            for (Item item : rewards) {
                rewardsMsgs.add(ShareMsgBuilder.createReward(item.getTemplateId(), item.getNum()));
            }
            builder = DungeonMsgBuilder.getSceneWinMsg(sds.getDungeonLevel(), rewardsMsgs);
        }
        MessageUtils.send(player, builder);
    }
}
