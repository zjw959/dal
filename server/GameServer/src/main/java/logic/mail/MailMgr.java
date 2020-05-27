package logic.mail;

import gm.db.mail.bean.BaseMailDBBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;


/**
 * 
 * @Description 邮件管理器
 * @author LiuJiang
 * @date 2018年6月10日 下午9:25:33
 *
 */
public class MailMgr extends PlayerBaseFunctionManager implements IRoleJsonConverter {
    /** 全服邮件状态 */
    private Map<Long, Integer> serverMailsStatus = new ConcurrentHashMap<Long, Integer>();
    /** 本人所有邮件缓存 */
    private transient Map<Long, BaseMailDBBean> allMails;

    public Map<Long, Integer> getServerMailsStatus() {
        return serverMailsStatus;
    }

    /** 获取本人所有邮件缓存 */
    public Map<Long, BaseMailDBBean> getAllMails() {
        return allMails;
    }

    /** 设置本人所有邮件缓存 */
    public void setAllMails(Map<Long, BaseMailDBBean> allMails) {
        this.allMails = allMails;
    }
}
