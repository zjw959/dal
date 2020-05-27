/**
 * Auto generated, do not edit it
 *
 * TriggerEvent
 */
package data.bean;

public class TriggerEventCfgBean 
{
  private int id; // id
  private int eventConditionId; // 事件条件ID
  private java.util.Map params; // 参数
  private java.util.Map result; // 事件结果
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
   * get 事件条件ID
   * @return
   */
   public int getEventConditionId()
   {
     return eventConditionId;
   }

  /**
   * set 事件条件ID
   */
   public void setEventConditionId(int eventConditionId)
   {
      this.eventConditionId = eventConditionId;
   }
  /**
   * get 参数
   * @return
   */
   public java.util.Map getParams()
   {
     return params;
   }

  /**
   * set 参数
   */
   public void setParams(java.util.Map params)
   {
      this.params = params;
   }
  /**
   * get 事件结果
   * @return
   */
   public java.util.Map getResult()
   {
     return result;
   }

  /**
   * set 事件结果
   */
   public void setResult(java.util.Map result)
   {
      this.result = result;
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
