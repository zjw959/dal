/**
 * Auto generated, do not edit it
 *
 * CityState
 */
package data.bean;

public class CityStateCfgBean 
{
  private int id; // id
  private String stateIcon; // 状态图标
  private int weight; // 状态权重
  private int datingType; // 对应约会类型
  private int lateTime; // 状态持续时间
  private java.util.Map trigger; // 状态触发
  private java.util.Map effect; // 状态处理
   
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
   * get 状态图标
   * @return
   */
   public String getStateIcon()
   {
     return stateIcon;
   }

  /**
   * set 状态图标
   */
   public void setStateIcon(String stateIcon)
   {
      this.stateIcon = stateIcon;
   }
  /**
   * get 状态权重
   * @return
   */
   public int getWeight()
   {
     return weight;
   }

  /**
   * set 状态权重
   */
   public void setWeight(int weight)
   {
      this.weight = weight;
   }
  /**
   * get 对应约会类型
   * @return
   */
   public int getDatingType()
   {
     return datingType;
   }

  /**
   * set 对应约会类型
   */
   public void setDatingType(int datingType)
   {
      this.datingType = datingType;
   }
  /**
   * get 状态持续时间
   * @return
   */
   public int getLateTime()
   {
     return lateTime;
   }

  /**
   * set 状态持续时间
   */
   public void setLateTime(int lateTime)
   {
      this.lateTime = lateTime;
   }
  /**
   * get 状态触发
   * @return
   */
   public java.util.Map getTrigger()
   {
     return trigger;
   }

  /**
   * set 状态触发
   */
   public void setTrigger(java.util.Map trigger)
   {
      this.trigger = trigger;
   }
  /**
   * get 状态处理
   * @return
   */
   public java.util.Map getEffect()
   {
     return effect;
   }

  /**
   * set 状态处理
   */
   public void setEffect(java.util.Map effect)
   {
      this.effect = effect;
   }
}
