package gm.utils;

import gm.db.DBFactory;
import gm.db.global.bean.FunctionSwitchDBBean;
import gm.db.global.dao.FunctionSwitchDao;

import java.util.List;

/**
 * 
 * @Description 全服功能开关工具类
 * @author LiuJiang
 * @date 2018年8月8日 下午9:15:23
 *
 */
public class FunctionSwitchUtils {
    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.GLOBAL_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
    }

    /**
     * 根据type获取
     * 
     * @return
     */
    public static FunctionSwitchDBBean selectOneByType(int type) {
        return FunctionSwitchDao.selectByType(type);
    }

    /**
     * 获取所有
     * 
     * @return
     */
    public static List<FunctionSwitchDBBean> selectAll() {
        return FunctionSwitchDao.selectAll();
    }

    /**
     * 修改开关状态
     * 
     * @param type 类型
     * @param status 状态
     * @return 0-成功 非0-失败
     */
    public static int updateBean(int type, int status) {
        FunctionSwitchDBBean bean = _createBean(type, status);
        boolean b = FunctionSwitchDao.update(bean);
        if (!b) {
            return -2;// 更新数据库失败
        }
        return 0;
    }

    /**
     * 删除指定的功能开关
     * 
     * @param ids
     * @return
     */
    public static boolean deleteByTypes(List<Integer> types) {
        return FunctionSwitchDao.deleteByTypes(types);
    }

    /**
     * 创建功能开关
     * 
     * @return
     */
    private static FunctionSwitchDBBean _createBean(int type, int status) {
        FunctionSwitchDBBean bean = new FunctionSwitchDBBean();
        bean.setType(type);
        bean.setStatus(status);
        return bean;
    }
}