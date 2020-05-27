package logic.novelDating;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;

import logic.character.bean.Player;
import script.IScript;

public interface INovelDatingScript extends IScript {

    public void reqNovelDatingInfo(Player player, int novelDatingId);

    public void reqStartNovelEntrance(Player player, int favorDatingId, int entranceId);

    public void reqGetEventChoices(Player player, ReqGetEventChoices msg);

    public void reqChooseEntrance(Player player, ReqChooseEntranceEvent msg);
}
