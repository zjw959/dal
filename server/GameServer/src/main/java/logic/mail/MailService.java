package logic.mail;

import gm.db.mail.bean.PlayerMailDBBean;
import gm.db.mail.bean.ServerMailDBBean;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import logic.constant.EReason;
import logic.mail.handler.LDeletePlayerMailsHandler;
import logic.mail.handler.LInsertMailHandler;
import logic.mail.handler.LUpdatePlayerMailsHandler;
import thread.sys.base.SysService;

/**
 * 
 * @Description 邮件服务类
 * @author LiuJiang
 * @date 2018年7月2日 下午6:20:49
 *
 */
public class MailService extends SysService {

    /** 定时获取邮件的时间间隔 */
    public static int DELAY = 30 * 1000;
    /** 上次获取邮件的截止时间 */
    private long lastEndTime = -1;
    /** 全服邮件缓存 */
    private Map<Long, ServerMailDBBean> serverMails =
            new ConcurrentHashMap<Long, ServerMailDBBean>();

    public long getLastEndTime() {
        return lastEndTime;
    }

    public void setLastEndTime(long lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public Map<Long, ServerMailDBBean> getServerMails() {
        return serverMails;
    }

    /** 缓存全服邮件 */
    public void putServerMail(ServerMailDBBean mail) {
        serverMails.put(mail.getId(), mail);
    }

    /**
     * 发送单人邮件
     * 
     * @param autoReceive 是否自动提取(玩家看不到邮件)（必填）
     * @param receiver_id 接收者playerid（必填）
     * @param title 标题（必填）
     * @param body 正文（必填）
     * @param items 奖励附件
     * @param reason 操作原因（必填）
     */
    public void sendPlayerMail(boolean autoReceive, long receiver_id, String title, String body,
            Map<Integer, Integer> items, EReason reason) {
        int sender_id = 0;// 目前只有系统邮件
        String info_ex = String.valueOf(reason.value());// 邮件额外信息补充(reason)
        this.getProcess().executeInnerHandler(
                new LInsertMailHandler(autoReceive, sender_id, receiver_id, title, body, items,
                        info_ex));
    }

    /**
     * 发送全服邮件
     * 
     * @param title 标题（必填）
     * @param body 正文（必填）
     * @param items 额外补充信息
     */
    public void sendServerMail(String title, String body, Map<Integer, Integer> items) {
        this.getProcess().executeInnerHandler(
                new LInsertMailHandler(false, 0, 0, title, body, items, null));
    }

    /** 批量更新玩家邮件 */
    public void updatePlayerMails(List<PlayerMailDBBean> mails) {
        if (mails == null || mails.size() == 0) {
            return;
        }
        this.getProcess().executeInnerHandler(new LUpdatePlayerMailsHandler(mails));
    }

    /** 批量删除玩家邮件 */
    public void deletePlayerMails(List<Long> mailIds) {
        if (mailIds == null || mailIds.size() == 0) {
            return;
        }
        this.getProcess().executeInnerHandler(new LDeletePlayerMailsHandler(mailIds));
    }


    public static MailService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        MailService instance;

        private Singleton() {
            instance = new MailService();
        }

        MailService getInstance() {
            return instance;
        }
    }
}
