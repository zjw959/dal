package logic.character.handler;

import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import utils.SensitiveWordsUtil;
import cn.hutool.core.util.StrUtil;

@MHandler(messageClazz = C2SPlayerMsg.SetPlayerInfo.class)
public class MReqSetPlayerInfoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        C2SPlayerMsg.SetPlayerInfo msg = (C2SPlayerMsg.SetPlayerInfo) getMessage().getData();
        PlayerInfo.Builder playerBuilder = PlayerInfo.newBuilder();
        // 名字
        String name = msg.getPlayerName();
        if (name != null) {
            // 增加屏蔽字判定
            boolean isDirtyWords = SensitiveWordsUtil.filter(name) != null;
            if (StrUtil.isBlank(name) || isDirtyWords) {
                MessageUtils.throwCondtionError(GameErrorCode.CONTAINS_ILLEGAL_CHARACTERS,
                        " name is invalid");
            }
            if (name.length() > 12) {
                MessageUtils.throwCondtionError(GameErrorCode.THE_CHARACTERS_ARE_TOO_LONG,
                        " name is invalid");
            }
            // 如果存在换行符：会导致日志出错
            name = name.replaceAll("\r|\n", "");
            player.setPlayerName(name);
            playerBuilder.setName(player.getPlayerName());
        }
        // 宣言
        String remark = msg.getRemark();
        if (remark != null) {
            if (remark.length() > 150) {
                MessageUtils.throwCondtionError(GameErrorCode.THE_CHARACTERS_ARE_TOO_LONG,
                        " name is invalid");
            }
            // 增加屏蔽字判定
            boolean isDirtyWords = SensitiveWordsUtil.filter(name) != null;
            if (isDirtyWords) {
                MessageUtils.throwCondtionError(GameErrorCode.CONTAINS_ILLEGAL_CHARACTERS,
                        " name is invalid");
            }
            player.getInfoManager().setRemark(msg.getRemark());
            playerBuilder.setRemark(msg.getRemark());
        }
        // 取得玩家信息
        MessageUtils.send(player, playerBuilder);
        // 向redis推送View
        ((PlayerViewService) PlayerViewService.getInstance())
                .updatePlayerView(player.toPlayerBean(), false, -1);
        // ------------returnEmptyBody必须放最后-----------------------
        MessageUtils.returnEmptyBody();
    }
}
