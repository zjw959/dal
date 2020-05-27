/**
 * Auto generated, do not edit it
 *
 * KabalaEvent
 */
package data.bean;

public class KabalaEventCfgBean 
{
  private int id; // ID
  private int eventType; // 事件类型(1-直接战斗，2-收集任务道具，3-占领据点，4-任务剧情，5-探索区域，6-拾取随机奖励，7-传送事件，8-商店事件)
  private int eventSubType; // 事件子类型(1001-BOSS击杀，1002-收集道具-常规，1012-收集道具-剧情，1022-收集道具-战斗（任务道具配在关卡掉落里），1003-击杀目标-数量，1013-击杀目标-具体怪物及数量，1004-探索区域（编辑器中探索目标地块ID），1005-占领据点，1006-剧情事件-常规；2001-可见战斗，2002-宝箱事件-道具，2012-宝箱事件-buff/debuff，2003-传送事件-随机传送，2013-传送事件-定点传送，2004-商店事件)
  private java.util.List eventParam; // 事件类型参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
  private int tileID; // 地图编辑器中地块ID(填地图导出的json文件中的数字ID)
  private int[] eventText; // 事件名文本(主要是传送点名用)
  private int oilCost; // 事件能源点数消耗
  private boolean isDelete; // 触发后是否删除模型
  private boolean isAgain; // 事件可否重复进行
   
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
   * get 事件类型(1-直接战斗，2-收集任务道具，3-占领据点，4-任务剧情，5-探索区域，6-拾取随机奖励，7-传送事件，8-商店事件)
   * @return
   */
   public int getEventType()
   {
     return eventType;
   }

  /**
   * set 事件类型(1-直接战斗，2-收集任务道具，3-占领据点，4-任务剧情，5-探索区域，6-拾取随机奖励，7-传送事件，8-商店事件)
   */
   public void setEventType(int eventType)
   {
      this.eventType = eventType;
   }
  /**
   * get 事件子类型(1001-BOSS击杀，1002-收集道具-常规，1012-收集道具-剧情，1022-收集道具-战斗（任务道具配在关卡掉落里），1003-击杀目标-数量，1013-击杀目标-具体怪物及数量，1004-探索区域（编辑器中探索目标地块ID），1005-占领据点，1006-剧情事件-常规；2001-可见战斗，2002-宝箱事件-道具，2012-宝箱事件-buff/debuff，2003-传送事件-随机传送，2013-传送事件-定点传送，2004-商店事件)
   * @return
   */
   public int getEventSubType()
   {
     return eventSubType;
   }

  /**
   * set 事件子类型(1001-BOSS击杀，1002-收集道具-常规，1012-收集道具-剧情，1022-收集道具-战斗（任务道具配在关卡掉落里），1003-击杀目标-数量，1013-击杀目标-具体怪物及数量，1004-探索区域（编辑器中探索目标地块ID），1005-占领据点，1006-剧情事件-常规；2001-可见战斗，2002-宝箱事件-道具，2012-宝箱事件-buff/debuff，2003-传送事件-随机传送，2013-传送事件-定点传送，2004-商店事件)
   */
   public void setEventSubType(int eventSubType)
   {
      this.eventSubType = eventSubType;
   }
  /**
   * get 事件类型参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
   * @return
   */
   public java.util.List getEventParam()
   {
     return eventParam;
   }

  /**
   * set 事件类型参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
   */
   public void setEventParam(java.util.List eventParam)
   {
      this.eventParam = eventParam;
   }
  /**
   * get 地图编辑器中地块ID(填地图导出的json文件中的数字ID)
   * @return
   */
   public int getTileID()
   {
     return tileID;
   }

  /**
   * set 地图编辑器中地块ID(填地图导出的json文件中的数字ID)
   */
   public void setTileID(int tileID)
   {
      this.tileID = tileID;
   }
  /**
   * get 事件名文本(主要是传送点名用)
   * @return
   */
   public int[] getEventText()
   {
     return eventText;
   }

  /**
   * set 事件名文本(主要是传送点名用)
   */
   public void setEventText(int[] eventText)
   {
      this.eventText = eventText;
   }
  /**
   * get 事件能源点数消耗
   * @return
   */
   public int getOilCost()
   {
     return oilCost;
   }

  /**
   * set 事件能源点数消耗
   */
   public void setOilCost(int oilCost)
   {
      this.oilCost = oilCost;
   }
  /**
   * get 触发后是否删除模型
   * @return
   */
   public boolean getIsDelete()
   {
     return isDelete;
   }

  /**
   * set 触发后是否删除模型
   */
   public void setIsDelete(boolean isDelete)
   {
      this.isDelete = isDelete;
   }
  /**
   * get 事件可否重复进行
   * @return
   */
   public boolean getIsAgain()
   {
     return isAgain;
   }

  /**
   * set 事件可否重复进行
   */
   public void setIsAgain(boolean isAgain)
   {
      this.isAgain = isAgain;
   }
}
