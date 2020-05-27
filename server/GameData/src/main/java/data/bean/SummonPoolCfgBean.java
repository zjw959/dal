/**
 * Auto generated, do not edit it
 *
 * SummonPool
 */
package data.bean;

public class SummonPoolCfgBean 
{
  private int id; // id
  private int poolType; // 卡池id
  private int repeatNum; // 10连最大重复次数
  private boolean composeReset; // 祈愿值重置标记（0不重置，1重置）
  private int quality; // 品质
  private int type; // 类型(1精灵，2质点，3道具）
  private java.util.Map itemMap; // 映射物品id
  private int[] weight; // 权重(格式：新手单抽,新手10连,正常单抽,正常10连）
   
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
   * get 卡池id
   * @return
   */
   public int getPoolType()
   {
     return poolType;
   }

  /**
   * set 卡池id
   */
   public void setPoolType(int poolType)
   {
      this.poolType = poolType;
   }
  /**
   * get 10连最大重复次数
   * @return
   */
   public int getRepeatNum()
   {
     return repeatNum;
   }

  /**
   * set 10连最大重复次数
   */
   public void setRepeatNum(int repeatNum)
   {
      this.repeatNum = repeatNum;
   }
  /**
   * get 祈愿值重置标记（0不重置，1重置）
   * @return
   */
   public boolean getComposeReset()
   {
     return composeReset;
   }

  /**
   * set 祈愿值重置标记（0不重置，1重置）
   */
   public void setComposeReset(boolean composeReset)
   {
      this.composeReset = composeReset;
   }
  /**
   * get 品质
   * @return
   */
   public int getQuality()
   {
     return quality;
   }

  /**
   * set 品质
   */
   public void setQuality(int quality)
   {
      this.quality = quality;
   }
  /**
   * get 类型(1精灵，2质点，3道具）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 类型(1精灵，2质点，3道具）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 映射物品id
   * @return
   */
   public java.util.Map getItemMap()
   {
     return itemMap;
   }

  /**
   * set 映射物品id
   */
   public void setItemMap(java.util.Map itemMap)
   {
      this.itemMap = itemMap;
   }
  /**
   * get 权重(格式：新手单抽,新手10连,正常单抽,正常10连）
   * @return
   */
   public int[] getWeight()
   {
     return weight;
   }

  /**
   * set 权重(格式：新手单抽,新手10连,正常单抽,正常10连）
   */
   public void setWeight(int[] weight)
   {
      this.weight = weight;
   }
}
