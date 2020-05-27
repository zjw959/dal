package logic.mail.handler;

import gm.db.mail.bean.PlayerMailDBBean;
import gm.db.mail.dao.MailDao;

import java.util.List;

import thread.base.GameInnerHandler;

/**
 * 批量更新玩家邮件
 * 
 * @Description 功能描述
 * @author LiuJiang
 * @date 2018年7月5日 下午6:24:58
 *
 */
public class LUpdatePlayerMailsHandler extends GameInnerHandler {
    List<PlayerMailDBBean> mails;

    public LUpdatePlayerMailsHandler(List<PlayerMailDBBean> mails) {
        this.mails = mails;
    }

    @Override
    public void action() throws Exception {
        MailDao.updatePlayerMails(mails);
    }
}
