package gm.db.pay.dao;

import gm.db.DBFactory;
import gm.db.pay.bean.PayDBBean;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;
import org.apache.ibatis.session.SqlSession;

import utils.ExceptionEx;

public class PayDao {
    /** 充值表前缀 */
    private static final String PAY_TABLE_PRE = "t_pay_";
    /** 充值表分表数量 */
    private static final int PAY_TABLE_SIZE = 10;

    /** 插入待处理的订单 */
    public static boolean insertPay(PayDBBean pay) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", getTableByPlayerId(pay.getPlayer_id()));
            map.put("pay", pay);
            session.insert("t_pay.insertPay", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(e);
            return false;
        }
    }

    /** 更新订单 */
    public static boolean updatePay(PayDBBean pay) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", getTableByPlayerId(pay.getPlayer_id()));
            map.put("pay", pay);
            session.update("t_pay.updatePay", map);
            session.commit();
            return true;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(e);
            return false;
        }
    }

    /**
     * 
     * @param playerId
     * @param orderId
     * @return
     */
    public static PayDBBean selectPayByPlayerIdAndOrderId(long playerId, String orderId) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", getTableByPlayerId(playerId));
            map.put("orderId", orderId);
            return session.selectOne("t_pay.selectPayByOrderId", map);
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /**
     * 
     * @param playerId
     * @param status
     * @return
     */
    public static List<PayDBBean> selectPaysByPlayerIdAndStatus(long playerId, int status) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("table", getTableByPlayerId(playerId));
            map.put("playerId", playerId);
            map.put("status", status);
            return session.selectList("t_pay.selectPaysByPlayerIdAndStatus", map);
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /**
     * 
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    public static List<PayDBBean> selectPaysByTimeAndStatus(Date startTime, Date endTime, int status) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            List<PayDBBean> allList = Lists.newArrayList();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("startTime", startTime);
            map.put("endTime", endTime);
            map.put("status", status);
            for (int i = 0; i < PAY_TABLE_SIZE; i++) {
                map.put("table", PAY_TABLE_PRE + i);
                List<PayDBBean> list = session.selectList("t_pay.selectPaysByTimeAndStatus", map);
                if (list != null && list.size() > 0) {
                    allList.addAll(list);
                }
            }
            return allList;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /** 根据渠道订单号查询 */
    public static PayDBBean selectPayByOrderId(String orderId) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderId", orderId);
            for (int i = 0; i < PAY_TABLE_SIZE; i++) {
                map.put("table", PAY_TABLE_PRE + i);
                PayDBBean bean = session.selectOne("t_pay.selectPayByOrderId", map);
                if (bean != null) {
                    return bean;
                }
            }
            return null;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /** 根据渠道订单号查询 */
    public static PayDBBean selectPayByChannelOrderId(String channelOrderId) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("channelOrderId", channelOrderId);
            for (int i = 0; i < PAY_TABLE_SIZE; i++) {
                map.put("table", PAY_TABLE_PRE + i);
                PayDBBean bean = session.selectOne("t_pay.selectPayByChannelOrderId", map);
                if (bean != null) {
                    return bean;
                }
            }
            return null;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /** 查询指定状态的所有订单 */
    public static List<PayDBBean> selectAllPaysByStatus(int status) {
        try (SqlSession session = DBFactory.PAY_DB.getSessionFactory().openSession()) {
            List<PayDBBean> allList = Lists.newArrayList();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("status", status);
            for (int i = 0; i < PAY_TABLE_SIZE; i++) {
                map.put("table", PAY_TABLE_PRE + i);
                List<PayDBBean> list = session.selectList("t_pay.selectPaysByStatus", map);
                if (list != null && list.size() > 0) {
                    allList.addAll(list);
                }
            }
            return allList;
        } catch (Exception e) {
            DBFactory.PAY_DB.getLogger().error(ExceptionEx.e2s(e));
            return null;
        }
    }

    /**
     * 根据玩家id获取对应表
     * 
     * @param playerId
     * @return
     */
    public static String getTableByPlayerId(long playerId) {
        String table = PAY_TABLE_PRE + playerId % PAY_TABLE_SIZE;
        return table;
    }

}
