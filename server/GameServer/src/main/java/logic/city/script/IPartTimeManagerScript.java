package logic.city.script;

import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RespPartTimeJobAward;
import data.bean.CityJobCfgBean;
import logic.character.bean.Player;
import logic.city.PartTimeManager;
import logic.city.build.bean.JobRecord;
import logic.city.build.bean.PartTimeRecord;
import logic.constant.EAcrossDayType;
import script.IScript;

/***
 * 
 * 兼职
 * 
 * @author lihongji
 *
 */
public interface IPartTimeManagerScript extends IScript {

	/** 检测当前是否能够兼职 **/
	public JobRecord createJobEvent(int buildingId, int jobId, Player player,Logger LOGGER);

	/** 检测当前job是否存在列表中 **/
	public JobRecord checkJobIsExist(PartTimeRecord ptRecord, int jobId);

	/** 刷新建筑中的兼职列表 **/
	public void refreshPartTimeList(Player player);

	/** 刷新单个建筑的兼职 **/
	public void refreshPartTimeBuilding(int buildingId, Player player);

	/**
	 * 获取当前建筑的兼职列表
	 * 
	 * @param buildingId
	 *            建筑id
	 * @param ptLevel
	 *            兼职等级
	 * @return
	 */
	public ArrayList<Integer> getJobList(int buildingId, int ptLevel);

//	/** 检查刷新 **/
//	public boolean checkRefresh(Player player);

	/** 刷新 **/
	public void refresh(Player player);

	/** 获取当前兼职列表中的 **/
	public JobRecord getJobRecord(int buildingId, int jobId, Player player);

	/** 获取奖励 **/
	public RespPartTimeJobAward.Builder getAward(Player player,Logger LOGGER);

	/** 封装奖励 **/
	public Map<Integer, Integer> getGift(int jobId);

	/** 获取额外的奖励 **/
	public Map<Integer, Integer> getExtraGift(Map<Integer, Integer> gifts, Map<Integer, Integer> systemGift);

	/** 增加经验 **/
	public void addExp(int addExp, int buildingId, PartTimeManager manager);

	/** 检测 **/
	public void checkUnlock(Player player);

	/** 解锁建筑就 **/
	public void unlock(Object object, Player player);

	/** 检测当前建筑是否能够兼职 **/
	public boolean checkBuilingFunId(int buildingId, Player player);

	/** 放弃兼职 **/
	public void giveUpJob(Player player,Logger LOGGER);

	/** 跨天刷新 **/
	public void acrossDay(EAcrossDayType type, boolean isNotify, Player player);
	
	 public void trigger(int buildingId, CityJobCfgBean cityJobCfgBean,Player player);

}
