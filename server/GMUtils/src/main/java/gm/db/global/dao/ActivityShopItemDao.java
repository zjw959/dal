package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.ActivityShopItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class ActivityShopItemDao {

	public static List<ActivityShopItem> selectActivityShopItemList() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_item_shop.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }
	
	public static ActivityShopItem selectActivityShopItemById(Integer id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
        	Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", id);
            return session.selectOne("t_item_shop.selectById", map);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }
	
    public static boolean insertActivityShopItem(ActivityShopItem config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.insert("t_item_shop.insertConfig", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
    
    public static boolean updateActivityShopItem(ActivityShopItem config) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("config", config);
            session.update("t_item_shop.updateConfig", map);
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
            session.delete("t_item_shop.deleteById", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return false;
        }
    }
}
