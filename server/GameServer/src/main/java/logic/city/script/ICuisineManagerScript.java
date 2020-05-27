package logic.city.script;

import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import data.bean.CookQteCfgBean;
import data.bean.FoodbaseCfgBean;
import logic.character.bean.Player;
import script.IScript;

public interface ICuisineManagerScript extends IScript {

	/** 检测当前是否能料理 **/
	public void checkCuisine(FoodbaseCfgBean foodbaseCfgBean, Player player);

	/** 创建料理事件 **/
	public void createCuisineEvent(int id, Player player,Logger LOGGER);

	/** 获取当前奖励 **/
	public S2CNewBuildingMsg.RespGetFoodBaseAward.Builder getAward(int cuisineId, Player player,Logger LOGGER);

	/** 获取QTE操作 **/
	public List<CookQteCfgBean> getCookQteCfgBeans();

	/** 检测客户端传递的QTE操作积分 **/
	public S2CNewBuildingMsg.RespUploadQteIntegral.Builder checkQteIntegral(int cuisineId, int qteId, int integral,
			Player player);

	/** 检测 **/
	public void checkUnlock(int buildingId, Player player, Set<Integer> validCuisine);

	/** 解锁料理 **/
	public void unlock(Object object, Player player, Set<Integer> validCuisine);

    /**自然时间**/
    public int getNatureTime(FoodbaseCfgBean foodbaseCfgBean, Player player);
}
