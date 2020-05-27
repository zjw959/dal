/**
 * Auto generated, do not edit it
 *
 * Commodity
 */
package data.bean;

public class CommodityCfgBean 
{
  private int id; // id
  private String name; // 商品名称
  private int storeId; // 所属商店
  private int grid; // 格子编号
  private int weight; // 权重
  private int openContType; // 开启条件（1.玩家等级。2.需要达到的关卡）
  private int openContVal; // 开启值
  private int sellTimeType; // 出售时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
  private int[] sellTime; // 出售时间（分钟）
  private int limitType; // 限购（0.不限购。1.刷新时间内限购。2.服务器时间本天内限购。3.永久限购。5.全服限购且刷新时间重置。6.全服限购夸天重置。7.全服永久限购 8.本周限购 9.本月限购）
  private int limitVal; // 限购值
  private int serLimit; // 全服时个人限购值
  private java.util.Map goods; // 道具（id:数量）
  private int[] priceType; // 价格类型
  private int[] priceVal; // 价格数量
  private String des; // 描述
  private int tag; // 折扣
  private String autoRefreshCorn; // 自动刷新时间（分钟）
   
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
   * get 商品名称
   * @return
   */
   public String getName()
   {
     return name;
   }

  /**
   * set 商品名称
   */
   public void setName(String name)
   {
      this.name = name;
   }
  /**
   * get 所属商店
   * @return
   */
   public int getStoreId()
   {
     return storeId;
   }

  /**
   * set 所属商店
   */
   public void setStoreId(int storeId)
   {
      this.storeId = storeId;
   }
  /**
   * get 格子编号
   * @return
   */
   public int getGrid()
   {
     return grid;
   }

  /**
   * set 格子编号
   */
   public void setGrid(int grid)
   {
      this.grid = grid;
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
   * get 开启条件（1.玩家等级。2.需要达到的关卡）
   * @return
   */
   public int getOpenContType()
   {
     return openContType;
   }

  /**
   * set 开启条件（1.玩家等级。2.需要达到的关卡）
   */
   public void setOpenContType(int openContType)
   {
      this.openContType = openContType;
   }
  /**
   * get 开启值
   * @return
   */
   public int getOpenContVal()
   {
     return openContVal;
   }

  /**
   * set 开启值
   */
   public void setOpenContVal(int openContVal)
   {
      this.openContVal = openContVal;
   }
  /**
   * get 出售时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
   * @return
   */
   public int getSellTimeType()
   {
     return sellTimeType;
   }

  /**
   * set 出售时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
   */
   public void setSellTimeType(int sellTimeType)
   {
      this.sellTimeType = sellTimeType;
   }
  /**
   * get 出售时间（分钟）
   * @return
   */
   public int[] getSellTime()
   {
     return sellTime;
   }

  /**
   * set 出售时间（分钟）
   */
   public void setSellTime(int[] sellTime)
   {
      this.sellTime = sellTime;
   }
  /**
   * get 限购（0.不限购。1.刷新时间内限购。2.服务器时间本天内限购。3.永久限购。5.全服限购且刷新时间重置。6.全服限购夸天重置。7.全服永久限购 8.本周限购 9.本月限购）
   * @return
   */
   public int getLimitType()
   {
     return limitType;
   }

  /**
   * set 限购（0.不限购。1.刷新时间内限购。2.服务器时间本天内限购。3.永久限购。5.全服限购且刷新时间重置。6.全服限购夸天重置。7.全服永久限购 8.本周限购 9.本月限购）
   */
   public void setLimitType(int limitType)
   {
      this.limitType = limitType;
   }
  /**
   * get 限购值
   * @return
   */
   public int getLimitVal()
   {
     return limitVal;
   }

  /**
   * set 限购值
   */
   public void setLimitVal(int limitVal)
   {
      this.limitVal = limitVal;
   }
  /**
   * get 全服时个人限购值
   * @return
   */
   public int getSerLimit()
   {
     return serLimit;
   }

  /**
   * set 全服时个人限购值
   */
   public void setSerLimit(int serLimit)
   {
      this.serLimit = serLimit;
   }
  /**
   * get 道具（id:数量）
   * @return
   */
   public java.util.Map getGoods()
   {
     return goods;
   }

  /**
   * set 道具（id:数量）
   */
   public void setGoods(java.util.Map goods)
   {
      this.goods = goods;
   }
  /**
   * get 价格类型
   * @return
   */
   public int[] getPriceType()
   {
     return priceType;
   }

  /**
   * set 价格类型
   */
   public void setPriceType(int[] priceType)
   {
      this.priceType = priceType;
   }
  /**
   * get 价格数量
   * @return
   */
   public int[] getPriceVal()
   {
     return priceVal;
   }

  /**
   * set 价格数量
   */
   public void setPriceVal(int[] priceVal)
   {
      this.priceVal = priceVal;
   }
  /**
   * get 描述
   * @return
   */
   public String getDes()
   {
     return des;
   }

  /**
   * set 描述
   */
   public void setDes(String des)
   {
      this.des = des;
   }
  /**
   * get 折扣
   * @return
   */
   public int getTag()
   {
     return tag;
   }

  /**
   * set 折扣
   */
   public void setTag(int tag)
   {
      this.tag = tag;
   }
  /**
   * get 自动刷新时间（分钟）
   * @return
   */
   public String getAutoRefreshCorn()
   {
     return autoRefreshCorn;
   }

  /**
   * set 自动刷新时间（分钟）
   */
   public void setAutoRefreshCorn(String autoRefreshCorn)
   {
      this.autoRefreshCorn = autoRefreshCorn;
   }
}
