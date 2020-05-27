/**
 * Auto generated, do not edit it
 *
 * MultiConditionEvent
 */
package data.bean;

public class MultiConditionEventCfgBean 
{
  private int id; // id
  private int eventConditionId; // 事件条件
  private java.util.Map condition; // 条件（buildId:建筑等级，buildLvl:建筑等级）
  private int weight; // 权重
  private String[] describe; // 客户端调用字符串
  private java.util.Map result; // 结果
   
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
   * get 事件条件
   * @return
   */
   public int getEventConditionId()
   {
     return eventConditionId;
   }

  /**
   * set 事件条件
   */
   public void setEventConditionId(int eventConditionId)
   {
      this.eventConditionId = eventConditionId;
   }
  /**
   * get 条件（buildId:建筑等级，buildLvl:建筑等级）
   * @return
   */
   public java.util.Map getCondition()
   {
     return condition;
   }

  /**
   * set 条件（buildId:建筑等级，buildLvl:建筑等级）
   */
   public void setCondition(java.util.Map condition)
   {
      this.condition = condition;
   }
  /**
   * get 权重
   * @return
   */
   public int getWeight()
   {
     return weight;
   }

  /**
   * set 权重
   */
   public void setWeight(int weight)
   {
      this.weight = weight;
   }
  /**
   * get 客户端调用字符串
   * @return
   */
   public String[] getDescribe()
   {
     return describe;
   }

  /**
   * set 客户端调用字符串
   */
   public void setDescribe(String[] describe)
   {
      this.describe = describe;
   }
  /**
   * get 结果
   * @return
   */
   public java.util.Map getResult()
   {
     return result;
   }

  /**
   * set 结果
   */
   public void setResult(java.util.Map result)
   {
      this.result = result;
   }
}
