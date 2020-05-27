package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.ActivityTaskItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class ActivityTaskItemDao {


    public static List<ActivityTaskItem> selectActivityTaskItemList() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_item_task.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static ActivityTaskItem selectActivityTaskItemById(Integer id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            return session.selectOne("t_item_task.selectById", map);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static boolean insertActivityTaskItem(ActivityTaskItem config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.insert("t_item_task.insertConfig", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }

    public static boolean updateActivityTaskItem(ActivityTaskItem config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.update("t_item_task.updateConfig", map);
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
            session.delete("t_item_task.deleteById", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }


}
