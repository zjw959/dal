/**
 * Auto generated, do not edit it
 *
 * KabalaShop
 */
package data.bean;

public class KabalaShopCfgBean 
{
  private int id; // id
  private int itemId; // 道具Id
  private int maxNum; // 购买上限
  private int itemType; // 商品类型(1-道具，2-buff)
  private int[] price; // 价格
   
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
   * get 道具Id
   * @return
   */
   public int getItemId()
   {
     return itemId;
   }

  /**
   * set 道具Id
   */
   public void setItemId(int itemId)
   {
      this.itemId = itemId;
   }
  /**
   * get 购买上限
   * @return
   */
   public int getMaxNum()
   {
     return maxNum;
   }

  /**
   * set 购买上限
   */
   public void setMaxNum(int maxNum)
   {
      this.maxNum = maxNum;
   }
  /**
   * get 商品类型(1-道具，2-buff)
   * @return
   */
   public int getItemType()
   {
     return itemType;
   }

  /**
   * set 商品类型(1-道具，2-buff)
   */
   public void setItemType(int itemType)
   {
      this.itemType = itemType;
   }
  /**
   * get 价格
   * @return
   */
   public int[] getPrice()
   {
     return price;
   }

  /**
   * set 价格
   */
   public void setPrice(int[] price)
   {
      this.price = price;
   }
}
