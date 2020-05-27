package logic.dating.script;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import data.bean.ItemRecoverCfgBean;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import script.IScript;

public interface IDatingManagerScript extends IScript {

    /**
     * 清理相应约会
     */
    public void deleteDating(CurrentDatingBean dating, Map<Long, CurrentDatingBean> currentDatings);

    /**
     * 清理相应约会
     */
    public CityDatingBean deleteCityDating(long datingId,
            Map<Long, CurrentDatingBean> currentDatings);

    /**
     * 获取城市约会
     */
    public CityDatingBean getCityDating(long cityDatingId,
            Map<Long, CurrentDatingBean> currentDatings);

    /**
     * 获取当前所有城市约会
     */
    public List<CityDatingBean> getCurrentCityDatings(int datingType,
            Map<Long, CurrentDatingBean> currentDatings);

    /**
     * 获取当前所有城市约会
     */
    public List<CityDatingBean> getAllCityDatings(Map<Long, CurrentDatingBean> currentDatings);

    /**
     * 放置约会对象
     */
    public void putDatingBean(CurrentDatingBean dating,
            Map<Long, CurrentDatingBean> currentDatings);


    /** 看板娘触发约会是否存在 */
    public boolean roleTriggerDatingExists(int roleId, int scriptId,
            Map<Integer, Set<Integer>> roleTriggerScripts);


    /**通过类型和精灵ID 剧本ID获取剧本**/
    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid,
            Map<Long, CurrentDatingBean> currentDatings,long datingId);

    /**通过类型和精灵ID获取剧本**/
    public CurrentDatingBean getByDatingTypeRoleId(int datingType, int roleCid,
            Map<Long, CurrentDatingBean> currentDatings);
    
    /** 精灵是否暂用 **/
    public CurrentDatingBean OccupyRoleId(int roleCid, Map<Long, CurrentDatingBean> currentDatings);

    /**通过类型和精灵ID获取剧本**/
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType,
            List<Integer> roleCidList, Map<Long, CurrentDatingBean> currentDatings);

    /**获取看板娘约会次数**/
    public int getRoleCityDatingCount(int roleCid, Map<Integer, Integer> roleCityDatingCount);

    /**看板娘城市约会次数 **/
    public void addRoleCityDatingCount(int roleCid, Map<Integer, Integer> roleCityDatingCount);

    public void tick(Player player);

    /** 检测触发城市约会的时间 **/
    public boolean checkCityDating(DatingManager manager);

    /**
     * 自主处理城市约会
     */
    public void handleCityDating(Date now, Player player);

    /**
     * 触发激活城市约会
     */
    public void triggerCityDating(Date now, Player player);


    /** 日常约会次数变化 **/
    public void changeDailyCont(int num, DatingManager manager);



    /** 获取日常约会次数配置表 */
    public ItemRecoverCfgBean getDailyCountCfg();

    /**
     * 整理核对约会状态
     */
    public void collateDatingState(Date now, Player player);


    public void acrossDay(EAcrossDayType type, boolean isNotify,
            Map<Integer, Integer> roleCityDatingCount, Player player);

    /** 时装和道具触发器 **/
    public void trigger(Object object, Player player);

    /** 检测喂食是否触发约会 **/
    public void checkDonateGift(Map<String, Object> in, Player player);

    /** 检测时装 **/
    public void checkDress(Map<String, Object> in, Player player);


    /** 重置当前日常约会次数 **/
    public void resetDailyDating(Player player);

    /**
     * 获取精力恢复的配置
     * 
     * @return
     */
    public ItemRecoverCfgBean getItemRecoverCfg(int id);

    public void createRoleInitialize(Player player);

    /** 接受邀请 **/
    public void acceptDating(boolean accept, Player player);

    /** 检测预定约会是否超时 **/
    public boolean checkReserveDating(Player player);

    /** 移除手机约会 **/
    public void removePhoneDating(Player player, CurrentDatingBean record);


}
