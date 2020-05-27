package gm.utils;

import gm.db.DBFactory;
import gm.db.global.bean.GlobalDBBean;
import gm.db.global.dao.GlobalDao;

import java.util.List;

/**
 * 
 * @Description 全服功能开关工具类
 * @author LiuJiang
 * @date 2018年8月8日 下午9:15:23
 *
 */
public class GlobalUtils {
    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.GLOBAL_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
    }

    /**
     * 获取所有
     * 
     * @return
     */
    public static List<GlobalDBBean> selectAll() {
        return GlobalDao.selectAll();
    }

    /**
     * 根据id获取
     * 
     * @return
     */
    public static GlobalDBBean selectOne(int id) {
        return GlobalDao.selectOne(id);
    }

    /**
     * 插入或更新bean
     * 
     */
    public static int insertOrUpdateBean(int type, long longValue, String stringValue) {
        GlobalDBBean bean = _createBean(type, longValue, stringValue);
        boolean b = GlobalDao.insert(bean);
        if (!b) {
            return -1;// 更新数据库失败
        }
        return 0;
    }

    /**
     * 创建bean
     * 
     * @return
     */
    private static GlobalDBBean _createBean(int id, long longValue, String stringValue) {
        GlobalDBBean bean = new GlobalDBBean();
        bean.setId(id);
        bean.setLongValue(longValue);
        bean.setStringValue(stringValue);
        return bean;
    }
}