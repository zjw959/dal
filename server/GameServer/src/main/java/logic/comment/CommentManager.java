package logic.comment;

import logic.basecore.PlayerBaseFunctionManager;
import logic.support.LogicScriptsUtils;

public class CommentManager extends PlayerBaseFunctionManager {

    /**
     * 增加一条评论
     * 
     * @param playerId
     * @param id
     * @param date
     * @param comment
     * @param type
     */
    public void addComment(int id, String comment, int type) {
        LogicScriptsUtils.getICommentScript().addComment(player, id, comment, type,getGameAcrossDay());
    }


    /**
     * 点赞一条评论
     * 
     * @param playerId
     * @param id
     * @param date
     * @param comment
     * @param type
     * @param i
     */
    public void likeComment(int commentPlayerId, int id, int date, int type) {
        LogicScriptsUtils.getICommentScript().likeComment(player, commentPlayerId, id, date, type);
    }


    /**
     * 取得评价排行榜
     * 
     * @param id
     * @param type
     */
    public void getComment(int id, int type) {
        LogicScriptsUtils.getICommentScript().getComment(player, id, type);
    }

}
