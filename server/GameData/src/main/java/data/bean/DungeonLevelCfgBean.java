/**
 * Auto generated, do not edit it
 *
 * DungeonLevel
 */
package data.bean;

public class DungeonLevelCfgBean 
{
  private int id; // 
  private int dungeonType; // 关卡类型(1-战斗，2-约会，3-城市约会，11-卡巴拉活动关卡)
  private int fightingMode; // 玩法模式(1-常规，2-飞行，3-撞击，约会关卡不填)
  private int[] preLevelId; // 前置关卡id
  private boolean isFree; // 是否为free关
  private int[] datingID; // 约会ID(读取约会表中的ID)
  private boolean lastOne; // 每卷最后1个主线剧情关卡(填1做标识)
  private int levelGroupId; // 所属关卡组ID
  private int levelGroupServerID; // 所属关卡组ID(此字段服务器用)
  private int heroLimitType; // 精灵限定类型(0-无限定/禁用；1-不能编入自己的精灵；2-可以编入自己的精灵；3-禁用精灵；)
  private int[] heroLimitID; // 限定精灵ID(读取heroLimitforDungeon表ID)
  private int[] heroForbiddenID; // 禁用精灵ID(读取hero表中ID)
  private int[] monsterGroupId; // 刷怪组id
  private String levelScript; // 关卡脚本
  private int levelStepID; // 关卡步骤表ID（完全由编辑器生成的关卡不填）
  private String bgm; // 背景音乐
  private int difficulty; // 难度
  private int time; // 关卡时长
  private int[] limitAttributes; // 关卡限定属性(1普攻伤害降低且技能伤害提高，2普攻伤害提高且技能伤害降低，后续扩展)
  private int[] limitTargetType; // 限定对象类型(1角色，2助战伙伴，3所有怪物(包含Boss)，4特指Boss，5普通怪物，6精英怪物)
  private int[] victoryType; // 通关条件类型(1歼灭，2生存，3击杀具体怪物，4击杀怪物类型，5击杀怪物数量，6防守)
  private java.util.List victoryParam; // 通关条件参数(歼灭不填，生存x秒内存活，怪物ID及数量x，怪物敌人类型及数量x，怪物总量x)
  private int[] starType; // 挑战类型(1通关，2连击数，3通关时间小于，4损失血量低于x%，5死亡人数小于x，6受击次数小于x，7倒地次数小于x，8使用x次觉醒技能，9使用角色出场技击败x个敌人，10选择一个助战角色，11队伍中包含某角色，12每x秒至少击杀1名敌人，13队伍中所有精灵使用技能总次数不超过x，14队伍中不能有精灵阵亡，31-击杀怪物数量达到x，32-积分达到x)
  private int[] starParam; // 挑战参数（类型为1通关时，参数配置为1）
  private int playerLv; // 进入等级
  private java.util.Map cost; // 消耗资源
  private int fightCount; // 可进入次数
  private boolean isDuelMod; // 是否可以开启决斗模式
  private java.util.Map duelModCost; // 开启决斗模式消耗资源
  private int rewardMultiple; // 决斗模式奖励倍数
  private boolean isQuickBattle; // 是否可以一次性挑战多次
  private java.util.Map quickBattleParameter; // 快速挑战参数{挑战次数:解锁星星条件}
  private int reward; // 奖励
  private int firstReward; // 首通奖励
  private int[] dailyLevelReward; // 日常副本奖励(根据每一次通关获得的星级发放对应奖励，1星奖励ID-2星奖励ID-3星奖励ID)
   
  /**
   * get 
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set 
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 关卡类型(1-战斗，2-约会，3-城市约会，11-卡巴拉活动关卡)
   * @return
   */
   public int getDungeonType()
   {
     return dungeonType;
   }

  /**
   * set 关卡类型(1-战斗，2-约会，3-城市约会，11-卡巴拉活动关卡)
   */
   public void setDungeonType(int dungeonType)
   {
      this.dungeonType = dungeonType;
   }
  /**
   * get 玩法模式(1-常规，2-飞行，3-撞击，约会关卡不填)
   * @return
   */
   public int getFightingMode()
   {
     return fightingMode;
   }

  /**
   * set 玩法模式(1-常规，2-飞行，3-撞击，约会关卡不填)
   */
   public void setFightingMode(int fightingMode)
   {
      this.fightingMode = fightingMode;
   }
  /**
   * get 前置关卡id
   * @return
   */
   public int[] getPreLevelId()
   {
     return preLevelId;
   }

  /**
   * set 前置关卡id
   */
   public void setPreLevelId(int[] preLevelId)
   {
      this.preLevelId = preLevelId;
   }
  /**
   * get 是否为free关
   * @return
   */
   public boolean getIsFree()
   {
     return isFree;
   }

  /**
   * set 是否为free关
   */
   public void setIsFree(boolean isFree)
   {
      this.isFree = isFree;
   }
  /**
   * get 约会ID(读取约会表中的ID)
   * @return
   */
   public int[] getDatingID()
   {
     return datingID;
   }

  /**
   * set 约会ID(读取约会表中的ID)
   */
   public void setDatingID(int[] datingID)
   {
      this.datingID = datingID;
   }
  /**
   * get 每卷最后1个主线剧情关卡(填1做标识)
   * @return
   */
   public boolean getLastOne()
   {
     return lastOne;
   }

  /**
   * set 每卷最后1个主线剧情关卡(填1做标识)
   */
   public void setLastOne(boolean lastOne)
   {
      this.lastOne = lastOne;
   }
  /**
   * get 所属关卡组ID
   * @return
   */
   public int getLevelGroupId()
   {
     return levelGroupId;
   }

  /**
   * set 所属关卡组ID
   */
   public void setLevelGroupId(int levelGroupId)
   {
      this.levelGroupId = levelGroupId;
   }
  /**
   * get 所属关卡组ID(此字段服务器用)
   * @return
   */
   public int getLevelGroupServerID()
   {
     return levelGroupServerID;
   }

  /**
   * set 所属关卡组ID(此字段服务器用)
   */
   public void setLevelGroupServerID(int levelGroupServerID)
   {
      this.levelGroupServerID = levelGroupServerID;
   }
  /**
   * get 精灵限定类型(0-无限定/禁用；1-不能编入自己的精灵；2-可以编入自己的精灵；3-禁用精灵；)
   * @return
   */
   public int getHeroLimitType()
   {
     return heroLimitType;
   }

  /**
   * set 精灵限定类型(0-无限定/禁用；1-不能编入自己的精灵；2-可以编入自己的精灵；3-禁用精灵；)
   */
   public void setHeroLimitType(int heroLimitType)
   {
      this.heroLimitType = heroLimitType;
   }
  /**
   * get 限定精灵ID(读取heroLimitforDungeon表ID)
   * @return
   */
   public int[] getHeroLimitID()
   {
     return heroLimitID;
   }

  /**
   * set 限定精灵ID(读取heroLimitforDungeon表ID)
   */
   public void setHeroLimitID(int[] heroLimitID)
   {
      this.heroLimitID = heroLimitID;
   }
  /**
   * get 禁用精灵ID(读取hero表中ID)
   * @return
   */
   public int[] getHeroForbiddenID()
   {
     return heroForbiddenID;
   }

  /**
   * set 禁用精灵ID(读取hero表中ID)
   */
   public void setHeroForbiddenID(int[] heroForbiddenID)
   {
      this.heroForbiddenID = heroForbiddenID;
   }
  /**
   * get 刷怪组id
   * @return
   */
   public int[] getMonsterGroupId()
   {
     return monsterGroupId;
   }

  /**
   * set 刷怪组id
   */
   public void setMonsterGroupId(int[] monsterGroupId)
   {
      this.monsterGroupId = monsterGroupId;
   }
  /**
   * get 关卡脚本
   * @return
   */
   public String getLevelScript()
   {
     return levelScript;
   }

  /**
   * set 关卡脚本
   */
   public void setLevelScript(String levelScript)
   {
      this.levelScript = levelScript;
   }
  /**
   * get 关卡步骤表ID（完全由编辑器生成的关卡不填）
   * @return
   */
   public int getLevelStepID()
   {
     return levelStepID;
   }

  /**
   * set 关卡步骤表ID（完全由编辑器生成的关卡不填）
   */
   public void setLevelStepID(int levelStepID)
   {
      this.levelStepID = levelStepID;
   }
  /**
   * get 背景音乐
   * @return
   */
   public String getBgm()
   {
     return bgm;
   }

  /**
   * set 背景音乐
   */
   public void setBgm(String bgm)
   {
      this.bgm = bgm;
   }
  /**
   * get 难度
   * @return
   */
   public int getDifficulty()
   {
     return difficulty;
   }

  /**
   * set 难度
   */
   public void setDifficulty(int difficulty)
   {
      this.difficulty = difficulty;
   }
  /**
   * get 关卡时长
   * @return
   */
   public int getTime()
   {
     return time;
   }

  /**
   * set 关卡时长
   */
   public void setTime(int time)
   {
      this.time = time;
   }
  /**
   * get 关卡限定属性(1普攻伤害降低且技能伤害提高，2普攻伤害提高且技能伤害降低，后续扩展)
   * @return
   */
   public int[] getLimitAttributes()
   {
     return limitAttributes;
   }

  /**
   * set 关卡限定属性(1普攻伤害降低且技能伤害提高，2普攻伤害提高且技能伤害降低，后续扩展)
   */
   public void setLimitAttributes(int[] limitAttributes)
   {
      this.limitAttributes = limitAttributes;
   }
  /**
   * get 限定对象类型(1角色，2助战伙伴，3所有怪物(包含Boss)，4特指Boss，5普通怪物，6精英怪物)
   * @return
   */
   public int[] getLimitTargetType()
   {
     return limitTargetType;
   }

  /**
   * set 限定对象类型(1角色，2助战伙伴，3所有怪物(包含Boss)，4特指Boss，5普通怪物，6精英怪物)
   */
   public void setLimitTargetType(int[] limitTargetType)
   {
      this.limitTargetType = limitTargetType;
   }
  /**
   * get 通关条件类型(1歼灭，2生存，3击杀具体怪物，4击杀怪物类型，5击杀怪物数量，6防守)
   * @return
   */
   public int[] getVictoryType()
   {
     return victoryType;
   }

  /**
   * set 通关条件类型(1歼灭，2生存，3击杀具体怪物，4击杀怪物类型，5击杀怪物数量，6防守)
   */
   public void setVictoryType(int[] victoryType)
   {
      this.victoryType = victoryType;
   }
  /**
   * get 通关条件参数(歼灭不填，生存x秒内存活，怪物ID及数量x，怪物敌人类型及数量x，怪物总量x)
   * @return
   */
   public java.util.List getVictoryParam()
   {
     return victoryParam;
   }

  /**
   * set 通关条件参数(歼灭不填，生存x秒内存活，怪物ID及数量x，怪物敌人类型及数量x，怪物总量x)
   */
   public void setVictoryParam(java.util.List victoryParam)
   {
      this.victoryParam = victoryParam;
   }
  /**
   * get 挑战类型(1通关，2连击数，3通关时间小于，4损失血量低于x%，5死亡人数小于x，6受击次数小于x，7倒地次数小于x，8使用x次觉醒技能，9使用角色出场技击败x个敌人，10选择一个助战角色，11队伍中包含某角色，12每x秒至少击杀1名敌人，13队伍中所有精灵使用技能总次数不超过x，14队伍中不能有精灵阵亡，31-击杀怪物数量达到x，32-积分达到x)
   * @return
   */
   public int[] getStarType()
   {
     return starType;
   }

  /**
   * set 挑战类型(1通关，2连击数，3通关时间小于，4损失血量低于x%，5死亡人数小于x，6受击次数小于x，7倒地次数小于x，8使用x次觉醒技能，9使用角色出场技击败x个敌人，10选择一个助战角色，11队伍中包含某角色，12每x秒至少击杀1名敌人，13队伍中所有精灵使用技能总次数不超过x，14队伍中不能有精灵阵亡，31-击杀怪物数量达到x，32-积分达到x)
   */
   public void setStarType(int[] starType)
   {
      this.starType = starType;
   }
  /**
   * get 挑战参数（类型为1通关时，参数配置为1）
   * @return
   */
   public int[] getStarParam()
   {
     return starParam;
   }

  /**
   * set 挑战参数（类型为1通关时，参数配置为1）
   */
   public void setStarParam(int[] starParam)
   {
      this.starParam = starParam;
   }
  /**
   * get 进入等级
   * @return
   */
   public int getPlayerLv()
   {
     return playerLv;
   }

  /**
   * set 进入等级
   */
   public void setPlayerLv(int playerLv)
   {
      this.playerLv = playerLv;
   }
  /**
   * get 消耗资源
   * @return
   */
   public java.util.Map getCost()
   {
     return cost;
   }

  /**
   * set 消耗资源
   */
   public void setCost(java.util.Map cost)
   {
      this.cost = cost;
   }
  /**
   * get 可进入次数
   * @return
   */
   public int getFightCount()
   {
     return fightCount;
   }

  /**
   * set 可进入次数
   */
   public void setFightCount(int fightCount)
   {
      this.fightCount = fightCount;
   }
  /**
   * get 是否可以开启决斗模式
   * @return
   */
   public boolean getIsDuelMod()
   {
     return isDuelMod;
   }

  /**
   * set 是否可以开启决斗模式
   */
   public void setIsDuelMod(boolean isDuelMod)
   {
      this.isDuelMod = isDuelMod;
   }
  /**
   * get 开启决斗模式消耗资源
   * @return
   */
   public java.util.Map getDuelModCost()
   {
     return duelModCost;
   }

  /**
   * set 开启决斗模式消耗资源
   */
   public void setDuelModCost(java.util.Map duelModCost)
   {
      this.duelModCost = duelModCost;
   }
  /**
   * get 决斗模式奖励倍数
   * @return
   */
   public int getRewardMultiple()
   {
     return rewardMultiple;
   }

  /**
   * set 决斗模式奖励倍数
   */
   public void setRewardMultiple(int rewardMultiple)
   {
      this.rewardMultiple = rewardMultiple;
   }
  /**
   * get 是否可以一次性挑战多次
   * @return
   */
   public boolean getIsQuickBattle()
   {
     return isQuickBattle;
   }

  /**
   * set 是否可以一次性挑战多次
   */
   public void setIsQuickBattle(boolean isQuickBattle)
   {
      this.isQuickBattle = isQuickBattle;
   }
  /**
   * get 快速挑战参数{挑战次数:解锁星星条件}
   * @return
   */
   public java.util.Map getQuickBattleParameter()
   {
     return quickBattleParameter;
   }

  /**
   * set 快速挑战参数{挑战次数:解锁星星条件}
   */
   public void setQuickBattleParameter(java.util.Map quickBattleParameter)
   {
      this.quickBattleParameter = quickBattleParameter;
   }
  /**
   * get 奖励
   * @return
   */
   public int getReward()
   {
     return reward;
   }

  /**
   * set 奖励
   */
   public void setReward(int reward)
   {
      this.reward = reward;
   }
  /**
   * get 首通奖励
   * @return
   */
   public int getFirstReward()
   {
     return firstReward;
   }

  /**
   * set 首通奖励
   */
   public void setFirstReward(int firstReward)
   {
      this.firstReward = firstReward;
   }
  /**
   * get 日常副本奖励(根据每一次通关获得的星级发放对应奖励，1星奖励ID-2星奖励ID-3星奖励ID)
   * @return
   */
   public int[] getDailyLevelReward()
   {
     return dailyLevelReward;
   }

  /**
   * set 日常副本奖励(根据每一次通关获得的星级发放对应奖励，1星奖励ID-2星奖励ID-3星奖励ID)
   */
   public void setDailyLevelReward(int[] dailyLevelReward)
   {
      this.dailyLevelReward = dailyLevelReward;
   }
}
