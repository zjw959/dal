/**
 * Auto generated, do not edit it
 *
 * Recharge
 */
package data.bean;

public class RechargeCfgBean 
{
  private int id; // id
  private float price; // 价格
  private int type; // 类型（1：月卡 2：钻石和礼包）
  private int goodsId; // 商品id(对应RechargeGiftBag和MonthCard中的id)
  private int channelGoodsId; // 平台商品id
  private int channelId; // 平台id
  private int channelAppId; // 平台appid
   
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
   * get 价格
   * @return
   */
   public float getPrice()
   {
     return price;
   }

  /**
   * set 价格
   */
   public void setPrice(float price)
   {
      this.price = price;
   }
  /**
   * get 类型（1：月卡 2：钻石和礼包）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 类型（1：月卡 2：钻石和礼包）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 商品id(对应RechargeGiftBag和MonthCard中的id)
   * @return
   */
   public int getGoodsId()
   {
     return goodsId;
   }

  /**
   * set 商品id(对应RechargeGiftBag和MonthCard中的id)
   */
   public void setGoodsId(int goodsId)
   {
      this.goodsId = goodsId;
   }
  /**
   * get 平台商品id
   * @return
   */
   public int getChannelGoodsId()
   {
     return channelGoodsId;
   }

  /**
   * set 平台商品id
   */
   public void setChannelGoodsId(int channelGoodsId)
   {
      this.channelGoodsId = channelGoodsId;
   }
  /**
   * get 平台id
   * @return
   */
   public int getChannelId()
   {
     return channelId;
   }

  /**
   * set 平台id
   */
   public void setChannelId(int channelId)
   {
      this.channelId = channelId;
   }
  /**
   * get 平台appid
   * @return
   */
   public int getChannelAppId()
   {
     return channelAppId;
   }

  /**
   * set 平台appid
   */
   public void setChannelAppId(int channelAppId)
   {
      this.channelAppId = channelAppId;
   }
}
