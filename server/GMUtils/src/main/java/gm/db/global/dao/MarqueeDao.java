package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.MarqueeDBBean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class MarqueeDao {

    public static List<MarqueeDBBean> selectExistMarquees(Date time) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("time", time);
            return session.selectList("t_marquee.selectExistMarquees", map);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }
    
    public static List<MarqueeDBBean> selectSameTimeMarquees(Date bengin_date, Date end_date) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("bengin_date", bengin_date);
            map.put("end_date", end_date);
            return session.selectList("t_marquee.selectSameTimeMarquees", map);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static MarqueeDBBean selectById(long id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectOne("t_marquee.selectById", id);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<MarqueeDBBean> selectAll() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_marquee.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static boolean insertMarquee(MarqueeDBBean bean) {
        if (bean == null) {
            return true;
        }
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.insert("t_marquee.insertMarquee", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean updateMarquee(MarqueeDBBean bean) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.update("t_marquee.updateMarquee", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean deleteMarqueesById(List<Long> ids) {
        if (ids == null || ids.size() == 0) {
            return true;
        }
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.delete("t_marquee.deleteMarqueesById", ids);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }
    }
}
