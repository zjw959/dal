package logic.city.script;

import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import data.bean.HandworkbaseCfgBean;
import logic.character.bean.Player;
import script.IScript;

public interface IManualManagerScript extends IScript {

	/** 检测当前是否能进行手工制作 **/
	public void checkManual(HandworkbaseCfgBean handworkbaseCfgBean, Player player);

	/** 创建手工事件 **/
	public void createManualEvent(int id, Player player,Logger LOGGER);

	/** 获取当前奖励 **/
	public S2CNewBuildingMsg.RespGetHandWorkAward.Builder getAward(int manualId, Player player,Logger LOGGER);

	/** 检测客户端传递的手工操作积分 **/
	public S2CNewBuildingMsg.RespGetHandWorkInfo.Builder checkOperateIntegral(int manualId, int integral,
			Player player);

	/** 检测 **/
	public void checkUnlock(int buildingId, Player player, Set<Integer> validManual);

	/** 获取自然增长量 **/
	public int getNatureTime(HandworkbaseCfgBean handworkbaseCfgBean, Player player);

	/** 解锁手工 **/
	public void unlock(Object object, Player player, Set<Integer> validManual);
}
