/**
 * Auto generated, do not edit it
 *
 * ItemTime
 */
package data.bean;

public class ItemTimeCfgBean extends BaseGoods
{
  private int id; // id
  private int itemId; // 道具ID
  private int limitType; // 限制类型（1:获得后一定分钟有效）
  private int limitData; // 限制参数
   
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
   * get 道具ID
   * @return
   */
   public int getItemId()
   {
     return itemId;
   }

  /**
   * set 道具ID
   */
   public void setItemId(int itemId)
   {
      this.itemId = itemId;
   }
  /**
   * get 限制类型（1:获得后一定分钟有效）
   * @return
   */
   public int getLimitType()
   {
     return limitType;
   }

  /**
   * set 限制类型（1:获得后一定分钟有效）
   */
   public void setLimitType(int limitType)
   {
      this.limitType = limitType;
   }
  /**
   * get 限制参数
   * @return
   */
   public int getLimitData()
   {
     return limitData;
   }

  /**
   * set 限制参数
   */
   public void setLimitData(int limitData)
   {
      this.limitData = limitData;
   }
}
