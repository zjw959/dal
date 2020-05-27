package logic.mail.handler;

import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 系统获取邮件(定时从邮件数据库拉取)
 * @author LiuJiang
 * @date 2018年7月2日 下午6:24:58
 *
 */
public class LSystemGetMailsHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getMailScript().systemGetMails();
    }
}
