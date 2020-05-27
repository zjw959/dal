package logic.dungeon.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonLimitHeroTO;
import logic.dungeon.script.ISingleDungeonSceneScript;

import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

/**
 * 单人副本场景
 */
public abstract class SingleDungeonScene extends DungeonScene {

    /** 战斗奖励 */
    protected Map<Integer, Integer> rewards;
    /** 助战英雄所属玩家id */
    protected int helpPid;
    /** 助战英雄 */
    protected HeroInfo helpHero;
    /** 所属玩家 */
    protected Player player;
    /** 所属玩家副本对象 */
    protected DungeonBean dungeonLevel;
    /** 关卡英雄限定数据,可能为空 */
    protected List<DungeonLimitHeroTO> limitedHeros;
    /** 战斗次数 */
    protected int battleCount;
    /** 扣除物品 */
    protected Map<Integer, Integer> cost;
    /**
     * 多倍收益倍数
     * <p>
     * 用于特殊结算
     */
    protected float multiple;
    /** 决斗模式 */
    protected boolean isDuel;

    public SingleDungeonScene(int sceneCid) {
        super();
        setSceneCfgId(sceneCid);
    }

    protected boolean win;
    protected List<Integer> goals = new ArrayList<Integer>(); // 达成目标的下标
    protected int batter;// 最大连击数
    protected int pickUpTypeCount;// 拾取道具种类个数
    protected int pickUpCount;// 拾取道具个数

    protected int oldStar;

    protected int nowStar;

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean isWin() {
        return win;
    }

    public List<Integer> getGoals() {
        return goals;
    }

    public void setGoals(List<Integer> goals) {
        this.goals = goals;
    }

    public int getBatter() {
        return batter;
    }

    public void setBatter(int batter) {
        this.batter = batter;
    }

    public int getPickUpTypeCount() {
        return pickUpTypeCount;
    }

    public void setPickUpTypeCount(int pickUpTypeCount) {
        this.pickUpTypeCount = pickUpTypeCount;
    }

    public int getPickUpCount() {
        return pickUpCount;
    }

    public void setPickUpCount(int pickUpCount) {
        this.pickUpCount = pickUpCount;
    }

    public Map<Integer, Integer> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Integer, Integer> rewards) {
        this.rewards = rewards;
    }

    public HeroInfo getHelpHero() {
        return helpHero;
    }

    public void setHelpHero(HeroInfo helpHero) {
        this.helpHero = helpHero;
    }

    public int getHelpPid() {
        return helpPid;
    }

    public void setHelpPid(int helpPid) {
        this.helpPid = helpPid;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
    
    public void setDungeonLevel(DungeonBean dungeonLevel) {
        this.dungeonLevel = dungeonLevel;
    }

    public DungeonBean getDungeonLevel() {
        return dungeonLevel;
    }

    public int getOldStar() {
        return oldStar;
    }

    public void setOldStar(int oldStar) {
        this.oldStar = oldStar;
    }

    public int getNowStar() {
        return nowStar;
    }

    public void setNowStar(int nowStar) {
        this.nowStar = nowStar;
    }

    public List<DungeonLimitHeroTO> getLimitedHeros() {
        return limitedHeros;
    }

    public void setLimitedHeros(List<DungeonLimitHeroTO> limitedHeros) {
        this.limitedHeros = limitedHeros;
    }

    public int getBattleCount() {
        return battleCount;
    }

    public void setBattleCount(int battleCount) {
        this.battleCount = battleCount;
    }

    public Map<Integer, Integer> getCost() {
        return cost;
    }

    public void setCost(Map<Integer, Integer> cost) {
        this.cost = cost;
    }

    public float getMultiple() {
        return multiple;
    }

    public void setMultiple(float multiple) {
        this.multiple = multiple;
    }

    public boolean isDuel() {
        return isDuel;
    }

    public void setDuel(boolean isDuel) {
        this.isDuel = isDuel;
    }

    /** 获取脚本处理对象 */
    protected abstract ISingleDungeonSceneScript getSceneScript();

    protected void putScene() {
        getSceneScript().putScene(this, player);
    }

    protected void destroyScene() {
        getSceneScript().destroyScene(getSceneId(), player);
    }

    @Override
    protected boolean sceneStart() {
        return getSceneScript().sceneStart(this);
    }

    @Override
    protected void sceneInit() {
        getSceneScript().sceneInit(this);
    }
}
