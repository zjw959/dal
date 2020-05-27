package gm.utils;

import gm.db.DBFactory;
import gm.db.global.bean.MarqueeDBBean;
import gm.db.global.dao.MarqueeDao;

import java.util.Date;
import java.util.List;

import utils.IdCreator;

/**
 * 
 * @Description 跑马灯工具类
 * @author LiuJiang
 * @date 2018年7月30日 下午9:15:23
 *
 */
public class MarqueeUtils {
    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.GLOBAL_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
    }

    /**
     * 获取当前进行中的跑马灯
     * 
     * @return
     */
    public static List<MarqueeDBBean> selectExistMarquees() {
        return MarqueeDao.selectExistMarquees(new Date());
    }

    /**
     * 根据id获取跑马灯
     * 
     * @return
     */
    public static MarqueeDBBean selectOneById(long id) {
        return MarqueeDao.selectById(id);
    }

    /**
     * 获取所有跑马灯
     * 
     * @return
     */
    public static List<MarqueeDBBean> selectAll() {
        return MarqueeDao.selectAll();
    }

    /**
     * 添加跑马灯
     * 
     * @param body 500个汉字以内
     * @param weight 权重（值越大越优先）
     * @param loop 循环次数限制
     * @param interval_time 间隔时间（秒）
     * @param begin_date
     * @param end_date
     * @return
     */
    public static int addMarquee(String body, int weight, int loop, int interval_time,
            Date begin_date,
            Date end_date) {
        if (begin_date.getTime() >= end_date.getTime()) {
            return -1;// 开始时间大于结束时间
        }
        if (end_date.getTime() <= System.currentTimeMillis()) {
            return -2;// 结束时间小于当前时间
        }
        if (body.length() > 1000) {
            return -3;// 内容过长
        }
        MarqueeDBBean bean =
                _createMarquee(body, weight, loop, interval_time, begin_date, end_date);
        Boolean b = MarqueeDao.insertMarquee(bean);
        if (!b) {
            return -5;// 插入数据库失败
        }
        return 0;
    }

    /**
     * 修改跑马灯
     * 
     * @param body 500个汉字以内
     * @param weight 权重（值越大越优先）
     * @param loop 循环次数限制
     * @param interval_time 循环间隔时间（秒）
     * @param begin_date
     * @param end_date
     * @return 0-成功 非0-失败
     */
    public static int updateMarquee(long id, String body, int weight, int loop, int interval_time,
            Date begin_date, Date end_date) {
        if(begin_date.getTime()>=end_date.getTime()){
            return -1;//开始时间大于结束时间
        }
        if(end_date.getTime()<=System.currentTimeMillis()){
            return -2;//结束时间小于当前时间
        }
        if (body.length() > 1000) {
            return -3;// 内容过长
        }
        MarqueeDBBean bean = selectOneById(id);
        if (bean == null) {
            return -5;// id错误
        }
        bean.setBody(body);
        bean.setWeight(weight);
        bean.setLoop_count(loop);
        bean.setInterval_time(interval_time);
        bean.setBegin_date(begin_date);
        bean.setEnd_date(end_date);
        boolean b = MarqueeDao.updateMarquee(bean);
        if (!b) {
            return -6;// 插入数据库失败
        }
        return 0;
    }

    /**
     * 删除指定的跑马灯
     * 
     * @param ids
     * @return
     */
    public static boolean deleteMarqueesById(List<Long> ids) {
        return MarqueeDao.deleteMarqueesById(ids);
    }

    /**
     * 创建跑马灯
     * 
     * @return
     */
    private static MarqueeDBBean _createMarquee(String body, int weight, int loop,
            int interval_time,
            Date begin_date, Date end_date) {
        MarqueeDBBean bean = new MarqueeDBBean();
        int serverId = 0;
        String serverId_str = System.getProperty("serverId");
        if (serverId_str != null) {
            serverId = Integer.parseInt(serverId_str);
        }
        int specialId = 0;
        String specialId_str = System.getProperty("specialId");
        if (specialId_str != null) {
            specialId = Integer.parseInt(specialId_str);
        }
        bean.setId(IdCreator.getUniqueId(serverId, specialId));
        bean.setBody(body);
        bean.setWeight(weight);
        bean.setLoop_count(loop);
        bean.setInterval_time(interval_time);
        bean.setCreate_date(new Date());
        bean.setBegin_date(begin_date);
        bean.setEnd_date(end_date);
        return bean;
    }
}