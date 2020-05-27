package logic.city.script;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import logic.character.bean.Player;
import logic.city.build.bean.BuildEvent;
import script.IScript;

public interface IBuildingGeneralScript extends IScript {

	/**
	 * 通用随机方法(返回数据的当前位置)
	 * 
	 * @param list
	 *            随机数据集合
	 * @param parm
	 *            循环增加i
	 * @param index
	 *            如果没有默认取值的位置
	 * @param location
	 *            概率的位置
	 * @return
	 */
	public int random(ArrayList<Integer> list, int parm, int index, int location);

	/***
	 * 
	 * @param list
	 *            随机数据集合
	 * @param parm
	 *            循环增加i
	 * @param index
	 *            概率的位置
	 * @return
	 */
	public void resetProbability(ArrayList<Integer> list, int parm, int index);

	/** 生成建筑事件 **/
	public BuildEvent createBuildingEvent(int buildingId, int eventType, int exeId, int funId, Player player);

	/**
	 * 开放检测检查条件
	 * 
	 * @param condition
	 * @return
	 */
	public boolean checkBuildingCondition(Map<Integer, Object> condition, Player player);

	/** 检测5属性能力 **/
	public boolean checkAbility(Map<Integer, Integer> ability, Player player);

	/** 获取功能中的所对应的建筑 **/
	public Set<Integer> getBuildingByFunId(int funId, Player player);

}
