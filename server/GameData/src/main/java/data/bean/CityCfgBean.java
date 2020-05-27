/**
 * Auto generated, do not edit it
 *
 * City
 */
package data.bean;

public class CityCfgBean 
{
  private int id; // ID
  private int buildType; // 区域类型（1打工区域2非打工3特殊）
  private int[] build; // 建筑列表
  private int timeType; // 开放类型（1常驻2每日时间3每周时间4特定时间段）
  private String openTime; // 开放时间
  private java.util.Map needItem; // 需求道具（1拥有道具2消耗道具）
   
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
   * get 区域类型（1打工区域2非打工3特殊）
   * @return
   */
   public int getBuildType()
   {
     return buildType;
   }

  /**
   * set 区域类型（1打工区域2非打工3特殊）
   */
   public void setBuildType(int buildType)
   {
      this.buildType = buildType;
   }
  /**
   * get 建筑列表
   * @return
   */
   public int[] getBuild()
   {
     return build;
   }

  /**
   * set 建筑列表
   */
   public void setBuild(int[] build)
   {
      this.build = build;
   }
  /**
   * get 开放类型（1常驻2每日时间3每周时间4特定时间段）
   * @return
   */
   public int getTimeType()
   {
     return timeType;
   }

  /**
   * set 开放类型（1常驻2每日时间3每周时间4特定时间段）
   */
   public void setTimeType(int timeType)
   {
      this.timeType = timeType;
   }
  /**
   * get 开放时间
   * @return
   */
   public String getOpenTime()
   {
     return openTime;
   }

  /**
   * set 开放时间
   */
   public void setOpenTime(String openTime)
   {
      this.openTime = openTime;
   }
  /**
   * get 需求道具（1拥有道具2消耗道具）
   * @return
   */
   public java.util.Map getNeedItem()
   {
     return needItem;
   }

  /**
   * set 需求道具（1拥有道具2消耗道具）
   */
   public void setNeedItem(java.util.Map needItem)
   {
      this.needItem = needItem;
   }
}
