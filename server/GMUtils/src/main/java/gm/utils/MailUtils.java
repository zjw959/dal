package gm.utils;

import gm.constant.MailConstant;
import gm.db.DBFactory;
import gm.db.mail.bean.PlayerMailDBBean;
import gm.db.mail.bean.ServerMailDBBean;
import gm.db.mail.dao.MailDao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import utils.IdCreator;

/**
 * 
 * @Description 邮件工具类
 * @author LiuJiang
 * @date 2018年7月6日 下午6:15:23
 *
 */
public class MailUtils {
    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.MAIL_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
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
     * @param info 额外补充信息
     */
    public static boolean sendPlayerMail(boolean autoReceive, long receiver_id, String title,
            String body, Map<Integer, Integer> items, String info) {
        long sender_id = 0;// 目前只有系统邮件
        PlayerMailDBBean bean =
                _createPlayerMail(autoReceive, sender_id, receiver_id, title, body, items, info);
        return MailDao.insertPlayerMail(bean);
    }

    /**
     * 发送全服邮件
     * 
     * @param title 标题（必填）
     * @param body 正文（必填）
     * @param items 奖励附件
     */
    public static boolean sendServerMail(String title, String body, Map<Integer, Integer> items) {
        ServerMailDBBean bean = _createServerMail(title, body, items);
        return MailDao.insertServerMail(bean);
    }

    /**
     * 获取指定玩家的所有单人邮件
     * 
     * @param playerId
     * @return
     */
    public static List<PlayerMailDBBean> selectPlayerMailsByPlayerId(long playerId) {
        List<PlayerMailDBBean> mails = MailDao.selectPlayerMailsByReceiverId(playerId);
        return mails;
    }


    /**
     * 获取指定时间段内的单人邮件
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<PlayerMailDBBean> selectPlayerMailsByTime(Date startTime, Date endTime) {
        List<PlayerMailDBBean> mails = MailDao.selectPlayerMailsByTime(startTime, endTime);
        return mails;
    }

    /**
     * 获取指定时间段内的全服邮件
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<ServerMailDBBean> selectServerMailsByTime(Date startTime, Date endTime) {
        List<ServerMailDBBean> mails = MailDao.selectServerMailsByTime(startTime, endTime);
        return mails;
    }

    /**
     * 删除指定的全服邮件
     * 
     * @param mailIds
     * @return
     */
    public static boolean deleteServerMailsByids(List<Long> mailIds) {
        return MailDao.deleteServerMails(mailIds);
    }

    /**
     * 删除指定时间段的单人邮件
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static boolean deletePlayerMailsByTime(Date startTime, Date endTime) {
        return MailDao.deletePlayerMailsByTime(startTime, endTime);
    }

    /**
     * 删除指定时间段的全服邮件
     * 
     * @param mailIds
     * @return
     */
    public static boolean deleteServerMailsByTime(Date startTime, Date endTime) {
        return MailDao.deleteServerMailsByTime(startTime, endTime);
    }

    /**
     * 删除指定的单人邮件
     * 
     * @param mailIds
     * @return
     */
    public static boolean deletePlayerMailsByids(List<Long> mailIds) {
        return MailDao.deletePlayerMails(mailIds);
    }

    /**
     * 创建单人邮件
     * 
     * @return
     */
    private static PlayerMailDBBean _createPlayerMail(boolean autoReceive, long sender_id,
            long receiver_id, String title, String body, Map<Integer, Integer> items, String info) {
        PlayerMailDBBean bean = new PlayerMailDBBean();
        int serverId = 0;
        String serverId_str = System.getProperty("serverId");
        if (serverId_str != null) {
            serverId = Integer.parseInt(serverId_str);
        }
        int specialId = 0;
        String specialId_str = System.getProperty("specialId");
        if (specialId_str != null) {
            specialId = Integer.parseInt(specialId_str);
        }
        bean.setId(IdCreator.getUniqueId(serverId, specialId));
        bean.setStatus(autoReceive ? MailConstant.STATUS_AUTO.getCode()
                : MailConstant.STATUS_DEFAULT.getCode());
        bean.setSender_id(sender_id);
        bean.setReceiver_id(receiver_id);
        bean.setTitle(title);
        bean.setBody(body);
        bean.setItems((items != null && items.size() > 0) ? GsonUtils.toJsonTree(items).toString()
                : null);
        bean.setInfo(info);
        bean.setCreate_date(new Date());
        bean.setModify_date(new Date());
        return bean;
    }

    /**
     * 创建全服邮件
     * 
     * @return
     */
    private static ServerMailDBBean _createServerMail(String title, String body,
            Map<Integer, Integer> items) {
        ServerMailDBBean bean = new ServerMailDBBean();
        int serverId = 0;
        String serverId_str = System.getProperty("serverId");
        if (serverId_str != null) {
            serverId = Integer.parseInt(serverId_str);
        }
        int specialId = 0;
        String specialId_str = System.getProperty("specialId");
        if (specialId_str != null) {
            specialId = Integer.parseInt(specialId_str);
        }
        bean.setId(IdCreator.getUniqueId(serverId, specialId));
        bean.setTitle(title);
        bean.setBody(body);
        bean.setItems((items != null && items.size() > 0) ? GsonUtils.toJsonTree(items).toString()
                : null);
        bean.setCreate_date(new Date());
        bean.setModify_date(new Date());
        return bean;
    }
}
