package logic.chasm.InnerHandler;

import org.game.protobuf.s2c.S2CChasmMsg.RsepEnterChasm;

import logic.character.bean.Player;
import logic.chasm.TeamDungeonManager;
import logic.support.MessageUtils;
import thread.base.LBaseHandler;

public class LRsepEnterChasmHandler extends LBaseHandler {

    public LRsepEnterChasmHandler(Player player) {
        super(player);
    }

    @Override
    public void action() throws Exception {
        TeamDungeonManager teamDungeonManager = player.getTeamDungeonManager();

        RsepEnterChasm.Builder enterChasmBuilder = RsepEnterChasm.newBuilder();
        enterChasmBuilder.addAllChashs(teamDungeonManager.buildChasmInfos());
        MessageUtils.send(player, enterChasmBuilder);
    }

}
