package logic.mail.handler;

import gm.db.mail.bean.BaseMailDBBean;

import java.util.List;

import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.LBaseCBHandler;

/**
 * 
 * @Description 玩家登录时获取邮件回调handler(玩家线程)
 * @author LiuJiang
 * @date 2018年7月2日 下午6:24:58
 *
 */
public class LLoginGetMailsCBHandler extends LBaseCBHandler {
    /** 查询返回的邮件 */
    List<BaseMailDBBean> mails;

    public LLoginGetMailsCBHandler(Player player) {
        super(player);
    }

    public void setMails(List<BaseMailDBBean> mails) {
        this.mails = mails;
    }

    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getMailScript().loginGetMails(player, mails);
    }
}
