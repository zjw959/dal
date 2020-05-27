package logic.city.script;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import data.bean.NewBuildCfgBean;
import logic.character.bean.Player;
import logic.city.NewBuildingManager;
import logic.city.build.bean.BuildEvent;
import script.IScript;

public interface INewBuildingManagerScript extends IScript {

	/** 建筑解锁 **/
	public Map<Integer, ArrayList<Integer>> checkUnlock(boolean notice, Player player, Set<Integer> validbuilding,
			Map<Integer, Set<Integer>> validFunIds);

	/** 建筑检测 **/
	public void checkUnlockBuilding(ArrayList<BuildEvent> events, Map<Integer, ArrayList<Integer>> buildingsFundIds,
			Set<Integer> validbuilding, Player player);

	/** 检测功能行开放 **/
	public void checkUnlockFunIds(ArrayList<BuildEvent> events, Map<Integer, ArrayList<Integer>> buildingsFundIds,
			Set<Integer> validbuilding, Player player, Map<Integer, Set<Integer>> validFunIds);

	/** 创建事件 **/
	public void createfireEvent(Player player, Set<Integer> validbuilding, Map<Integer, Set<Integer>> validFunIds);

	/** 移除事件 **/
	public void removeEvent(int eventType, Player player, Map<Integer, ArrayList<BuildEvent>> buildingEvent);

	/** 检测时间点 **/
	public void checkDayType(NewBuildingManager manager);

	/** 检测 **/
	public void check(Object object, Player player, Set<Integer> validbuilding, Map<Integer, Set<Integer>> validFunIds);

	public void createPlayerInitialize(Player player, Set<Integer> validbuilding,
			Map<Integer, Set<Integer>> validFunIds);

	public void tick(NewBuildingManager manager);

	/** 检测条件 **/
	public boolean checkCondition(NewBuildCfgBean bean, int fun, Player player);

}
