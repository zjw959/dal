/**
 * Auto generated, do not edit it
 *
 * RechargeGiftBag
 */
package data.bean;

public class RechargeGiftBagCfgBean 
{
  private int id; // id
  private int type; // 所属界面（1.充值界面 2.礼包界面）
  private java.util.Map item; // 礼包
  private String name; // 商品名称
  private String icon; // icon
  private boolean tag; // 是否商品标签
  private String tagDes; // 标签内容1（首次购买前）
  private String tagDes2; // 标签内容2
  private String des1; // 说明文本
  private String des2; // 说明文本2
  private String name2; // 审核名称
  private String des3; // 审核描述
  private int order; // 客户端排序
  private int[] startDate; // 开始时间
  private int[] endDate; // 结束时间
  private int buyCount; // 购买次数
  private int resetType; // 重置类型（0：不重置 1：每日重置 2：周重置 3：月重置）
  private int resetDate; // 重置时间（默认为每周一、每月一日，否则周日为1，周一为2）
  private int[] playerLevel; // 玩家等级
  private java.util.Map firstBuyItem; // 首充奖励
   
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
   * get 所属界面（1.充值界面 2.礼包界面）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 所属界面（1.充值界面 2.礼包界面）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 礼包
   * @return
   */
   public java.util.Map getItem()
   {
     return item;
   }

  /**
   * set 礼包
   */
   public void setItem(java.util.Map item)
   {
      this.item = item;
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
   * get icon
   * @return
   */
   public String getIcon()
   {
     return icon;
   }

  /**
   * set icon
   */
   public void setIcon(String icon)
   {
      this.icon = icon;
   }
  /**
   * get 是否商品标签
   * @return
   */
   public boolean getTag()
   {
     return tag;
   }

  /**
   * set 是否商品标签
   */
   public void setTag(boolean tag)
   {
      this.tag = tag;
   }
  /**
   * get 标签内容1（首次购买前）
   * @return
   */
   public String getTagDes()
   {
     return tagDes;
   }

  /**
   * set 标签内容1（首次购买前）
   */
   public void setTagDes(String tagDes)
   {
      this.tagDes = tagDes;
   }
  /**
   * get 标签内容2
   * @return
   */
   public String getTagDes2()
   {
     return tagDes2;
   }

  /**
   * set 标签内容2
   */
   public void setTagDes2(String tagDes2)
   {
      this.tagDes2 = tagDes2;
   }
  /**
   * get 说明文本
   * @return
   */
   public String getDes1()
   {
     return des1;
   }

  /**
   * set 说明文本
   */
   public void setDes1(String des1)
   {
      this.des1 = des1;
   }
  /**
   * get 说明文本2
   * @return
   */
   public String getDes2()
   {
     return des2;
   }

  /**
   * set 说明文本2
   */
   public void setDes2(String des2)
   {
      this.des2 = des2;
   }
  /**
   * get 审核名称
   * @return
   */
   public String getName2()
   {
     return name2;
   }

  /**
   * set 审核名称
   */
   public void setName2(String name2)
   {
      this.name2 = name2;
   }
  /**
   * get 审核描述
   * @return
   */
   public String getDes3()
   {
     return des3;
   }

  /**
   * set 审核描述
   */
   public void setDes3(String des3)
   {
      this.des3 = des3;
   }
  /**
   * get 客户端排序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 客户端排序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 开始时间
   * @return
   */
   public int[] getStartDate()
   {
     return startDate;
   }

  /**
   * set 开始时间
   */
   public void setStartDate(int[] startDate)
   {
      this.startDate = startDate;
   }
  /**
   * get 结束时间
   * @return
   */
   public int[] getEndDate()
   {
     return endDate;
   }

  /**
   * set 结束时间
   */
   public void setEndDate(int[] endDate)
   {
      this.endDate = endDate;
   }
  /**
   * get 购买次数
   * @return
   */
   public int getBuyCount()
   {
     return buyCount;
   }

  /**
   * set 购买次数
   */
   public void setBuyCount(int buyCount)
   {
      this.buyCount = buyCount;
   }
  /**
   * get 重置类型（0：不重置 1：每日重置 2：周重置 3：月重置）
   * @return
   */
   public int getResetType()
   {
     return resetType;
   }

  /**
   * set 重置类型（0：不重置 1：每日重置 2：周重置 3：月重置）
   */
   public void setResetType(int resetType)
   {
      this.resetType = resetType;
   }
  /**
   * get 重置时间（默认为每周一、每月一日，否则周日为1，周一为2）
   * @return
   */
   public int getResetDate()
   {
     return resetDate;
   }

  /**
   * set 重置时间（默认为每周一、每月一日，否则周日为1，周一为2）
   */
   public void setResetDate(int resetDate)
   {
      this.resetDate = resetDate;
   }
  /**
   * get 玩家等级
   * @return
   */
   public int[] getPlayerLevel()
   {
     return playerLevel;
   }

  /**
   * set 玩家等级
   */
   public void setPlayerLevel(int[] playerLevel)
   {
      this.playerLevel = playerLevel;
   }
  /**
   * get 首充奖励
   * @return
   */
   public java.util.Map getFirstBuyItem()
   {
     return firstBuyItem;
   }

  /**
   * set 首充奖励
   */
   public void setFirstBuyItem(java.util.Map firstBuyItem)
   {
      this.firstBuyItem = firstBuyItem;
   }
}
