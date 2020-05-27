/**
 * Auto generated, do not edit it
 *
 * Handworkbase
 */
package data.bean;

public class HandworkbaseCfgBean 
{
  private int id; // ID
  private String callTableName; // 调用剧情表格文件名
  private int datingId; // 功能类型（约会）
  private int handworkType; // 手工类型
  private String name; // 手工名称
  private int presentId; // 展示手工（对应item表中物品ID）
  private java.util.Map ability; // 所需能力
  private java.util.Map materials; // 所需材料
  private int order; // 手工排序
  private java.util.List integral; // 奖励手工（所需积分,物品ID,数量）
  private java.util.Map jump; // 分数对应跳转（约会）
  private int maxintegral; // 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
  private int worktime; // 制作总时长
  private int natureTime; // 每秒自然增长积分
  private int additiontime; // 标记区域中每秒积分
  private java.util.Map excursion; // 剪刀区域偏移权重[像素,权重]
  private int excursiontime; // 剪刀偏移间隔时间（单位：毫秒）
  private int upcastspeed; // 专注区域上抛速度（每次点击给予的速度）
  private int dropspeed; // 专注区域下落速度（下落时额外赋予速度）
  private int zonesize; // 标记区域大小（只用配置高）
   
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
   * get 调用剧情表格文件名
   * @return
   */
   public String getCallTableName()
   {
     return callTableName;
   }

  /**
   * set 调用剧情表格文件名
   */
   public void setCallTableName(String callTableName)
   {
      this.callTableName = callTableName;
   }
  /**
   * get 功能类型（约会）
   * @return
   */
   public int getDatingId()
   {
     return datingId;
   }

  /**
   * set 功能类型（约会）
   */
   public void setDatingId(int datingId)
   {
      this.datingId = datingId;
   }
  /**
   * get 手工类型
   * @return
   */
   public int getHandworkType()
   {
     return handworkType;
   }

  /**
   * set 手工类型
   */
   public void setHandworkType(int handworkType)
   {
      this.handworkType = handworkType;
   }
  /**
   * get 手工名称
   * @return
   */
   public String getName()
   {
     return name;
   }

  /**
   * set 手工名称
   */
   public void setName(String name)
   {
      this.name = name;
   }
  /**
   * get 展示手工（对应item表中物品ID）
   * @return
   */
   public int getPresentId()
   {
     return presentId;
   }

  /**
   * set 展示手工（对应item表中物品ID）
   */
   public void setPresentId(int presentId)
   {
      this.presentId = presentId;
   }
  /**
   * get 所需能力
   * @return
   */
   public java.util.Map getAbility()
   {
     return ability;
   }

  /**
   * set 所需能力
   */
   public void setAbility(java.util.Map ability)
   {
      this.ability = ability;
   }
  /**
   * get 所需材料
   * @return
   */
   public java.util.Map getMaterials()
   {
     return materials;
   }

  /**
   * set 所需材料
   */
   public void setMaterials(java.util.Map materials)
   {
      this.materials = materials;
   }
  /**
   * get 手工排序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 手工排序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 奖励手工（所需积分,物品ID,数量）
   * @return
   */
   public java.util.List getIntegral()
   {
     return integral;
   }

  /**
   * set 奖励手工（所需积分,物品ID,数量）
   */
   public void setIntegral(java.util.List integral)
   {
      this.integral = integral;
   }
  /**
   * get 分数对应跳转（约会）
   * @return
   */
   public java.util.Map getJump()
   {
     return jump;
   }

  /**
   * set 分数对应跳转（约会）
   */
   public void setJump(java.util.Map jump)
   {
      this.jump = jump;
   }
  /**
   * get 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
   * @return
   */
   public int getMaxintegral()
   {
     return maxintegral;
   }

  /**
   * set 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
   */
   public void setMaxintegral(int maxintegral)
   {
      this.maxintegral = maxintegral;
   }
  /**
   * get 制作总时长
   * @return
   */
   public int getWorktime()
   {
     return worktime;
   }

  /**
   * set 制作总时长
   */
   public void setWorktime(int worktime)
   {
      this.worktime = worktime;
   }
  /**
   * get 每秒自然增长积分
   * @return
   */
   public int getNatureTime()
   {
     return natureTime;
   }

  /**
   * set 每秒自然增长积分
   */
   public void setNatureTime(int natureTime)
   {
      this.natureTime = natureTime;
   }
  /**
   * get 标记区域中每秒积分
   * @return
   */
   public int getAdditiontime()
   {
     return additiontime;
   }

  /**
   * set 标记区域中每秒积分
   */
   public void setAdditiontime(int additiontime)
   {
      this.additiontime = additiontime;
   }
  /**
   * get 剪刀区域偏移权重[像素,权重]
   * @return
   */
   public java.util.Map getExcursion()
   {
     return excursion;
   }

  /**
   * set 剪刀区域偏移权重[像素,权重]
   */
   public void setExcursion(java.util.Map excursion)
   {
      this.excursion = excursion;
   }
  /**
   * get 剪刀偏移间隔时间（单位：毫秒）
   * @return
   */
   public int getExcursiontime()
   {
     return excursiontime;
   }

  /**
   * set 剪刀偏移间隔时间（单位：毫秒）
   */
   public void setExcursiontime(int excursiontime)
   {
      this.excursiontime = excursiontime;
   }
  /**
   * get 专注区域上抛速度（每次点击给予的速度）
   * @return
   */
   public int getUpcastspeed()
   {
     return upcastspeed;
   }

  /**
   * set 专注区域上抛速度（每次点击给予的速度）
   */
   public void setUpcastspeed(int upcastspeed)
   {
      this.upcastspeed = upcastspeed;
   }
  /**
   * get 专注区域下落速度（下落时额外赋予速度）
   * @return
   */
   public int getDropspeed()
   {
     return dropspeed;
   }

  /**
   * set 专注区域下落速度（下落时额外赋予速度）
   */
   public void setDropspeed(int dropspeed)
   {
      this.dropspeed = dropspeed;
   }
  /**
   * get 标记区域大小（只用配置高）
   * @return
   */
   public int getZonesize()
   {
     return zonesize;
   }

  /**
   * set 标记区域大小（只用配置高）
   */
   public void setZonesize(int zonesize)
   {
      this.zonesize = zonesize;
   }
}
