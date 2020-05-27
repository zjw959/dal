package logic.dating.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关卡约会对象
 * 
 * @author Alan
 *
 */
public class DungeonDatingBean extends CurrentDatingBean {
	/**
	 * 约会过程道具
	 */
	Map<Integer, Integer> datingItems = new HashMap<Integer, Integer>();

	public DungeonDatingBean(long id, int datingType, int score,
			int favorReward, int currentCid,
			Map<Integer, List<Integer>> currentScript, int scriptId,
			List<Integer> roleIds) {
		super(id, datingType, score, favorReward, currentCid, currentScript,
				scriptId, roleIds);
	}

	public boolean isItemEnough(int id, int num) {
		int left = datingItems.getOrDefault(id, 0);
		return left > 0;
	}

	public boolean isItemEnough(Map<Integer, Integer> datingItems) {
		for (Map.Entry<Integer, Integer> item : datingItems.entrySet()) {
			if (!isItemEnough(item.getKey(), item.getValue()))
				return false;
		}
		return true;
	}

	/** 增加约会道具 */
	public void addDatingItem(int id, int num) {
		datingItems.put(id, datingItems.getOrDefault(id, 0) + num);
	}

	/** 增加约会道具 */
	public void addDatingItems(Map<Integer, Integer> datingItems) {
		for (Map.Entry<Integer, Integer> item : datingItems.entrySet()) {
			addDatingItem(item.getKey(), item.getValue());
		}
	}

	/** 移除约会道具 */
	public void removeDatingItem(int id, int num) {
		int previous = datingItems.getOrDefault(id, 0);
		if (previous > num)
			datingItems.put(id, previous - num);
		else
			datingItems.remove(id);
	}

	/** 移除约会道具 */
	public void removeDatingItems(Map<Integer, Integer> datingItems) {
		for (Map.Entry<Integer, Integer> item : datingItems.entrySet()) {
			removeDatingItem(item.getKey(), item.getValue());
		}
	}
	
	public void outputDatingItems(Map<Integer, Integer> out) {
		for (Map.Entry<Integer, Integer> entry : datingItems.entrySet()) {
			if(entry.getValue() > 0)
				out.put(entry.getKey(), out.getOrDefault(entry.getKey(), 0) + entry.getValue());
		}
	}
}
