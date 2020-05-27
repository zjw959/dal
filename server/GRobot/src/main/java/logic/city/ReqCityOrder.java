package logic.city;

public interface ReqCityOrder {
    
    /** 请求加入GM道具 */
    public static final int REQ_GM_ITEMS = 1;
    /**请求获取建筑信息**/
    public static final int REQ_NEWBUILDING_INFO=2;
    /**删除建筑解封事件**/
    public static final int REQ_REMINDSUCCESS=3;
    /**获取兼职列表**/
    public static final int REQ_GETPART_LIST=4;
    /**请求兼职任务**/
    public static final int REQ_DOPARTTIMEJOB=5;
    /**领取兼职任务奖励**/
    public static final int REQ_GETPARTTIMEAWAR=6;
    /**请求放弃兼职**/
    public static final int REQ_GIVE_UP_JOB=7;
    /**请求获取抓娃娃信息**/
    public static final int REQ_GETGASHAPONINFO=8;
    /**请求开始抓娃娃**/
    public static final int REQ_START_GASHAPON=9;
    /**请求验证抓娃娃**/
    public static final int REQ_CHECK_GASHAPON=10;
    /**请求刷新抓娃娃蛋池**/
    public static final int REQ_REFRESHGASHAPONPOOL=11;
    /**请求料理数据**/
    public static final int REQ_GETFOODBASEINFO=12;
    /**请求请求制作料理**/
    public static final int REQ_COOKFOODBASE=13;
    /**请求QTE完成上传积分**/
    public static final int REQ_UPLOADQTEINTEGRAL=14;
    /**请求料理奖励**/
    public static final int REQ_GETFOODBASEAWARD=15;
    /**请求手工制作信息**/
    public static final int REQ_GETHANDWORKINFO=16;
    /**请求手工制作**/
    public static final int REQ_DOHANDWORK=17;
    /**验证手工操作完成上传积分**/
    public static final int REQ_UPLOADHANDINTEGRAL=18;
    /**请求手工奖励**/
    public static final int REQ_GETHANDWORKAWARD=19;

}
