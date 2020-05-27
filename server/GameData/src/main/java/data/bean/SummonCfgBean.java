/**
 * Auto generated, do not edit it
 *
 * Summon
 */
package data.bean;

public class SummonCfgBean 
{
  private int id; // id
  private int summonType; // 所属召唤类型
  private int order; // 排序
  private int sunmmonId; // 玩法名称
  private int cardCount; // 召唤个数
  private java.util.List cost; // 消耗
  private int costCommodity; // 消耗对应的商品id
  private int poolType; // 调用卡池
  private java.util.Map goods; // 获得金币
  private int rareItemTimes; // 抽到高级物品需要的最小次数
  private int[] minQuality; // 保底物品品质
  private int noobTime; // 新手期时间（建号天数）
   
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
   * get 所属召唤类型
   * @return
   */
   public int getSummonType()
   {
     return summonType;
   }

  /**
   * set 所属召唤类型
   */
   public void setSummonType(int summonType)
   {
      this.summonType = summonType;
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
   * get 玩法名称
   * @return
   */
   public int getSunmmonId()
   {
     return sunmmonId;
   }

  /**
   * set 玩法名称
   */
   public void setSunmmonId(int sunmmonId)
   {
      this.sunmmonId = sunmmonId;
   }
  /**
   * get 召唤个数
   * @return
   */
   public int getCardCount()
   {
     return cardCount;
   }

  /**
   * set 召唤个数
   */
   public void setCardCount(int cardCount)
   {
      this.cardCount = cardCount;
   }
  /**
   * get 消耗
   * @return
   */
   public java.util.List getCost()
   {
     return cost;
   }

  /**
   * set 消耗
   */
   public void setCost(java.util.List cost)
   {
      this.cost = cost;
   }
  /**
   * get 消耗对应的商品id
   * @return
   */
   public int getCostCommodity()
   {
     return costCommodity;
   }

  /**
   * set 消耗对应的商品id
   */
   public void setCostCommodity(int costCommodity)
   {
      this.costCommodity = costCommodity;
   }
  /**
   * get 调用卡池
   * @return
   */
   public int getPoolType()
   {
     return poolType;
   }

  /**
   * set 调用卡池
   */
   public void setPoolType(int poolType)
   {
      this.poolType = poolType;
   }
  /**
   * get 获得金币
   * @return
   */
   public java.util.Map getGoods()
   {
     return goods;
   }

  /**
   * set 获得金币
   */
   public void setGoods(java.util.Map goods)
   {
      this.goods = goods;
   }
  /**
   * get 抽到高级物品需要的最小次数
   * @return
   */
   public int getRareItemTimes()
   {
     return rareItemTimes;
   }

  /**
   * set 抽到高级物品需要的最小次数
   */
   public void setRareItemTimes(int rareItemTimes)
   {
      this.rareItemTimes = rareItemTimes;
   }
  /**
   * get 保底物品品质
   * @return
   */
   public int[] getMinQuality()
   {
     return minQuality;
   }

  /**
   * set 保底物品品质
   */
   public void setMinQuality(int[] minQuality)
   {
      this.minQuality = minQuality;
   }
  /**
   * get 新手期时间（建号天数）
   * @return
   */
   public int getNoobTime()
   {
     return noobTime;
   }

  /**
   * set 新手期时间（建号天数）
   */
   public void setNoobTime(int noobTime)
   {
      this.noobTime = noobTime;
   }
}
