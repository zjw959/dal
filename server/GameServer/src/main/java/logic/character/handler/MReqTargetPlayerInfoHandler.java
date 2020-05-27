package logic.character.handler;

import org.game.protobuf.c2s.C2SPlayerMsg;

import logic.character.PlayerManager;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import message.MHandler;
import message.MessageHandler;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import thread.player.hanlder.LGetPlayerViewCBHandler;
import thread.player.hanlder.LPlayerViewProcessHandler;

@MHandler(messageClazz = C2SPlayerMsg.ReqTargetPlayerInfo.class)
public class MReqTargetPlayerInfoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        C2SPlayerMsg.ReqTargetPlayerInfo msg =
                (C2SPlayerMsg.ReqTargetPlayerInfo) getMessage().getData();
        int pid = msg.getTargetPid();
        Player _targetPlayer = PlayerManager.getPlayerByPlayerId(pid);
        int _lineIndex = player.getLineIndex();
        if (_targetPlayer != null) {
            _lineIndex = _targetPlayer.getLineIndex();
        }
        // 回调线程
        PlayerProcessor proc = PlayerProcessorManager.getInstance().getProcessor(_lineIndex);
        LPlayerViewProcessHandler handler = new LPlayerViewProcessHandler(player, proc,
                new LGetPlayerViewCBHandler(player, pid));
        PlayerViewService.getInstance().getPlayerViewAnsyc(handler);
    }
}
