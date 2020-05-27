/**
 * Auto generated, do not edit it
 *
 * DungeonLevelGroup
 */
package data.bean;

public class DungeonLevelGroupCfgBean 
{
  private int id; // id
  private int dungeonType; // 关卡集类型(1.主线 2.常规 3.活动)
  private int unlockLevel; // 开放等级
  private int dungeonChapterId; // 所属章节
  private int[] layout; // 关卡布局
  private int countLimit; // 限制次数
  private int openTimeType; // 开启时间类型
  private java.util.List timeFrame; // 活动时间（周日~周六：1~7）
  private int cycleType; // 活动周期类型(0-不刷新，1-每日刷新)
  private int cycleParam; // 活动周期参数
  private int buyCountType; // 购买次数方式
  private int buyCountLimit; // 最大购买次数
  private java.util.Map price; // 单次购买消耗
  private java.util.Map reward; // （简单）挑战星级
   
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
   * get 关卡集类型(1.主线 2.常规 3.活动)
   * @return
   */
   public int getDungeonType()
   {
     return dungeonType;
   }

  /**
   * set 关卡集类型(1.主线 2.常规 3.活动)
   */
   public void setDungeonType(int dungeonType)
   {
      this.dungeonType = dungeonType;
   }
  /**
   * get 开放等级
   * @return
   */
   public int getUnlockLevel()
   {
     return unlockLevel;
   }

  /**
   * set 开放等级
   */
   public void setUnlockLevel(int unlockLevel)
   {
      this.unlockLevel = unlockLevel;
   }
  /**
   * get 所属章节
   * @return
   */
   public int getDungeonChapterId()
   {
     return dungeonChapterId;
   }

  /**
   * set 所属章节
   */
   public void setDungeonChapterId(int dungeonChapterId)
   {
      this.dungeonChapterId = dungeonChapterId;
   }
  /**
   * get 关卡布局
   * @return
   */
   public int[] getLayout()
   {
     return layout;
   }

  /**
   * set 关卡布局
   */
   public void setLayout(int[] layout)
   {
      this.layout = layout;
   }
  /**
   * get 限制次数
   * @return
   */
   public int getCountLimit()
   {
     return countLimit;
   }

  /**
   * set 限制次数
   */
   public void setCountLimit(int countLimit)
   {
      this.countLimit = countLimit;
   }
  /**
   * get 开启时间类型
   * @return
   */
   public int getOpenTimeType()
   {
     return openTimeType;
   }

  /**
   * set 开启时间类型
   */
   public void setOpenTimeType(int openTimeType)
   {
      this.openTimeType = openTimeType;
   }
  /**
   * get 活动时间（周日~周六：1~7）
   * @return
   */
   public java.util.List getTimeFrame()
   {
     return timeFrame;
   }

  /**
   * set 活动时间（周日~周六：1~7）
   */
   public void setTimeFrame(java.util.List timeFrame)
   {
      this.timeFrame = timeFrame;
   }
  /**
   * get 活动周期类型(0-不刷新，1-每日刷新)
   * @return
   */
   public int getCycleType()
   {
     return cycleType;
   }

  /**
   * set 活动周期类型(0-不刷新，1-每日刷新)
   */
   public void setCycleType(int cycleType)
   {
      this.cycleType = cycleType;
   }
  /**
   * get 活动周期参数
   * @return
   */
   public int getCycleParam()
   {
     return cycleParam;
   }

  /**
   * set 活动周期参数
   */
   public void setCycleParam(int cycleParam)
   {
      this.cycleParam = cycleParam;
   }
  /**
   * get 购买次数方式
   * @return
   */
   public int getBuyCountType()
   {
     return buyCountType;
   }

  /**
   * set 购买次数方式
   */
   public void setBuyCountType(int buyCountType)
   {
      this.buyCountType = buyCountType;
   }
  /**
   * get 最大购买次数
   * @return
   */
   public int getBuyCountLimit()
   {
     return buyCountLimit;
   }

  /**
   * set 最大购买次数
   */
   public void setBuyCountLimit(int buyCountLimit)
   {
      this.buyCountLimit = buyCountLimit;
   }
  /**
   * get 单次购买消耗
   * @return
   */
   public java.util.Map getPrice()
   {
     return price;
   }

  /**
   * set 单次购买消耗
   */
   public void setPrice(java.util.Map price)
   {
      this.price = price;
   }
  /**
   * get （简单）挑战星级
   * @return
   */
   public java.util.Map getReward()
   {
     return reward;
   }

  /**
   * set （简单）挑战星级
   */
   public void setReward(java.util.Map reward)
   {
      this.reward = reward;
   }
}
