package logic.favor;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorReward;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;

import event.Event;
import logic.character.bean.Player;
import script.IScript;

public interface IFavorDatingScript extends IScript {

    public void reqFavorDatingInfo(Player player, int roleId, int favorDatingId);

    public void reqStartEntrance(Player player, int roleId, int favorDatingId, int entranceId);

    public void reqGetEventChoices(Player player, ReqGetEventChoices msg);

    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg);

    public void reqFavorDatingPanel(Player player, int roleId);

    public void reqFavorReward(Player player, ReqFavorReward msg);

    public void eventPerformed(Player player, Event event);

    public void reqRoleStatue(Player player);

    public void dealNoticeStatue(Player player, int roleId);
}
