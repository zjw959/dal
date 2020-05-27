package javascript.http.fix;

import java.util.List;

import org.apache.log4j.Logger;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import net.http.HttpRequestWrapper;
import script.IHttpScript;
import thread.base.LBaseHandler;
import thread.player.PlayerProcessorManager;

/**
 * 仅对当前在线玩家执行一次的操作
 */
public class FixBugPlayerScript implements IHttpScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.FIX_BUG_PlAYER.Value();
    }

    @Override
    public String execute(HttpRequestWrapper httpRequest) {
        LOGGER.info("hello i am FixBugPlayerScript.");

        List<Player> players = PlayerManager.getAllPlayers();
        for (Player player : players) {
            PlayerProcessorManager.getInstance().addPlayerHandler(player,
                    new LPlayerFixBugHandler(player));
        }

        return HttpRequestWrapper.HttpRet.OK.desc();
    }

    private static final Logger LOGGER = Logger.getLogger(FixBugPlayerScript.class);
}


class LPlayerFixBugHandler extends LBaseHandler {
    private static final Logger LOGGER = Logger.getLogger(LPlayerFixBugHandler.class);


    public LPlayerFixBugHandler(Player player) {
        super(player);
    }

    @Override
    public void action() throws Exception {
        // 确保该player还在线,并且是同一个player
        Player player = PlayerManager.getPlayerByPlayerId(this.player.getPlayerId());
        if (player == this.player) {
            LOGGER.info("begin fix bug. playerid:" + player.getPlayerId());

        }
    }
}
