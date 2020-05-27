/**
 * Auto generated, do not edit it
 *
 * SummonCompose
 */
package data.bean;

public class SummonComposeCfgBean 
{
  private int id; // id
  private int poolType; // 合成物品卡池
  private int rarePoolType; // 稀有合成卡池
  private int zPointType; // 所属质点（1.王冠 ……）
  private int level; // 配方类型（1.低级 2.中级3.高级）
  private java.util.Map cost; // 需要材料
  private int time; // 合成时间（秒）
   
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
   * get 合成物品卡池
   * @return
   */
   public int getPoolType()
   {
     return poolType;
   }

  /**
   * set 合成物品卡池
   */
   public void setPoolType(int poolType)
   {
      this.poolType = poolType;
   }
  /**
   * get 稀有合成卡池
   * @return
   */
   public int getRarePoolType()
   {
     return rarePoolType;
   }

  /**
   * set 稀有合成卡池
   */
   public void setRarePoolType(int rarePoolType)
   {
      this.rarePoolType = rarePoolType;
   }
  /**
   * get 所属质点（1.王冠 ……）
   * @return
   */
   public int getZPointType()
   {
     return zPointType;
   }

  /**
   * set 所属质点（1.王冠 ……）
   */
   public void setZPointType(int zPointType)
   {
      this.zPointType = zPointType;
   }
  /**
   * get 配方类型（1.低级 2.中级3.高级）
   * @return
   */
   public int getLevel()
   {
     return level;
   }

  /**
   * set 配方类型（1.低级 2.中级3.高级）
   */
   public void setLevel(int level)
   {
      this.level = level;
   }
  /**
   * get 需要材料
   * @return
   */
   public java.util.Map getCost()
   {
     return cost;
   }

  /**
   * set 需要材料
   */
   public void setCost(java.util.Map cost)
   {
      this.cost = cost;
   }
  /**
   * get 合成时间（秒）
   * @return
   */
   public int getTime()
   {
     return time;
   }

  /**
   * set 合成时间（秒）
   */
   public void setTime(int time)
   {
      this.time = time;
   }
}
