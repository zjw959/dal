package logic.mail.handler;

import gm.utils.MailUtils;

import java.util.Map;

import thread.base.GameInnerHandler;

/**
 * 邮件入库
 * 
 * @Description 功能描述
 * @author LiuJiang
 * @date 2018年7月2日 下午6:24:58
 *
 */
public class LInsertMailHandler extends GameInnerHandler {
    boolean autoReceive;
    long sender_id;
    long receiver_id;
    String title;
    String body;
    Map<Integer, Integer> items;
    String info;

    public LInsertMailHandler(boolean autoReceive, long sender_id, long receiver_id, String title,
            String body,
            Map<Integer, Integer> items, String info) {
        this.autoReceive = autoReceive;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.title = title;
        this.body = body;
        this.items = items;
        this.info = info;
    }


    @Override
    public void action() throws Exception {
        if (receiver_id > 0) {// 单人邮件
            MailUtils.sendPlayerMail(autoReceive, receiver_id, title, body, items, info);
        } else {// 全服邮件
            MailUtils.sendServerMail(title, body, items);
        }
    }
}
