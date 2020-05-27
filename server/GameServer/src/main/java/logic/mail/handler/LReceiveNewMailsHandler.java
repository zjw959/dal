package logic.mail.handler;

import gm.db.mail.bean.BaseMailDBBean;

import java.util.List;

import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description 接收到新邮件(玩家线程)
 * @author LiuJiang
 * @date 2018年7月2日 下午6:24:58
 *
 */
public class LReceiveNewMailsHandler extends GameInnerHandler {
    Player player;
    List<BaseMailDBBean> newMails;

    public LReceiveNewMailsHandler(Player player, List<BaseMailDBBean> newMails) {
        this.player = player;
        this.newMails = newMails;
    }

    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getMailScript().receiveNewMails(player, newMails);
    }
}
