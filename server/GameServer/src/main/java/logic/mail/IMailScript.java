package logic.mail;

import gm.db.mail.bean.BaseMailDBBean;

import java.util.List;

import logic.character.bean.Player;
import logic.mail.handler.LLoginGetMailsCBHandler;

import org.game.protobuf.c2s.C2SMailMsg.MailHandleMsg;

import script.IScript;

public abstract class IMailScript implements IScript {

    /** 系统自动定时拉取需处理的邮件 */
    public abstract void systemGetMails();

    /** 玩家登录时获取邮件线程handler(邮件线程) */
    public abstract boolean loginGetMailsProce(int playerId, LLoginGetMailsCBHandler handler);
    
    /** 玩家登录时拉取需处理的邮件 */
    public abstract void loginGetMails(Player player, List<BaseMailDBBean> mails);

    /** 收到新邮件 */
    public abstract void receiveNewMails(Player player, List<BaseMailDBBean> mails);

    /** 操作邮件 */
    public abstract void handleMails(Player player, MailHandleMsg msg);

}
