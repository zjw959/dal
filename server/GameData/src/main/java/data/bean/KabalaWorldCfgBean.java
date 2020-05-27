/**
 * Auto generated, do not edit it
 *
 * KabalaWorld
 */
package data.bean;

public class KabalaWorldCfgBean 
{
  private int id; // ID
  private int worldName; // 质点世界名
  private int worldBackground; // 质点世界背景介绍
  private int missionName; // 质点世界任务名
  private int missionDescription; // 质点世界任务内容描述
  private String dateBegin; // 质点世界开放时间
  private String dateEnd; // 质点世界关闭时间
  private int beSoon; // 质点时间开启倒计时时间(即侵蚀倒计时，单位-分钟)
  private String mapDocument; // 地图文件调用路径
  private String sephirothDocument; // 质点图标调用路径
  private int missionItemAward; // 质点世界通关道具奖励
  private int missionBuffAward; // 质点世界通关增益奖励
  private int pollutionBase; // 质点世界精灵感染度基础值(1场战斗完后精灵感染度增加，无论战斗结果)
  private int pollutionDeepen; // 精灵感染度加重增幅(万分比)
  private int[] pollutionHero; // 感染度加重的精灵(填hero表中精灵ID)
  private int tileAppearing; // 格子刷出基础数量(3x3区域，定位1圈)
  private int unlockCost; // 移动解锁格子消耗能源点数(只有解锁时才扣除)
  private int scanCost; // 探索区域扫描消耗能源点数(成功或失败都要扣除)
  private java.util.List tileAppearingforEvent; // 事件影响格子出现数量(任务或事件ID，出现圈数)
  private java.util.List missionParam; // 质点世界任务目标类型(填kabalaMission表中的任务ID,配置顺序即为任务进行顺序)
  private boolean isRandom; // 任务流程是否随机(0-不随机，1-随机)
  private int finalMission; // 质点世界终极任务(填mission表中Boss任务ID)
  private java.util.List missionGuideEvent; // 任务的引导事件(填任务ID和已出现格子数量，1个任务包含多个子任务时填第一个子任务ID)
  private java.util.List fightingEvent; // 事件-可见战斗参数(填任务事件表中可见战斗事件ID，按数量随机，生成参数=数量-优先级-间距)
  private int[] strongpointEvent; // 据点事件
  private int[] exploreEvent; // 探索点事件
  private int[] ambushEvent; // 事件-伏击战斗参数(填多个dungeonLevel表中伏击战斗关卡ID，触发时随机一个)
  private java.util.List ambushChance; // 伏击触发概率(初始概率%+每移动1格的概率%，万分比)
  private int ambushCost; // 伏击战斗能源消耗
  private java.util.List randomEvent; // 宝箱事件参数(填事件表中宝箱事件ID，按数量随机，生成参数=数量-优先级-间距)
  private java.util.List shopEvent; // 商店事件参数(填商店事件ID，生成参数=数量-优先级-间距)
  private int[] itemID; // 商店出售道具ID(kabalashop表中商品ID)
  private int[] teleportEvent; // 传送事件参数(填事件表中的传送点ID)
   
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
   * get 质点世界名
   * @return
   */
   public int getWorldName()
   {
     return worldName;
   }

  /**
   * set 质点世界名
   */
   public void setWorldName(int worldName)
   {
      this.worldName = worldName;
   }
  /**
   * get 质点世界背景介绍
   * @return
   */
   public int getWorldBackground()
   {
     return worldBackground;
   }

  /**
   * set 质点世界背景介绍
   */
   public void setWorldBackground(int worldBackground)
   {
      this.worldBackground = worldBackground;
   }
  /**
   * get 质点世界任务名
   * @return
   */
   public int getMissionName()
   {
     return missionName;
   }

  /**
   * set 质点世界任务名
   */
   public void setMissionName(int missionName)
   {
      this.missionName = missionName;
   }
  /**
   * get 质点世界任务内容描述
   * @return
   */
   public int getMissionDescription()
   {
     return missionDescription;
   }

  /**
   * set 质点世界任务内容描述
   */
   public void setMissionDescription(int missionDescription)
   {
      this.missionDescription = missionDescription;
   }
  /**
   * get 质点世界开放时间
   * @return
   */
   public String getDateBegin()
   {
     return dateBegin;
   }

  /**
   * set 质点世界开放时间
   */
   public void setDateBegin(String dateBegin)
   {
      this.dateBegin = dateBegin;
   }
  /**
   * get 质点世界关闭时间
   * @return
   */
   public String getDateEnd()
   {
     return dateEnd;
   }

  /**
   * set 质点世界关闭时间
   */
   public void setDateEnd(String dateEnd)
   {
      this.dateEnd = dateEnd;
   }
  /**
   * get 质点时间开启倒计时时间(即侵蚀倒计时，单位-分钟)
   * @return
   */
   public int getBeSoon()
   {
     return beSoon;
   }

  /**
   * set 质点时间开启倒计时时间(即侵蚀倒计时，单位-分钟)
   */
   public void setBeSoon(int beSoon)
   {
      this.beSoon = beSoon;
   }
  /**
   * get 地图文件调用路径
   * @return
   */
   public String getMapDocument()
   {
     return mapDocument;
   }

  /**
   * set 地图文件调用路径
   */
   public void setMapDocument(String mapDocument)
   {
      this.mapDocument = mapDocument;
   }
  /**
   * get 质点图标调用路径
   * @return
   */
   public String getSephirothDocument()
   {
     return sephirothDocument;
   }

  /**
   * set 质点图标调用路径
   */
   public void setSephirothDocument(String sephirothDocument)
   {
      this.sephirothDocument = sephirothDocument;
   }
  /**
   * get 质点世界通关道具奖励
   * @return
   */
   public int getMissionItemAward()
   {
     return missionItemAward;
   }

  /**
   * set 质点世界通关道具奖励
   */
   public void setMissionItemAward(int missionItemAward)
   {
      this.missionItemAward = missionItemAward;
   }
  /**
   * get 质点世界通关增益奖励
   * @return
   */
   public int getMissionBuffAward()
   {
     return missionBuffAward;
   }

  /**
   * set 质点世界通关增益奖励
   */
   public void setMissionBuffAward(int missionBuffAward)
   {
      this.missionBuffAward = missionBuffAward;
   }
  /**
   * get 质点世界精灵感染度基础值(1场战斗完后精灵感染度增加，无论战斗结果)
   * @return
   */
   public int getPollutionBase()
   {
     return pollutionBase;
   }

  /**
   * set 质点世界精灵感染度基础值(1场战斗完后精灵感染度增加，无论战斗结果)
   */
   public void setPollutionBase(int pollutionBase)
   {
      this.pollutionBase = pollutionBase;
   }
  /**
   * get 精灵感染度加重增幅(万分比)
   * @return
   */
   public int getPollutionDeepen()
   {
     return pollutionDeepen;
   }

  /**
   * set 精灵感染度加重增幅(万分比)
   */
   public void setPollutionDeepen(int pollutionDeepen)
   {
      this.pollutionDeepen = pollutionDeepen;
   }
  /**
   * get 感染度加重的精灵(填hero表中精灵ID)
   * @return
   */
   public int[] getPollutionHero()
   {
     return pollutionHero;
   }

  /**
   * set 感染度加重的精灵(填hero表中精灵ID)
   */
   public void setPollutionHero(int[] pollutionHero)
   {
      this.pollutionHero = pollutionHero;
   }
  /**
   * get 格子刷出基础数量(3x3区域，定位1圈)
   * @return
   */
   public int getTileAppearing()
   {
     return tileAppearing;
   }

  /**
   * set 格子刷出基础数量(3x3区域，定位1圈)
   */
   public void setTileAppearing(int tileAppearing)
   {
      this.tileAppearing = tileAppearing;
   }
  /**
   * get 移动解锁格子消耗能源点数(只有解锁时才扣除)
   * @return
   */
   public int getUnlockCost()
   {
     return unlockCost;
   }

  /**
   * set 移动解锁格子消耗能源点数(只有解锁时才扣除)
   */
   public void setUnlockCost(int unlockCost)
   {
      this.unlockCost = unlockCost;
   }
  /**
   * get 探索区域扫描消耗能源点数(成功或失败都要扣除)
   * @return
   */
   public int getScanCost()
   {
     return scanCost;
   }

  /**
   * set 探索区域扫描消耗能源点数(成功或失败都要扣除)
   */
   public void setScanCost(int scanCost)
   {
      this.scanCost = scanCost;
   }
  /**
   * get 事件影响格子出现数量(任务或事件ID，出现圈数)
   * @return
   */
   public java.util.List getTileAppearingforEvent()
   {
     return tileAppearingforEvent;
   }

  /**
   * set 事件影响格子出现数量(任务或事件ID，出现圈数)
   */
   public void setTileAppearingforEvent(java.util.List tileAppearingforEvent)
   {
      this.tileAppearingforEvent = tileAppearingforEvent;
   }
  /**
   * get 质点世界任务目标类型(填kabalaMission表中的任务ID,配置顺序即为任务进行顺序)
   * @return
   */
   public java.util.List getMissionParam()
   {
     return missionParam;
   }

  /**
   * set 质点世界任务目标类型(填kabalaMission表中的任务ID,配置顺序即为任务进行顺序)
   */
   public void setMissionParam(java.util.List missionParam)
   {
      this.missionParam = missionParam;
   }
  /**
   * get 任务流程是否随机(0-不随机，1-随机)
   * @return
   */
   public boolean getIsRandom()
   {
     return isRandom;
   }

  /**
   * set 任务流程是否随机(0-不随机，1-随机)
   */
   public void setIsRandom(boolean isRandom)
   {
      this.isRandom = isRandom;
   }
  /**
   * get 质点世界终极任务(填mission表中Boss任务ID)
   * @return
   */
   public int getFinalMission()
   {
     return finalMission;
   }

  /**
   * set 质点世界终极任务(填mission表中Boss任务ID)
   */
   public void setFinalMission(int finalMission)
   {
      this.finalMission = finalMission;
   }
  /**
   * get 任务的引导事件(填任务ID和已出现格子数量，1个任务包含多个子任务时填第一个子任务ID)
   * @return
   */
   public java.util.List getMissionGuideEvent()
   {
     return missionGuideEvent;
   }

  /**
   * set 任务的引导事件(填任务ID和已出现格子数量，1个任务包含多个子任务时填第一个子任务ID)
   */
   public void setMissionGuideEvent(java.util.List missionGuideEvent)
   {
      this.missionGuideEvent = missionGuideEvent;
   }
  /**
   * get 事件-可见战斗参数(填任务事件表中可见战斗事件ID，按数量随机，生成参数=数量-优先级-间距)
   * @return
   */
   public java.util.List getFightingEvent()
   {
     return fightingEvent;
   }

  /**
   * set 事件-可见战斗参数(填任务事件表中可见战斗事件ID，按数量随机，生成参数=数量-优先级-间距)
   */
   public void setFightingEvent(java.util.List fightingEvent)
   {
      this.fightingEvent = fightingEvent;
   }
  /**
   * get 据点事件
   * @return
   */
   public int[] getStrongpointEvent()
   {
     return strongpointEvent;
   }

  /**
   * set 据点事件
   */
   public void setStrongpointEvent(int[] strongpointEvent)
   {
      this.strongpointEvent = strongpointEvent;
   }
  /**
   * get 探索点事件
   * @return
   */
   public int[] getExploreEvent()
   {
     return exploreEvent;
   }

  /**
   * set 探索点事件
   */
   public void setExploreEvent(int[] exploreEvent)
   {
      this.exploreEvent = exploreEvent;
   }
  /**
   * get 事件-伏击战斗参数(填多个dungeonLevel表中伏击战斗关卡ID，触发时随机一个)
   * @return
   */
   public int[] getAmbushEvent()
   {
     return ambushEvent;
   }

  /**
   * set 事件-伏击战斗参数(填多个dungeonLevel表中伏击战斗关卡ID，触发时随机一个)
   */
   public void setAmbushEvent(int[] ambushEvent)
   {
      this.ambushEvent = ambushEvent;
   }
  /**
   * get 伏击触发概率(初始概率%+每移动1格的概率%，万分比)
   * @return
   */
   public java.util.List getAmbushChance()
   {
     return ambushChance;
   }

  /**
   * set 伏击触发概率(初始概率%+每移动1格的概率%，万分比)
   */
   public void setAmbushChance(java.util.List ambushChance)
   {
      this.ambushChance = ambushChance;
   }
  /**
   * get 伏击战斗能源消耗
   * @return
   */
   public int getAmbushCost()
   {
     return ambushCost;
   }

  /**
   * set 伏击战斗能源消耗
   */
   public void setAmbushCost(int ambushCost)
   {
      this.ambushCost = ambushCost;
   }
  /**
   * get 宝箱事件参数(填事件表中宝箱事件ID，按数量随机，生成参数=数量-优先级-间距)
   * @return
   */
   public java.util.List getRandomEvent()
   {
     return randomEvent;
   }

  /**
   * set 宝箱事件参数(填事件表中宝箱事件ID，按数量随机，生成参数=数量-优先级-间距)
   */
   public void setRandomEvent(java.util.List randomEvent)
   {
      this.randomEvent = randomEvent;
   }
  /**
   * get 商店事件参数(填商店事件ID，生成参数=数量-优先级-间距)
   * @return
   */
   public java.util.List getShopEvent()
   {
     return shopEvent;
   }

  /**
   * set 商店事件参数(填商店事件ID，生成参数=数量-优先级-间距)
   */
   public void setShopEvent(java.util.List shopEvent)
   {
      this.shopEvent = shopEvent;
   }
  /**
   * get 商店出售道具ID(kabalashop表中商品ID)
   * @return
   */
   public int[] getItemID()
   {
     return itemID;
   }

  /**
   * set 商店出售道具ID(kabalashop表中商品ID)
   */
   public void setItemID(int[] itemID)
   {
      this.itemID = itemID;
   }
  /**
   * get 传送事件参数(填事件表中的传送点ID)
   * @return
   */
   public int[] getTeleportEvent()
   {
     return teleportEvent;
   }

  /**
   * set 传送事件参数(填事件表中的传送点ID)
   */
   public void setTeleportEvent(int[] teleportEvent)
   {
      this.teleportEvent = teleportEvent;
   }
}
