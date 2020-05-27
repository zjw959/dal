package gm.db.pay.bean;

import java.util.Date;

import utils.DateEx;

/**
 * 
 * @Description 支付订单
 * @author LiuJiang
 * @date 2018年7月10日 下午5:17:09
 *
 */
public class PayDBBean {
    /** 游戏订单id */
    private String order_id;
    /** 渠道订单id */
    private String channel_order_id;
    /** 渠道id */
    private String channel_id;
    /** 子渠道id */
    private String channel_appid;
    /** 玩家账号 */
    private String user_name;
    /** 玩家id */
    private long player_id;
    /** 状态 */
    private int status;
    /** 充值道具id */
    private int item_id;
    /** 出售价格金额(单位：分) */
    private int sell_amount;
    /** 实际付款金额(单位：分) */
    private int pay_amount;
    /** 附加信息 */
    private String extinfo;
    /** 创建时间 */
    private Date create_time;
    /** 收到渠道回调时间 */
    private Date callback_time;
    /** 状态变化时间 */
    private Date modify_time;
    /** 回调信息 */
    private String htnonce;
    /** 回调信息 */
    private String httoken;
    /** 是否计入收入 */
    private boolean is_count;
    
    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getChannel_order_id() {
        return channel_order_id;
    }

    public void setChannel_order_id(String channel_order_id) {
        this.channel_order_id = channel_order_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getChannel_appid() {
        return channel_appid;
    }

    public void setChannel_appid(String channel_appid) {
        this.channel_appid = channel_appid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public long getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(long player_id) {
        this.player_id = player_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    /**
     * 出售价格金额(单位：分)
     * 
     * @return
     */
    public int getSell_amount() {
        return sell_amount;
    }

    public void setSell_amount(int sell_amount) {
        this.sell_amount = sell_amount;
    }

    /**
     * 实际付款金额(单位：分)
     * 
     * @return
     */
    public int getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(int pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getExtinfo() {
        return extinfo;
    }

    public void setExtinfo(String extinfo) {
        this.extinfo = extinfo;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getCallback_time() {
        return callback_time;
    }

    public void setCallback_time(Date callback_time) {
        this.callback_time = callback_time;
    }

    public Date getModify_time() {
        return modify_time;
    }

    public void setModify_time(Date modify_time) {
        this.modify_time = modify_time;
    }

    public String toString() {
        return "{order_id:" + order_id + ",channel_order_id:" + channel_order_id + ",channel_id:"
                + channel_id + ",channel_appid:" + channel_appid + ",user_name:" + user_name
                + ",player_id:" + player_id + ",status:" + status + ",item_id:" + item_id
                + ",sell_amount:" + sell_amount + ",pay_amount:" + pay_amount + ",create_time:"
                + DateEx.format(create_time, DateEx.fmt_yyyy_MM_dd_HH_mm_ss) + ",callback_time:"
                + DateEx.format(callback_time, DateEx.fmt_yyyy_MM_dd_HH_mm_ss) + ",modify_time:"
                + DateEx.format(modify_time, DateEx.fmt_yyyy_MM_dd_HH_mm_ss) + ",extinfo:"
                + extinfo;
    }

	public String getHtnonce() {
		return htnonce;
	}

	public void setHtnonce(String htnonce) {
		this.htnonce = htnonce;
	}

	public String getHttoken() {
		return httoken;
	}

	public void setHttoken(String httoken) {
		this.httoken = httoken;
	}

	public boolean isIs_count() {
		return is_count;
	}

	public void setIs_count(boolean is_count) {
		this.is_count = is_count;
	}
}
