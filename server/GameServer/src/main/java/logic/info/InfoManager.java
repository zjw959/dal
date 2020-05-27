package logic.info;

import logic.basecore.IAcrossDay;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IView;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.support.LogicScriptsUtils;

import org.apache.log4j.Logger;

import utils.DateEx;
import utils.GsonUtils;

import com.google.gson.JsonElement;

/**
 * 
 * @Description 信息管理器
 * @author LiuJiang
 * @date 2018年6月9日 下午5:00:03
 *
 */
public class InfoManager extends PlayerBaseFunctionManager implements ICreateRoleInitialize,
        IAcrossDay, IRoleJsonConverter, IView, ICreatePlayerInitialize {
    public long getLastTickTime() {
        return lastTickTime;
    }

    public void setLastTickTime(long lastTickTime) {
        this.lastTickTime = lastTickTime;
    }

    public long getNextAntiTime() {
        return nextAntiTime;
    }

    public void setNextAntiTime(long nextAntiTime) {
        this.nextAntiTime = nextAntiTime;
    }

    public void setTodayOnlineTime(long todayOnlineTime) {
        this.todayOnlineTime = todayOnlineTime;
    }

    private static final Logger LOGGER = Logger.getLogger(InfoManager.class);
    public static final long UNIT_TIME = 1 * DateEx.TIME_HOUR;
    /** 宣言 */
    private String remark = "";
    /** 体力 */
    private int strength;
    /** 上次体力恢复时间 */
    private long lastRecoverStrengthTime;
    /** 助战id */
    private int helpFightHeroCid;
    /** 专注 **/
    private int absorbed;
    /** 魅力值 **/
    private int glamour;
    /** 温柔 **/
    private int tender;
    /** 知识 **/
    private int knowledge;
    /** 运气 **/
    private int fortune;
    /** 当天累计恢复体力 **/
    private int recoverStrength;
    /** 今日在线时长 **/
    private long todayOnlineTime;
    /** 上次tick时间 **/
    private transient long lastTickTime;
    /** 下次通知防沉迷时间 **/
    private transient long nextAntiTime;
    /** 防沉迷状态 **/
    private transient int antiStatus;

    @Override
    public void createRoleInitialize() throws Exception {
        strength = getLevelMaxStrength();
    }

    /** 获取体力 */
    public int getStrength() {
        return strength;
    }

    /** 非必须禁止使用 */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /** 宣言 */
    public String getRemark() {
        return remark;
    }

    /** 宣言 */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getHelpFightHeroCid() {
        return helpFightHeroCid;
    }

    public void setHelpFightHeroCid(int helpFightHeroCid) {
        this.helpFightHeroCid = helpFightHeroCid;
    }
  
    public long getLastRecoverStrengthTime() {
        return lastRecoverStrengthTime;
    }

    public void setLastRecoverStrengthTime(long lastRecoverStrengthTime) {
        this.lastRecoverStrengthTime = lastRecoverStrengthTime;
    }

    @Override
    public void tick() {
        LogicScriptsUtils.getIInfoManagerScript().tick(player);
    }

    /**
     * 改变角色体力
     * 
     * @param num
     * @param isForce 是否可以强制超过当前等级的体力上限
     * @param isNotify
     * @return
     */
    public boolean changeStrength(int num, boolean isForce) {
        return LogicScriptsUtils.getIInfoManagerScript().changeStrength(this, num, isForce);
    }


    /** 获取当前等级体力上限 */
    public int getLevelMaxStrength() {
        return LogicScriptsUtils.getIInfoManagerScript().getLevelMaxStrength(player);
    }

    /** 通知客户端体力变化 */
    public void sendStrengthUpdate() {
        LogicScriptsUtils.getIInfoManagerScript().sendStrengthUpdate(player);
    }

    /** 专注变化 **/
    public void changeAbsorbed(int change) {
        LogicScriptsUtils.getIInfoManagerScript().changeAbsorbed(this, change);
    }

    /** 魅力变化 **/
    public void changeGlamour(int change) {
        LogicScriptsUtils.getIInfoManagerScript().changeGlamour(this, change);
    }

    /** 温柔变化 **/
    public void changeTender(int change) {
        LogicScriptsUtils.getIInfoManagerScript().changeTender(this, change);
    }

    /** 知识变化 **/
    public void changeKnowledge(int change) {
        LogicScriptsUtils.getIInfoManagerScript().changeKnowledge(this, change);
    }

    /** 运气变化 **/
    public void changefortune(int change) {
        LogicScriptsUtils.getIInfoManagerScript().changefortune(this, change);
    }

    public int getMaxByItemId(int itemId) {
        return LogicScriptsUtils.getIInfoManagerScript().getMaxByItemId(itemId);
    }

    /** 计算 **/
    public int calculate(int parm, int change, int max) {
        return LogicScriptsUtils.getIInfoManagerScript().calculate(this, parm, change, max);
    }

    public int getAbsorbed() {
        return absorbed;
    }

    public void setAbsorbed(int absorbed) {
        this.absorbed = absorbed;
    }

    public int getGlamour() {
        return glamour;
    }

    public void setGlamour(int glamour) {
        this.glamour = glamour;
    }

    public int getTender() {
        return tender;
    }

    public void setTender(int tender) {
        this.tender = tender;
    }

    public int getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(int knowledge) {
        this.knowledge = knowledge;
    }

    public int getFortune() {
        return fortune;
    }

    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    public int getRecoverStrength() {
        return recoverStrength;
    }

    public void setRecoverStrength(int recoverStrength) {
        this.recoverStrength = recoverStrength;
    }

    public long getTodayOnlineTime() {
        return todayOnlineTime;
    }

    public int getAntiStatus() {
        return antiStatus;
    }

    public void setAntiStatus(int antiStatus) {
        this.antiStatus = antiStatus;
        LOGGER.info("anti-addiction, setAntiStatus:" + antiStatus + " playerId:"
                + player.getPlayerId());
    }

    @Override
    public IView toView() {
        return this;
    }

    @Override
    public JsonElement toViewJson(String fullJsonData) {
        InfoManager baseFunMan = (InfoManager) GsonUtils.fromJson(fullJsonData, getClass());
        JsonElement _json = GsonUtils.toJsonTree(baseFunMan.toView());
        return _json;
    }
    
    public void createEvent()
    {
        LogicScriptsUtils.getIInfoManagerScript().createEvent(player);
    }
    
    public void loginInit() {}

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        LogicScriptsUtils.getIInfoManagerScript().acrossDay(player, type, isNotify); 
    }

    /** 当前是否是防沉迷状态 */
    public boolean isAnti() {
        return LogicScriptsUtils.getIInfoManagerScript().isAnti(this);
    }

    @Override
    public void createPlayerInitialize() {
        LogicScriptsUtils.getIInfoManagerScript().createPlayerInitialize(player);
    }
}
