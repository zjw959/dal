/**
 * Auto generated, do not edit it
 *
 * EventCondition
 */
package data.bean;

public class EventConditionCfgBean 
{
  private int id; // id
  private int eventId; // 事件ID
  private boolean history; // 是否需要检查历史数据
  private String remark; // 备注
   
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
   * get 事件ID
   * @return
   */
   public int getEventId()
   {
     return eventId;
   }

  /**
   * set 事件ID
   */
   public void setEventId(int eventId)
   {
      this.eventId = eventId;
   }
  /**
   * get 是否需要检查历史数据
   * @return
   */
   public boolean getHistory()
   {
     return history;
   }

  /**
   * set 是否需要检查历史数据
   */
   public void setHistory(boolean history)
   {
      this.history = history;
   }
  /**
   * get 备注
   * @return
   */
   public String getRemark()
   {
     return remark;
   }

  /**
   * set 备注
   */
   public void setRemark(String remark)
   {
      this.remark = remark;
   }
}
