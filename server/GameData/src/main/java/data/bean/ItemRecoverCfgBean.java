/**
 * Auto generated, do not edit it
 *
 * ItemRecover
 */
package data.bean;

public class ItemRecoverCfgBean 
{
  private int id; // id
  private int itemId; // 道具ID
  private int cooldown; // 自动恢复时间间隔(秒)
  private int recoverCount; // 恢复数量
  private int maxRecoverCount; // 最大恢复上限
  private String[] price; // 购买价格
  private int resetBuyCountTime; // 购买重置时间
   
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
   * get 自动恢复时间间隔(秒)
   * @return
   */
   public int getCooldown()
   {
     return cooldown;
   }

  /**
   * set 自动恢复时间间隔(秒)
   */
   public void setCooldown(int cooldown)
   {
      this.cooldown = cooldown;
   }
  /**
   * get 恢复数量
   * @return
   */
   public int getRecoverCount()
   {
     return recoverCount;
   }

  /**
   * set 恢复数量
   */
   public void setRecoverCount(int recoverCount)
   {
      this.recoverCount = recoverCount;
   }
  /**
   * get 最大恢复上限
   * @return
   */
   public int getMaxRecoverCount()
   {
     return maxRecoverCount;
   }

  /**
   * set 最大恢复上限
   */
   public void setMaxRecoverCount(int maxRecoverCount)
   {
      this.maxRecoverCount = maxRecoverCount;
   }
  /**
   * get 购买价格
   * @return
   */
   public String[] getPrice()
   {
     return price;
   }

  /**
   * set 购买价格
   */
   public void setPrice(String[] price)
   {
      this.price = price;
   }
  /**
   * get 购买重置时间
   * @return
   */
   public int getResetBuyCountTime()
   {
     return resetBuyCountTime;
   }

  /**
   * set 购买重置时间
   */
   public void setResetBuyCountTime(int resetBuyCountTime)
   {
      this.resetBuyCountTime = resetBuyCountTime;
   }
}
