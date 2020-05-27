/**
 * Auto generated, do not edit it
 *
 * Build
 */
package data.bean;

public class BuildCfgBean 
{
  private int id; // ID
  private int buildId; // 建筑编号
  private int level; // 建筑等级
  private java.util.Map openType; // 解锁条件（1等级、2章节）
  private boolean isLevelUp; // 是否可升级
  private int upGradeTips; // 升级按钮
  private java.util.Map upType; // 升级条件（1等级、2章节）
  private java.util.Map consume; // 建筑升级消耗
  private int needTime; // 升级时间
  private boolean isWork; // 是否可打工
  private int workTips; // 打工按钮
  private int position; // 打工槽位
  private int[] nowDrop; // 当前掉落预览
  private int[] nextDrop; // 下级掉落预览
  private java.util.Map demand; // 打工要求（1.派遣N个看板娘2.必须包含某个看板娘3.看板娘好感度达到4.看板娘心情值达到
  private int workTime; // 打工时间
  private int cycle; // 收益周期
  private boolean isShop; // 是否开放商店
  private int shopTips; // 商店按钮
  private int shopType; // 商店ID
  private int cityId; // 城市id
   
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
   * get 建筑编号
   * @return
   */
   public int getBuildId()
   {
     return buildId;
   }

  /**
   * set 建筑编号
   */
   public void setBuildId(int buildId)
   {
      this.buildId = buildId;
   }
  /**
   * get 建筑等级
   * @return
   */
   public int getLevel()
   {
     return level;
   }

  /**
   * set 建筑等级
   */
   public void setLevel(int level)
   {
      this.level = level;
   }
  /**
   * get 解锁条件（1等级、2章节）
   * @return
   */
   public java.util.Map getOpenType()
   {
     return openType;
   }

  /**
   * set 解锁条件（1等级、2章节）
   */
   public void setOpenType(java.util.Map openType)
   {
      this.openType = openType;
   }
  /**
   * get 是否可升级
   * @return
   */
   public boolean getIsLevelUp()
   {
     return isLevelUp;
   }

  /**
   * set 是否可升级
   */
   public void setIsLevelUp(boolean isLevelUp)
   {
      this.isLevelUp = isLevelUp;
   }
  /**
   * get 升级按钮
   * @return
   */
   public int getUpGradeTips()
   {
     return upGradeTips;
   }

  /**
   * set 升级按钮
   */
   public void setUpGradeTips(int upGradeTips)
   {
      this.upGradeTips = upGradeTips;
   }
  /**
   * get 升级条件（1等级、2章节）
   * @return
   */
   public java.util.Map getUpType()
   {
     return upType;
   }

  /**
   * set 升级条件（1等级、2章节）
   */
   public void setUpType(java.util.Map upType)
   {
      this.upType = upType;
   }
  /**
   * get 建筑升级消耗
   * @return
   */
   public java.util.Map getConsume()
   {
     return consume;
   }

  /**
   * set 建筑升级消耗
   */
   public void setConsume(java.util.Map consume)
   {
      this.consume = consume;
   }
  /**
   * get 升级时间
   * @return
   */
   public int getNeedTime()
   {
     return needTime;
   }

  /**
   * set 升级时间
   */
   public void setNeedTime(int needTime)
   {
      this.needTime = needTime;
   }
  /**
   * get 是否可打工
   * @return
   */
   public boolean getIsWork()
   {
     return isWork;
   }

  /**
   * set 是否可打工
   */
   public void setIsWork(boolean isWork)
   {
      this.isWork = isWork;
   }
  /**
   * get 打工按钮
   * @return
   */
   public int getWorkTips()
   {
     return workTips;
   }

  /**
   * set 打工按钮
   */
   public void setWorkTips(int workTips)
   {
      this.workTips = workTips;
   }
  /**
   * get 打工槽位
   * @return
   */
   public int getPosition()
   {
     return position;
   }

  /**
   * set 打工槽位
   */
   public void setPosition(int position)
   {
      this.position = position;
   }
  /**
   * get 当前掉落预览
   * @return
   */
   public int[] getNowDrop()
   {
     return nowDrop;
   }

  /**
   * set 当前掉落预览
   */
   public void setNowDrop(int[] nowDrop)
   {
      this.nowDrop = nowDrop;
   }
  /**
   * get 下级掉落预览
   * @return
   */
   public int[] getNextDrop()
   {
     return nextDrop;
   }

  /**
   * set 下级掉落预览
   */
   public void setNextDrop(int[] nextDrop)
   {
      this.nextDrop = nextDrop;
   }
  /**
   * get 打工要求（1.派遣N个看板娘2.必须包含某个看板娘3.看板娘好感度达到4.看板娘心情值达到
   * @return
   */
   public java.util.Map getDemand()
   {
     return demand;
   }

  /**
   * set 打工要求（1.派遣N个看板娘2.必须包含某个看板娘3.看板娘好感度达到4.看板娘心情值达到
   */
   public void setDemand(java.util.Map demand)
   {
      this.demand = demand;
   }
  /**
   * get 打工时间
   * @return
   */
   public int getWorkTime()
   {
     return workTime;
   }

  /**
   * set 打工时间
   */
   public void setWorkTime(int workTime)
   {
      this.workTime = workTime;
   }
  /**
   * get 收益周期
   * @return
   */
   public int getCycle()
   {
     return cycle;
   }

  /**
   * set 收益周期
   */
   public void setCycle(int cycle)
   {
      this.cycle = cycle;
   }
  /**
   * get 是否开放商店
   * @return
   */
   public boolean getIsShop()
   {
     return isShop;
   }

  /**
   * set 是否开放商店
   */
   public void setIsShop(boolean isShop)
   {
      this.isShop = isShop;
   }
  /**
   * get 商店按钮
   * @return
   */
   public int getShopTips()
   {
     return shopTips;
   }

  /**
   * set 商店按钮
   */
   public void setShopTips(int shopTips)
   {
      this.shopTips = shopTips;
   }
  /**
   * get 商店ID
   * @return
   */
   public int getShopType()
   {
     return shopType;
   }

  /**
   * set 商店ID
   */
   public void setShopType(int shopType)
   {
      this.shopType = shopType;
   }
  /**
   * get 城市id
   * @return
   */
   public int getCityId()
   {
     return cityId;
   }

  /**
   * set 城市id
   */
   public void setCityId(int cityId)
   {
      this.cityId = cityId;
   }
}
