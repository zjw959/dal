package logic.dungeon.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.dating.bean.DungeonDatingBean;
import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.support.LogicScriptsUtils;

/**
 * 约会副本
 * 
 * @author Alan
 *
 */
public class DatingDungeonScene extends SingleDungeonScene {
	/** 当前约会对象缓存,记录约会中的数据 */
	transient DungeonDatingBean currentDatingBean;
	/**
	 * 约会完成对象
	 */
	transient Map<Integer, DungeonDatingBean> datingBeans = new HashMap<Integer, DungeonDatingBean>();
	transient Map<Integer, Integer> totalDatingItems = new HashMap<Integer, Integer>();
	transient int totalScore;

	public DatingDungeonScene(int sceneCid) {
		super(sceneCid);
		super.setWin(true);
	}

	@Override
	protected ISingleDungeonSceneScript getSceneScript() {
		return LogicScriptsUtils.getIDungeonDatingScript();
	}

	@Override
	protected boolean sceneOver(boolean normal) {
		return getSceneScript().sceneOver(this, normal);
	}

	/**
	 * 结束本阶段的约会剧情
	 */
	public void completeCurrentDating() {
		datingBeans.put(currentDatingBean.getScriptId(), currentDatingBean);
		currentDatingBean = null;
	}

	public DungeonDatingBean getCurrentDatingBean() {
		return currentDatingBean;
	}

	public void setCurrentDatingBean(DungeonDatingBean currentDatingBean) {
		this.currentDatingBean = currentDatingBean;
	}

	public Map<Integer, DungeonDatingBean> getDatingBeans() {
		return datingBeans;
	}

	public void setDatingBeans(Map<Integer, DungeonDatingBean> datingBeans) {
		this.datingBeans = datingBeans;
	}

	public Map<Integer, Integer> getTotalDatingItems() {
		return totalDatingItems;
	}

	public void setTotalDatingItems(Map<Integer, Integer> totalDatingItems) {
		this.totalDatingItems = totalDatingItems;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	
	@Override
	public void setGoals(List<Integer> goals) {
		// 不允许外部直接重置
	}
	
	@Override
	public void setWin(boolean win) {
		// 约会关卡没有失败
	}
}
