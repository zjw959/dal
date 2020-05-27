package logic.constant;

/**
 * @author : DengYing
 * @CreateDate : 2017年9月8日 下午3:11:16
 * @Description ：商店常量
 */
public class StoreConstant {
    // ---------------------------------- 商店类型


    /** 普通商店 */
    public static final int COMMON_STORE_TYPE = 1;
    /** 随机商店 */
    public static final int RANDOM_STORE_TYPE = 2;
    /** 兑换商店 */
    public static final int CONVERT_SHOP_TYPE = 3;


    // ---------------------------------- 货源类型
    /** 系统商城(货物固定) */
    public static final int COMMODITY_SUPPLY_FIXATION_TYPE = 1;
    /** 玩家商城(货物随机) */
    public static final int COMMODITY_SUPPLY_RANDOM_TYPE = 2;


    // ---------------------------------- 限购类型
    /** 不限购 */
    public static final int LIMIT_NO_TYPE = 0;

    /** 刷新时间内限购 */
    public static final int LIMIT_REFRESH_TIME_TYPE = 1;

    /** 每日限购 */
    public static final int LIMIT_TO_DAY_TYPE = 2;

    /** 总数限购 */
    public static final int LIMIT_TOTAL_TYPE = 3;

    /** 全服限购 */
    public static final int LIMIT_SERVER_TYPE = 5;
    /** 全服限购夸天重置 */
    public static final int LIMIT_SERVER_ACROSSDAY_TYPE = 6;
    /** 全服永久限购 */
    public static final int LIMIT_SERVER_FOREVER_TYPE = 7;
    /** 本周限购 */
    public static final int LIMIT_WEEK_TYPE = 8;
    /** 本月限购 */
    public static final int LIMIT_MONTH_TYPE = 9;


    /** 手动刷新 */
    public static final int REFRESH_TYPE_MANUAL = 1;
    /** 自动刷新 */
    public static final int REFRESH_TYPE_TIMING = 2;


    // --------------------------商品状态-----------------------------
    /** 待上架 */
    public static final int DEFAULT_STATUS = 0;
    /** 已上架 */
    public static final int ADDED_STATUS = 1;
    /** 已下架 */
    public static final int SHELF_STATUS = 2;

    // 商店开启时间类型
    /** 一直开启 */
    public static final int STORE_OPEN_TIME_DEFAULT = 1;
    /** 每天固定时刻 */
    public static final int STORE_OPEN_TIME_TODAY = 2;
    /** 每周固定时刻 */
    public static final int STORE_OPEN_TIME_WEEK = 3;
    // 商店的开启条件
    /** 玩家等级 */
    public static final int STORE_PLV_OPEN_CONT = 1;
    /** 关卡副本 */
    public static final int STORE_DNU_OPEN_CONT = 2;



    // 商品开启时间类型
    /** 一直开启 */
    public static final int COMMODITY_OPEN_TIME_DEFAULT = 1;
    /** 每天固定时刻 */
    public static final int COMMODITY_OPEN_TIME_TODAY = 2;
    /** 每周固定时刻 */
    public static final int COMMODITY_OPEN_TIME_WEEK = 3;
    // 商品的开启条件
    /** 玩家等级 */
    public static final int COMMODITY_PLV_OPEN_CONT = 1;
    /** 关卡副本 */
    public static final int COMMODITY_DNU_OPEN_CONT = 2;

    public static final long TIME_DAY = 86400000l;

    public static final long TIME_MINUTE = 60000l;
}
