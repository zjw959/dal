package gm.db.global.dao;

import gm.db.DBFactory;
import gm.db.global.bean.FunctionSwitchDBBean;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class FunctionSwitchDao {

    public static FunctionSwitchDBBean selectByType(int type) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectOne("t_function_switch.selectByType", type);
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static List<FunctionSwitchDBBean> selectAll() {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            return session.selectList("t_function_switch.selectAll");
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    public static boolean update(FunctionSwitchDBBean bean) {
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.insert("t_function_switch.insert", bean);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }
    }

    public static boolean deleteByTypes(List<Integer> types) {
        if (types == null || types.size() == 0) {
            return true;
        }
        try (SqlSession session = DBFactory.GLOBAL_DB.getSessionFactory().openSession()) {
            session.delete("t_function_switch.deleteByTypes", types);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.GLOBAL_DB.getLogger().error(e);
            return false;
        }
    }
}
