package logic.mail.handler;

import gm.utils.MailUtils;

import java.util.List;

import thread.base.GameInnerHandler;

/**
 * 批量删除玩家邮件
 * 
 * @Description 功能描述
 * @author LiuJiang
 * @date 2018年7月5日 下午6:24:58
 *
 */
public class LDeletePlayerMailsHandler extends GameInnerHandler {
    List<Long> mailIds;

    public LDeletePlayerMailsHandler(List<Long> mailIds) {
        this.mailIds = mailIds;
    }

    @Override
    public void action() throws Exception {
        MailUtils.deletePlayerMailsByids(mailIds);
    }
}
