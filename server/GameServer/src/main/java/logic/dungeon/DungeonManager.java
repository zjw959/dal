package logic.dungeon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import logic.basecore.IAcrossDay;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.dungeon.bean.ConfigHeroVO;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.bean.GroupRewardTO;
import logic.dungeon.bean.SceneOverTO;
import logic.dungeon.bean.SceneStartTO;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.IDungeonManagerScript;
import logic.item.bean.Item;
import logic.support.LogicScriptsUtils;
import data.bean.MainLineCfgBean;

/**
 * 玩家个人副本数据管理器
 * 
 * @author Alan
 *
 */
public class DungeonManager extends PlayerBaseFunctionManager implements
        IRoleJsonConverter, ICreatePlayerInitialize,
		IAcrossDay {
	Map<Integer, DungeonBean> dungeons = new HashMap<Integer, DungeonBean>();
	Map<Integer, DungeonGroupBean> dungeonGroups = new HashMap<Integer, DungeonGroupBean>();
	transient Map<Integer, Long> helpFightCD = new HashMap<Integer, Long>();

	/** 当前场景缓存,不需要存储 */
	transient DungeonScene currentScene;

	/**
	 * 直接获取副本对象
	 */
	public DungeonBean getDungeonWithoutInit(int id) {
		return dungeons.get(id);
	}

	/**
	 * 直接获取副本组对象
	 */
	public DungeonGroupBean getDungeonGroupBeanWithoutInit(int id) {
		return dungeonGroups.get(id);
	}

	public void addDungeonBean(DungeonBean dungeon, boolean force2put) {
		if (force2put)
			dungeons.put(dungeon.getCid(), dungeon);
		else
			dungeons.putIfAbsent(dungeon.getCid(), dungeon);
	}

	public void addDungeonGroupBean(DungeonGroupBean dungeonGroup,
			boolean force2put) {
		if (force2put)
			dungeonGroups.put(dungeonGroup.getCid(), dungeonGroup);
		else
			dungeonGroups.putIfAbsent(dungeonGroup.getCid(), dungeonGroup);
	}

	private IDungeonManagerScript getManagerScript() {
		return LogicScriptsUtils.getIDungeonManagerScript();
	}

	/**
	 * 获取或新建副本对象
	 */
	public DungeonBean getOrInitDungeonBean(int cid) {
		return getManagerScript().getOrInitDungeonBean(this, cid);
	}

	/**
	 * 获取或新建副本组对象
	 * <p><b>此方式会使关卡组参与多倍收益的刷新，务必在需要初始化的地方调用</b>
	 */
	public DungeonGroupBean getOrInitDungeonLevelGroup(Player player, int groupCid) {
		return getManagerScript().getOrInitDungeonLevelGroup(player, this, groupCid);
	}

	/**
	 * 检查助战玩家是否在冷却中
	 */
	public boolean isHelpFightCD(int helpPlayerId) {
		return getManagerScript().isHelpFightCD(this, helpPlayerId);
	}

	/**
	 * 检查清理助战玩家CD
	 */
	public void checkHelpFightCD() {
		getManagerScript().checkHelpFightCD(this);
	}

	@Override
	public void createPlayerInitialize() {
		getManagerScript().createPlayerInitialize();
	}

	public DungeonScene getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(DungeonScene currentScene) {
		this.currentScene = currentScene;
	}

	public Map<Integer, DungeonBean> getDungeons() {
		return dungeons;
	}

	public Map<Integer, DungeonGroupBean> getDungeonGroups() {
		return dungeonGroups;
	}

	/** 好友助战cd */
	public Map<Integer, Long> getHelpFightCD() {
		return helpFightCD;
	}

	/** 好友助战cd */
	public void setHelpFightCD(int friendId, long cdEndTime) {
		helpFightCD.put(friendId, cdEndTime);
	}

	/** 获取好友助战cd */
	public long getHelpFightCD(int friendId) {
		Long value = helpFightCD.get(friendId);
		return value != null ? value.longValue() : 0;
	}

	/** 修改主线剧情进度 */
	public DungeonGroupBean updateGroupStoryProgress(Player player,MainLineCfgBean cfg) {
		return getManagerScript().updateGroupStoryProgress(player,this, cfg);
	}

	/** 推进主线剧情,向玩家推送进度数据 */
	public void promoteStory(int mainLineCid) {
		getManagerScript().promoteStory(player, this, mainLineCid);
	}

	@Override
	public void tick() {
		getManagerScript().tick(this);
	}

	@Override
	public void loginInit() {
		getManagerScript().clientLoginInitOver(this);
	}

	@Override
	public void acrossDay(EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
			getManagerScript().acrossDay(player, this, isNotify);
		}
	}

	/**
	 * 战斗次数是否达到上限
	 */
	public boolean checkFightCountIsLimit(int limit,
			DungeonGroupBean dungeonGroup) {
		return getManagerScript().checkFightCountIsLimit(limit, dungeonGroup);
	}

	/**
	 * 查看奖励是否领取
	 */
	public boolean isGettedReward(int difficulty, int rewardId,
			DungeonGroupBean dungeonGroup) {
		return getManagerScript().isGettedReward(difficulty, rewardId,
				dungeonGroup);
	}

	/**
	 * 奖励领取记录
	 */
	public void recordReward(int difficulty, int rewardId,
			DungeonGroupBean dungeonGroup) {
		getManagerScript().recordReward(difficulty, rewardId, dungeonGroup);
	}
	
	/**检测副本有没有通过**/
	public boolean checkDungeonPass(int id){
	    return getManagerScript().checkDungeonPass(this,id);
	}
	
	/** 检测并开始副本  **/
    public SingleDungeonScene sceneStart(SceneStartTO startTo) throws Exception {
        return getManagerScript().sceneStart(player, startTo);
    }
    
    /** 结束副本场景 **/
    public SingleDungeonScene sceneOver(List<Item> rewardItems, SceneOverTO overTo) {
        return getManagerScript().sceneOver(player, rewardItems, overTo);
    }
    
    /** 领取关卡组奖励 */
    public DungeonGroupBean handleGroupReward(GroupRewardTO rewardTo) {
        return getManagerScript().handleGroupReward(player, rewardTo);
    }
    
    /** 购买关卡组次数 **/
    public DungeonGroupBean buyGroupCount(int levelGroupCid) {
        return getManagerScript().buyGroupCount(player, levelGroupCid);
    }
    
    /** 获取关卡配置英雄信息 **/
    public List<ConfigHeroVO> createDungeonConfigHeroVOs(int leveId) {
        return getManagerScript().createDungeonConfigHeroVOs(leveId);
    }
}
