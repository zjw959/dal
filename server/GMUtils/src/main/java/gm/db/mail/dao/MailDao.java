package gm.db.mail.dao;

import gm.db.DBFactory;
import gm.db.mail.bean.PlayerMailDBBean;
import gm.db.mail.bean.ServerMailDBBean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class MailDao {
    public static List<PlayerMailDBBean> selectPlayerMailsByReceiverId(long receiver_id) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_mail.selectPlayerMailsByReceiverId", receiver_id);
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<PlayerMailDBBean> selectPlayerMailsByTime(Date startTime, Date endTime) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return session.selectList("t_mail.selectPlayerMailsByTime", map);
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<ServerMailDBBean> selectServerMailsByTime(Date startTime, Date endTime) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            return session.selectList("t_mail.selectServerMailsByTime", map);
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<PlayerMailDBBean> selectPlayerMailsBeyondId(long id) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_mail.selectPlayerMailsBeyondId", id);
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<ServerMailDBBean> selectServerMailsBeyondId(long id) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_mail.selectServerMailsBeyondId", id);
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /** 获取当前最大的玩家邮件id */
    public static long selectMaxPlayerMailId() {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            Long id = session.selectOne("t_mail.selectMaxPlayerMailId");
            return id == null ? 0 : id.longValue();
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return 0;
        }
    }

    public static List<ServerMailDBBean> selectAllServerMails() {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_mail.selectAllServerMails");
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static boolean insertPlayerMail(PlayerMailDBBean bean) {
        if (bean == null) {
            return true;
        }
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            session.insert("t_mail.insertPlayerMail", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean insertServerMail(ServerMailDBBean bean) {
        if (bean == null) {
            return true;
        }
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            session.insert("t_mail.insertServerMail", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean updatePlayerMails(List<PlayerMailDBBean> mails) {
        if (mails == null || mails.size() == 0) {
            return true;
        }
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            session.insert("t_mail.updatePlayerMails", mails);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean deletePlayerMails(List<Long> mailIds) {
        if (mailIds == null || mailIds.size() == 0) {
            return true;
        }
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            session.delete("t_mail.deletePlayerMails", mailIds);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean deleteServerMails(List<Long> mailIds) {
        if (mailIds == null || mailIds.size() == 0) {
            return true;
        }
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            session.delete("t_mail.deleteServerMails", mailIds);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean deletePlayerMailsByTime(Date startTime, Date endTime) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            session.delete("t_mail.deletePlayerMailsByTime", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static boolean deleteServerMailsByTime(Date startTime, Date endTime) {
        try (SqlSession session = DBFactory.MAIL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            session.delete("t_mail.deleteServerMailsByTime", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.MAIL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
}
