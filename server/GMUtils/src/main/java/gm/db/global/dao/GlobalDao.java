package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.GlobalDBBean;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class GlobalDao {

    public static List<GlobalDBBean> selectAll() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_global.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static GlobalDBBean selectOne(int id) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectOne("t_global.selectOne", id);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }


    public static boolean insert(GlobalDBBean bean) {
        if (bean == null) {
            return true;
        }
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.insert("t_global.insert", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }

    }
}
