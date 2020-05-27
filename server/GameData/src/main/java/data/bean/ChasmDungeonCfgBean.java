/**
 * Auto generated, do not edit it
 *
 * ChasmDungeon
 */
package data.bean;

public class ChasmDungeonCfgBean 
{
  private int id; // ID
  private int[] openWeeks; // 开启星期
  private int[] openTimes; // 开启时间
  private int continuedTime; // 开启持续时间
  private int[] lvlLimit; // 玩家等级限制
  private int countLimit; // 最小人数
  private int capacity; // 队伍容量
  private int battleTime; // 关卡战斗时间
  private java.util.List reviveCost; // 复活消耗
  private int[] reward; // 奖励
  private java.util.Map fightCost; // 战斗消耗
  private java.util.Map heroLimit; // 英雄限制
  private int fightCount; // 最大战斗次数
  private java.util.Map cost; // 消耗资源
   
  /**
   * get ID
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set ID
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 开启星期
   * @return
   */
   public int[] getOpenWeeks()
   {
     return openWeeks;
   }

  /**
   * set 开启星期
   */
   public void setOpenWeeks(int[] openWeeks)
   {
      this.openWeeks = openWeeks;
   }
  /**
   * get 开启时间
   * @return
   */
   public int[] getOpenTimes()
   {
     return openTimes;
   }

  /**
   * set 开启时间
   */
   public void setOpenTimes(int[] openTimes)
   {
      this.openTimes = openTimes;
   }
  /**
   * get 开启持续时间
   * @return
   */
   public int getContinuedTime()
   {
     return continuedTime;
   }

  /**
   * set 开启持续时间
   */
   public void setContinuedTime(int continuedTime)
   {
      this.continuedTime = continuedTime;
   }
  /**
   * get 玩家等级限制
   * @return
   */
   public int[] getLvlLimit()
   {
     return lvlLimit;
   }

  /**
   * set 玩家等级限制
   */
   public void setLvlLimit(int[] lvlLimit)
   {
      this.lvlLimit = lvlLimit;
   }
  /**
   * get 最小人数
   * @return
   */
   public int getCountLimit()
   {
     return countLimit;
   }

  /**
   * set 最小人数
   */
   public void setCountLimit(int countLimit)
   {
      this.countLimit = countLimit;
   }
  /**
   * get 队伍容量
   * @return
   */
   public int getCapacity()
   {
     return capacity;
   }

  /**
   * set 队伍容量
   */
   public void setCapacity(int capacity)
   {
      this.capacity = capacity;
   }
  /**
   * get 关卡战斗时间
   * @return
   */
   public int getBattleTime()
   {
     return battleTime;
   }

  /**
   * set 关卡战斗时间
   */
   public void setBattleTime(int battleTime)
   {
      this.battleTime = battleTime;
   }
  /**
   * get 复活消耗
   * @return
   */
   public java.util.List getReviveCost()
   {
     return reviveCost;
   }

  /**
   * set 复活消耗
   */
   public void setReviveCost(java.util.List reviveCost)
   {
      this.reviveCost = reviveCost;
   }
  /**
   * get 奖励
   * @return
   */
   public int[] getReward()
   {
     return reward;
   }

  /**
   * set 奖励
   */
   public void setReward(int[] reward)
   {
      this.reward = reward;
   }
  /**
   * get 战斗消耗
   * @return
   */
   public java.util.Map getFightCost()
   {
     return fightCost;
   }

  /**
   * set 战斗消耗
   */
   public void setFightCost(java.util.Map fightCost)
   {
      this.fightCost = fightCost;
   }
  /**
   * get 英雄限制
   * @return
   */
   public java.util.Map getHeroLimit()
   {
     return heroLimit;
   }

  /**
   * set 英雄限制
   */
   public void setHeroLimit(java.util.Map heroLimit)
   {
      this.heroLimit = heroLimit;
   }
  /**
   * get 最大战斗次数
   * @return
   */
   public int getFightCount()
   {
     return fightCount;
   }

  /**
   * set 最大战斗次数
   */
   public void setFightCount(int fightCount)
   {
      this.fightCount = fightCount;
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
}
