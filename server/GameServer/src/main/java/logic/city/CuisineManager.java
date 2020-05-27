package logic.city;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;

import data.bean.CookQteCfgBean;
import data.bean.FoodbaseCfgBean;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IUnlockBuilding;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.city.build.bean.CuisineEvent;
import logic.city.script.ICuisineManagerScript;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 料理管理器
 * 
 * @author lihongji
 *
 */
public class CuisineManager extends PlayerBaseFunctionManager
        implements IUnlockBuilding, IRoleJsonConverter {

    /** 料理事件 **/
    CuisineEvent event = new CuisineEvent();
    /** 用于解锁料理提醒 **/
    Set<Integer> validCuisine = new HashSet<Integer>();

    private static final Logger LOGGER = Logger.getLogger(CuisineManager.class);
    
    private ICuisineManagerScript getManagerScript() {
        return LogicScriptsUtils.getICuisineManagerScript();
    }

    /** 检测当前是否能料理 **/
    public void checkCuisine(FoodbaseCfgBean foodbaseCfgBean) {
        getManagerScript().checkCuisine(foodbaseCfgBean, player);
    }

    /** 创建料理事件 **/
    public void createCuisineEvent(int id) {
        getManagerScript().createCuisineEvent(id, player,LOGGER);
    }


    /** 获取当前奖励 **/
    public S2CNewBuildingMsg.RespGetFoodBaseAward.Builder getAward(int cuisineId) {
        return getManagerScript().getAward(cuisineId, player,LOGGER);
    }

    /** 获取QTE操作 **/
    public List<CookQteCfgBean> getCookQteCfgBeans() {
        return getManagerScript().getCookQteCfgBeans();
    }

    /** 检测客户端传递的QTE操作积分 **/
    public S2CNewBuildingMsg.RespUploadQteIntegral.Builder checkQteIntegral(int cuisineId,
            int qteId, int integral) {
        return getManagerScript().checkQteIntegral(cuisineId, qteId, integral, player);
    }

    /** 检测 **/
    public void checkUnlock(int buildingId) {
        getManagerScript().checkUnlock(buildingId, player, validCuisine);

    }
    
    /**获取自然时间**/
    public int getNatureTime(FoodbaseCfgBean foodbaseCfgBean, Player player) {
        return getManagerScript().getNatureTime(foodbaseCfgBean, player);
        
    }
    


    public CuisineEvent getEvent() {
        return event;
    }

    public void setEvent(CuisineEvent event) {
        this.event = event;
    }


    /** 解锁料理 **/
    @Override
    public void unlock(Object object) {
        getManagerScript().unlock(object, player, validCuisine);
    }
}
