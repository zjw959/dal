package thread.player.hanlder;

import logic.character.PlayerViewService;
import logic.character.bean.Player;
import thread.base.GameBaseProcessor;
import thread.base.LProcessCBHandler;

/**
 * 
 * @Description 获取PlayerView的handler(异步)
 * @author LiuJiang
 * @date 2018年7月2日 下午9:13:01
 *
 */
public class LPlayerViewProcessHandler extends LProcessCBHandler {

    public LPlayerViewProcessHandler(Player player, GameBaseProcessor cp,
            LGetPlayerViewCBHandler callBackhandler) {
        super(player, cp, callBackhandler);
    }

    @Override
    public void action() throws Exception {
        Player senderPlayer = this.player;
        if (senderPlayer == null || !senderPlayer.isOnline()) {
            return;// 玩家已离线
        }
        // 取得目标玩家信息
        LGetPlayerViewCBHandler handler = (LGetPlayerViewCBHandler) getHandler();
        Player targetPlayer =
                PlayerViewService.getInstance().getPlayerView(handler.getTargetPlayerId());
        handler.setTargetPlayer(targetPlayer);
        boolean isSuccess = targetPlayer != null;
        _doCallBack(isSuccess);
    }
}
