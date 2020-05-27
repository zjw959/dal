package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.ActivityConfigure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class ActivityConfigureDao {

	public static List<ActivityConfigure> selectActivityConfigureList() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_activity.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }
	
	public static ActivityConfigure selectActivityConfigureById(Integer id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            return session.selectOne("t_activity.selectById", map);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }
	
    public static boolean insertActivityConfigure(ActivityConfigure config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.insert("t_activity.insertConfig", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
    
    public static boolean updateActivityConfigure(ActivityConfigure config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.update("t_activity.updateConfig", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
    
    public static boolean deleteActivityById(Integer id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            session.delete("t_activity.deleteById", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
    
    public static boolean updateActivityByIdAndChangeTime(Integer id,Long changeTime) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            map.put("changeTime", changeTime);
            map.put("updateStatus", 2);
            session.update("t_activity.updateLastChangeTime", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
}
