/**
 * Auto generated, do not edit it
 *
 * Equipment
 */
package data.bean;

public class EquipmentCfgBean extends BaseGoods
{
  private int id; // id
  private int superType; // 物品类型
  private int bagType; // 背包类型
  private int gridMax; // 单格上限
  private boolean pileUp; // 是否支持叠加
  private int totalMax; // 道具总数上限
  private java.util.Map convertMax; // 超出上限转换
  private int subType; // 灵装类型
  private int star; // 星级
  private int quality; // 品质
  private int cost; // 负重
  private int exp; // 提供经验
  private int initalLevel; // 初始等级
  private int maxLevel; // 等级上限
  private int[] combination; // 所属组合类型(Equipment_Combination中的组合类型id)
  private int suit; // 所属套装
  private java.util.Map baseAttribute; // 基础属性({属性号：属性值，属性号：属性值})
  private java.util.Map growthAttribute; // 成长属性
  private int combatNum; // 基础战力
  private int growthCombatNum; // 每级成长战力
  private java.util.List specialAttrLevelRange; // 各属性条目的等级范围
  private java.util.Map specialAttribute; // 特殊属性(类型id：权重，类型id：权重)
  private int inherentAttribute; // 固有属性
  private java.util.Map sellProfit; // 出售收益
  private java.util.Map dealProfit; // 交易收益
   
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
   * get 物品类型
   * @return
   */
   public int getSuperType()
   {
     return superType;
   }

  /**
   * set 物品类型
   */
   public void setSuperType(int superType)
   {
      this.superType = superType;
   }
  /**
   * get 背包类型
   * @return
   */
   public int getBagType()
   {
     return bagType;
   }

  /**
   * set 背包类型
   */
   public void setBagType(int bagType)
   {
      this.bagType = bagType;
   }
  /**
   * get 单格上限
   * @return
   */
   public int getGridMax()
   {
     return gridMax;
   }

  /**
   * set 单格上限
   */
   public void setGridMax(int gridMax)
   {
      this.gridMax = gridMax;
   }
  /**
   * get 是否支持叠加
   * @return
   */
   public boolean getPileUp()
   {
     return pileUp;
   }

  /**
   * set 是否支持叠加
   */
   public void setPileUp(boolean pileUp)
   {
      this.pileUp = pileUp;
   }
  /**
   * get 道具总数上限
   * @return
   */
   public int getTotalMax()
   {
     return totalMax;
   }

  /**
   * set 道具总数上限
   */
   public void setTotalMax(int totalMax)
   {
      this.totalMax = totalMax;
   }
  /**
   * get 超出上限转换
   * @return
   */
   public java.util.Map getConvertMax()
   {
     return convertMax;
   }

  /**
   * set 超出上限转换
   */
   public void setConvertMax(java.util.Map convertMax)
   {
      this.convertMax = convertMax;
   }
  /**
   * get 灵装类型
   * @return
   */
   public int getSubType()
   {
     return subType;
   }

  /**
   * set 灵装类型
   */
   public void setSubType(int subType)
   {
      this.subType = subType;
   }
  /**
   * get 星级
   * @return
   */
   public int getStar()
   {
     return star;
   }

  /**
   * set 星级
   */
   public void setStar(int star)
   {
      this.star = star;
   }
  /**
   * get 品质
   * @return
   */
   public int getQuality()
   {
     return quality;
   }

  /**
   * set 品质
   */
   public void setQuality(int quality)
   {
      this.quality = quality;
   }
  /**
   * get 负重
   * @return
   */
   public int getCost()
   {
     return cost;
   }

  /**
   * set 负重
   */
   public void setCost(int cost)
   {
      this.cost = cost;
   }
  /**
   * get 提供经验
   * @return
   */
   public int getExp()
   {
     return exp;
   }

  /**
   * set 提供经验
   */
   public void setExp(int exp)
   {
      this.exp = exp;
   }
  /**
   * get 初始等级
   * @return
   */
   public int getInitalLevel()
   {
     return initalLevel;
   }

  /**
   * set 初始等级
   */
   public void setInitalLevel(int initalLevel)
   {
      this.initalLevel = initalLevel;
   }
  /**
   * get 等级上限
   * @return
   */
   public int getMaxLevel()
   {
     return maxLevel;
   }

  /**
   * set 等级上限
   */
   public void setMaxLevel(int maxLevel)
   {
      this.maxLevel = maxLevel;
   }
  /**
   * get 所属组合类型(Equipment_Combination中的组合类型id)
   * @return
   */
   public int[] getCombination()
   {
     return combination;
   }

  /**
   * set 所属组合类型(Equipment_Combination中的组合类型id)
   */
   public void setCombination(int[] combination)
   {
      this.combination = combination;
   }
  /**
   * get 所属套装
   * @return
   */
   public int getSuit()
   {
     return suit;
   }

  /**
   * set 所属套装
   */
   public void setSuit(int suit)
   {
      this.suit = suit;
   }
  /**
   * get 基础属性({属性号：属性值，属性号：属性值})
   * @return
   */
   public java.util.Map getBaseAttribute()
   {
     return baseAttribute;
   }

  /**
   * set 基础属性({属性号：属性值，属性号：属性值})
   */
   public void setBaseAttribute(java.util.Map baseAttribute)
   {
      this.baseAttribute = baseAttribute;
   }
  /**
   * get 成长属性
   * @return
   */
   public java.util.Map getGrowthAttribute()
   {
     return growthAttribute;
   }

  /**
   * set 成长属性
   */
   public void setGrowthAttribute(java.util.Map growthAttribute)
   {
      this.growthAttribute = growthAttribute;
   }
  /**
   * get 基础战力
   * @return
   */
   public int getCombatNum()
   {
     return combatNum;
   }

  /**
   * set 基础战力
   */
   public void setCombatNum(int combatNum)
   {
      this.combatNum = combatNum;
   }
  /**
   * get 每级成长战力
   * @return
   */
   public int getGrowthCombatNum()
   {
     return growthCombatNum;
   }

  /**
   * set 每级成长战力
   */
   public void setGrowthCombatNum(int growthCombatNum)
   {
      this.growthCombatNum = growthCombatNum;
   }
  /**
   * get 各属性条目的等级范围
   * @return
   */
   public java.util.List getSpecialAttrLevelRange()
   {
     return specialAttrLevelRange;
   }

  /**
   * set 各属性条目的等级范围
   */
   public void setSpecialAttrLevelRange(java.util.List specialAttrLevelRange)
   {
      this.specialAttrLevelRange = specialAttrLevelRange;
   }
  /**
   * get 特殊属性(类型id：权重，类型id：权重)
   * @return
   */
   public java.util.Map getSpecialAttribute()
   {
     return specialAttribute;
   }

  /**
   * set 特殊属性(类型id：权重，类型id：权重)
   */
   public void setSpecialAttribute(java.util.Map specialAttribute)
   {
      this.specialAttribute = specialAttribute;
   }
  /**
   * get 固有属性
   * @return
   */
   public int getInherentAttribute()
   {
     return inherentAttribute;
   }

  /**
   * set 固有属性
   */
   public void setInherentAttribute(int inherentAttribute)
   {
      this.inherentAttribute = inherentAttribute;
   }
  /**
   * get 出售收益
   * @return
   */
   public java.util.Map getSellProfit()
   {
     return sellProfit;
   }

  /**
   * set 出售收益
   */
   public void setSellProfit(java.util.Map sellProfit)
   {
      this.sellProfit = sellProfit;
   }
  /**
   * get 交易收益
   * @return
   */
   public java.util.Map getDealProfit()
   {
     return dealProfit;
   }

  /**
   * set 交易收益
   */
   public void setDealProfit(java.util.Map dealProfit)
   {
      this.dealProfit = dealProfit;
   }
}
