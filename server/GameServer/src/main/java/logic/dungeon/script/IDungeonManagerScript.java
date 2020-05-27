package logic.dungeon.script;

import java.util.List;

import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.ConfigHeroVO;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.bean.GroupRewardTO;
import logic.dungeon.bean.SceneOverTO;
import logic.dungeon.bean.SceneStartTO;
import logic.dungeon.scene.SingleDungeonScene;
import logic.item.bean.Item;
import script.IScript;
import data.bean.DungeonLevelCfgBean;
import data.bean.MainLineCfgBean;

/**
 * 副本脚本
 * 
 * @author Alan
 *
 */
public abstract class IDungeonManagerScript implements IScript {

	@Override
	public int getScriptId() {
		return EScriptIdDefine.DUNGEON_MANAGER_SCRIPT.Value();
	}

	/**
	 * 获取或新建副本对象
	 */
	public abstract DungeonBean getOrInitDungeonBean(DungeonManager dm, int cid);

	/**
	 * 获取或新建副本组对象
	 * <p><b>此方式会使关卡组参与多倍收益的刷新，务必在需要初始化的地方调用</b>
	 */
	public abstract DungeonGroupBean getOrInitDungeonLevelGroup(Player player,
			DungeonManager dm, int groupCid);

	/**
	 * 检查助战玩家是否在冷却中
	 */
	public abstract boolean isHelpFightCD(DungeonManager dm, int helpPlayerId);

	/**
	 * 检查清理助战玩家cd
	 */
	public abstract void checkHelpFightCD(DungeonManager dm);

	public abstract void createPlayerInitialize();

	public abstract void createRoleInitialize() throws Exception;

	/** 修改主线剧情进度 */
	public abstract DungeonGroupBean updateGroupStoryProgress(Player player,
			DungeonManager dm, MainLineCfgBean cfg);

	/** 推进主线剧情,向玩家推送进度数据 */
	public abstract void promoteStory(Player player, DungeonManager dm,
			int mainLineCid);

	public abstract void tick(DungeonManager dm);

	public abstract void clientLoginInitOver(DungeonManager dm);

	public abstract void acrossDay(Player player, DungeonManager dm,
			boolean isNotify);

	/** 对副本合法性的检测与更新 */
	public abstract void checkDungeonValid();

	/**
	 * 战斗次数是否达到上限
	 */
	public abstract boolean checkFightCountIsLimit(int limit,
			DungeonGroupBean dungeonGroup);

	/**
	 * 查看奖励是否领取
	 */
	public abstract boolean isGettedReward(int difficulty, int rewardId,
			DungeonGroupBean dungeonGroup);

	/**
	 * 奖励领取记录
	 */
	public abstract void recordReward(int difficulty, int rewardId,
			DungeonGroupBean dungeonGroup);

    /** 检测副本是否已经通过 **/
    public abstract boolean checkDungeonPass(DungeonManager dungeonManager, int id);
    
    /** 检测并开始副本 **/
    public abstract SingleDungeonScene sceneStart(Player player, SceneStartTO startTo) throws Exception;
    
    /** 创建副本场景 **/
    public abstract SingleDungeonScene createScene(DungeonLevelCfgBean dungeonLevelCfg);
    
    /** 结束副本场景 **/
    public abstract SingleDungeonScene sceneOver(Player player, List<Item> rewardItems, SceneOverTO overTo);
    
    /** 领取关卡组奖励 **/
    public abstract DungeonGroupBean handleGroupReward(Player player, GroupRewardTO rewardTo);
    
    /** 购买关卡组次数 **/
    public abstract DungeonGroupBean buyGroupCount(Player player, int levelGroupCid);
    
    /** 获取关卡配置英雄信息 **/
    public abstract List<ConfigHeroVO> createDungeonConfigHeroVOs(int leveId);
	
}
