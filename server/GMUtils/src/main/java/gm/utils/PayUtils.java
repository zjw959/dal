package gm.utils;

import gm.constant.PayConstant;
import gm.db.DBFactory;
import gm.db.pay.bean.PayDBBean;
import gm.db.pay.dao.PayDao;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 
 * @Description 充值订单工具类
 * @author LiuJiang
 * @date 2018年7月14日 下午6:15:23
 *
 */
public class PayUtils {
    /**
     * 重构数据库连接工厂
     * */
    public static boolean rebuildDBFactory(String dbUrl, String dbUser, String dbPwd) {
        return DBFactory.PAY_DB.rebuildSessionFactory(dbUrl, dbUser, dbPwd);
    }

    /**
     * 生成并插入游戏内订单
     * 
     * @param order_id 游戏内生成的订单号
     * @param channel_id 渠道id
     * @param channel_appid 子渠道id
     * @param user_name 玩家账号
     * @param player_id 玩家id
     * @param item_id 充值商品id
     * @param sell_amount 商品售价（单位：分）
     * @return
     */
    public static boolean createPay(String order_id, String channel_id, String channel_appid,
            String user_name, long player_id, int item_id, int sell_amount) {
        PayDBBean bean =
                _createPayDBBean(null, order_id, channel_id, channel_appid, user_name, player_id,
                        item_id, sell_amount, 0, null);
        return PayDao.insertPay(bean);
    }

    /**
     * 渠道回调时更新订单信息
     * 
     * @param channel_order_id 渠道订单号（必填）
     * @param order_id 游戏内订单号（必填）
     * @param player_id 充值玩家id（必填）
     * @param pay_amount 实际支付金额（单位：分）
     * @param callback_time 渠道回调时间
     * @param extinfo 额外信息
     */
    public static boolean updatePayByCallBack(String channel_order_id, String order_id,
            long player_id, int pay_amount, Date callback_time, String extinfo
            , String htnonce, String httoken) {
        PayDBBean bean = PayDao.selectPayByPlayerIdAndOrderId(player_id, order_id);
        if (bean == null) {
            return false;
        }
        bean.setChannel_order_id(channel_order_id);
        bean.setStatus(PayConstant.STATUS_CALL_BACK.getCode());
        bean.setPay_amount(pay_amount);
        bean.setExtinfo(extinfo);
        bean.setCallback_time(callback_time);
        bean.setModify_time(new Date());
        bean.setHtnonce(htnonce);
        bean.setHttoken(httoken);
        return PayDao.updatePay(bean);
    }

    /**
     * 获取指定玩家的充值订单
     * 
     * @param playerId
     * @param isUndealt true-未处理的 fale-所有
     * @return
     */
    public static List<PayDBBean> selectPaysByPlayerIdAndStatus(long playerId, int status) {
        return PayDao.selectPaysByPlayerIdAndStatus(playerId, status);
    }

    /**
     * 获取指定时间段内的充值订单
     * 
     * @param startTime
     * @param endTime
     * @param status
     * @return
     */
    public static List<PayDBBean> selectPaysByTimeAndStatus(Date startTime, Date endTime, int status) {
        return PayDao.selectPaysByTimeAndStatus(startTime, endTime, status);
    }

    /**
     * 跟据游戏内订单号获取订单
     * 
     * @param orderId
     * @return
     */
    public static PayDBBean selectPayByPlayerIdAndOrderId(long playerId, String orderId) {
        return PayDao.selectPayByPlayerIdAndOrderId(playerId, orderId);
    }

    /**
     * 跟据渠道订单号获取订单
     * 
     * @param channelOrderId
     * @return
     */
    public static PayDBBean selectPayByChannelOrderId(String channelOrderId) {
        return PayDao.selectPayByChannelOrderId(channelOrderId);
    }

    /**
     * 创建订单
     * 
     * @return
     */
    private static PayDBBean _createPayDBBean(String channel_order_id, String order_id,
            String channel_id, String channel_appid, String user_name, long player_id, int item_id,
            int sell_amount, int pay_amount, String extinfo) {
        PayDBBean bean = new PayDBBean();
        bean.setChannel_order_id(channel_order_id);
        bean.setOrder_id(order_id);
        bean.setChannel_id(channel_id);
        bean.setChannel_appid(channel_appid);
        bean.setUser_name(user_name);
        bean.setPlayer_id(player_id);
        bean.setItem_id(item_id);
        bean.setSell_amount(sell_amount);
        bean.setPay_amount(pay_amount);
        bean.setExtinfo(extinfo);
        bean.setCreate_time(new Date());
        bean.setModify_time(new Date());
        return bean;
    }
    
    /**
     * GM后台充值
     * 
     * @param player_id  玩家id
     * @param pay_amount	付款金额    单位分
     * @param user_name		玩家姓名
     * @param rechargeId	充值道具id
     * @return
     */
    public static boolean GMBossRecharge(long player_id,int pay_amount,
    		String user_name,int rechargeId,boolean is_count) {
    	PayDBBean bean = new PayDBBean();
    	String order_id = UUID.randomUUID().toString();
    	bean.setOrder_id(order_id);
    	bean.setChannel_id("system");
    	bean.setUser_name(user_name);
    	bean.setPlayer_id(player_id);
    	bean.setStatus(PayConstant.STATUS_CALL_BACK.getCode());
    	bean.setItem_id(rechargeId);
    	bean.setSell_amount(pay_amount);
    	bean.setPay_amount(pay_amount);
    	bean.setExtinfo("system");
    	bean.setCreate_time(new Date());
    	bean.setCallback_time(new Date());
    	bean.setIs_count(is_count);
    	return PayDao.insertPay(bean);
    }
}
