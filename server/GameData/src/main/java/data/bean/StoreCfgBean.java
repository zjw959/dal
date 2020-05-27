/**
 * Auto generated, do not edit it
 *
 * Store
 */
package data.bean;

public class StoreCfgBean 
{
  private int id; // ID
  private String name; // 名称
  private int storeType; // 商店类型（1.补给 2.城建 3.微信商店 4.十香生日活动商店）
  private int commoditySupplyType; // 货源类型（1.固定类商城。2.随机商品商城）
  private int rank; // 排序
  private int openContType; // 开启条件类型（1.玩家等级。2.需要通过的关卡）
  private int openContVal; // 开启条件值
  private int openTimeType; // 开启时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
  private int[] openTime; // 开启时间（分钟）
  private String autoRefreshCorn; // 自动刷新时间（分钟）
  private boolean manualRefresh; // 是否可以手动刷新
  private int refreshCostId; // 刷新消耗ID（货币id）
  private int[] refreshCostNum; // 刷新消耗数量
  private int[] showCurrency; // 展示货币类型
   
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
   * get 名称
   * @return
   */
   public String getName()
   {
     return name;
   }

  /**
   * set 名称
   */
   public void setName(String name)
   {
      this.name = name;
   }
  /**
   * get 商店类型（1.补给 2.城建 3.微信商店 4.十香生日活动商店）
   * @return
   */
   public int getStoreType()
   {
     return storeType;
   }

  /**
   * set 商店类型（1.补给 2.城建 3.微信商店 4.十香生日活动商店）
   */
   public void setStoreType(int storeType)
   {
      this.storeType = storeType;
   }
  /**
   * get 货源类型（1.固定类商城。2.随机商品商城）
   * @return
   */
   public int getCommoditySupplyType()
   {
     return commoditySupplyType;
   }

  /**
   * set 货源类型（1.固定类商城。2.随机商品商城）
   */
   public void setCommoditySupplyType(int commoditySupplyType)
   {
      this.commoditySupplyType = commoditySupplyType;
   }
  /**
   * get 排序
   * @return
   */
   public int getRank()
   {
     return rank;
   }

  /**
   * set 排序
   */
   public void setRank(int rank)
   {
      this.rank = rank;
   }
  /**
   * get 开启条件类型（1.玩家等级。2.需要通过的关卡）
   * @return
   */
   public int getOpenContType()
   {
     return openContType;
   }

  /**
   * set 开启条件类型（1.玩家等级。2.需要通过的关卡）
   */
   public void setOpenContType(int openContType)
   {
      this.openContType = openContType;
   }
  /**
   * get 开启条件值
   * @return
   */
   public int getOpenContVal()
   {
     return openContVal;
   }

  /**
   * set 开启条件值
   */
   public void setOpenContVal(int openContVal)
   {
      this.openContVal = openContVal;
   }
  /**
   * get 开启时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
   * @return
   */
   public int getOpenTimeType()
   {
     return openTimeType;
   }

  /**
   * set 开启时间类型（1.任意时间。2.每日固定时刻。3.每周固定时刻。）
   */
   public void setOpenTimeType(int openTimeType)
   {
      this.openTimeType = openTimeType;
   }
  /**
   * get 开启时间（分钟）
   * @return
   */
   public int[] getOpenTime()
   {
     return openTime;
   }

  /**
   * set 开启时间（分钟）
   */
   public void setOpenTime(int[] openTime)
   {
      this.openTime = openTime;
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
  /**
   * get 是否可以手动刷新
   * @return
   */
   public boolean getManualRefresh()
   {
     return manualRefresh;
   }

  /**
   * set 是否可以手动刷新
   */
   public void setManualRefresh(boolean manualRefresh)
   {
      this.manualRefresh = manualRefresh;
   }
  /**
   * get 刷新消耗ID（货币id）
   * @return
   */
   public int getRefreshCostId()
   {
     return refreshCostId;
   }

  /**
   * set 刷新消耗ID（货币id）
   */
   public void setRefreshCostId(int refreshCostId)
   {
      this.refreshCostId = refreshCostId;
   }
  /**
   * get 刷新消耗数量
   * @return
   */
   public int[] getRefreshCostNum()
   {
     return refreshCostNum;
   }

  /**
   * set 刷新消耗数量
   */
   public void setRefreshCostNum(int[] refreshCostNum)
   {
      this.refreshCostNum = refreshCostNum;
   }
  /**
   * get 展示货币类型
   * @return
   */
   public int[] getShowCurrency()
   {
     return showCurrency;
   }

  /**
   * set 展示货币类型
   */
   public void setShowCurrency(int[] showCurrency)
   {
      this.showCurrency = showCurrency;
   }
}
