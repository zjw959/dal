/**
 * Auto generated, do not edit it
 *
 * EndlessCloisterLevel
 */
package data.bean;

public class EndlessCloisterLevelCfgBean 
{
  private int id; // id
  private int order; // 排序
  private int week; // 所属日期（1~7分别对应周1~周日）
  private int[] monsterGroupId; // 刷怪组id
  private int dungeonLevel; // 关卡等级(刷出怪物的等级，不用刷怪器中的怪物等级)
  private int difficulty; // 难度(1-普通关卡，2-BOSS关卡)
  private int[] map; // 地图ID
  private int levelStepID; // 关卡步骤表ID（完全由编辑器生成的关卡不填）
  private int time; // 关卡时长
  private int[] limitAttributes; // 关卡限定属性(1普攻伤害降低且技能伤害提高，2普攻伤害提高且技能伤害降低，后续扩展)
  private int[] limitTargetType; // 限定对象类型(1角色，2助战伙伴，3所有怪物(包含Boss)，4特指Boss，5普通怪物，6精英怪物)
  private int[] victoryType; // 通关条件类型(1歼灭，暂无其他通关条件类型)
  private java.util.List victoryParam; // 通关条件参数
  private int playerLv; // 进入等级
  private java.util.Map cost; // 消耗资源
  private int fightCount; // 可进入次数
  private int reward; // 奖励
  private java.util.Map floorReward; // 层数奖励
   
  /**
   * get id
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set id
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 排序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 排序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 所属日期（1~7分别对应周1~周日）
   * @return
   */
   public int getWeek()
   {
     return week;
   }

  /**
   * set 所属日期（1~7分别对应周1~周日）
   */
   public void setWeek(int week)
   {
      this.week = week;
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
   * get 关卡等级(刷出怪物的等级，不用刷怪器中的怪物等级)
   * @return
   */
   public int getDungeonLevel()
   {
     return dungeonLevel;
   }

  /**
   * set 关卡等级(刷出怪物的等级，不用刷怪器中的怪物等级)
   */
   public void setDungeonLevel(int dungeonLevel)
   {
      this.dungeonLevel = dungeonLevel;
   }
  /**
   * get 难度(1-普通关卡，2-BOSS关卡)
   * @return
   */
   public int getDifficulty()
   {
     return difficulty;
   }

  /**
   * set 难度(1-普通关卡，2-BOSS关卡)
   */
   public void setDifficulty(int difficulty)
   {
      this.difficulty = difficulty;
   }
  /**
   * get 地图ID
   * @return
   */
   public int[] getMap()
   {
     return map;
   }

  /**
   * set 地图ID
   */
   public void setMap(int[] map)
   {
      this.map = map;
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
   * get 通关条件类型(1歼灭，暂无其他通关条件类型)
   * @return
   */
   public int[] getVictoryType()
   {
     return victoryType;
   }

  /**
   * set 通关条件类型(1歼灭，暂无其他通关条件类型)
   */
   public void setVictoryType(int[] victoryType)
   {
      this.victoryType = victoryType;
   }
  /**
   * get 通关条件参数
   * @return
   */
   public java.util.List getVictoryParam()
   {
     return victoryParam;
   }

  /**
   * set 通关条件参数
   */
   public void setVictoryParam(java.util.List victoryParam)
   {
      this.victoryParam = victoryParam;
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
   * get 层数奖励
   * @return
   */
   public java.util.Map getFloorReward()
   {
     return floorReward;
   }

  /**
   * set 层数奖励
   */
   public void setFloorReward(java.util.Map floorReward)
   {
      this.floorReward = floorReward;
   }
}
