package logic.comment;

import logic.character.bean.Player;
import script.IScript;

public abstract class ICommentScript implements IScript {
    public abstract void addComment(Player player, int id, String comment, int type, long crossDayTime);

    public abstract void likeComment(Player player, int commentPlayerId, int id, int date,
            int type);

    public abstract void getComment(Player player, int id, int type);
}
