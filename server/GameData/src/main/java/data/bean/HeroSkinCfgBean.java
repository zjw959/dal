/**
 * Auto generated, do not edit it
 *
 * HeroSkin
 */
package data.bean;

public class HeroSkinCfgBean extends BaseGoods
{
  private int id; // ID
  private String name; // 备注
  private int heroFlyForm; // 精灵飞行初始形态(flyForm表中精灵ID)
  private int wingmanFlyForm; // 僚机飞行初始形态(flyForm表中僚机ID)
  private int nameTextId; // 皮肤名字
  private int superType; // 道具类型
  private int subType; // 子类型
  private int smallType; // 小类型
  private String icon; // 图标
  private int bagType; // 所属背包
  private int order; // 排序索引
  private int star; // 星级
  private int quality; // 品质
  private int totalMax; // 总上限
  private boolean pileUp; // 是否支持叠加
  private int gridMax; // 单格上限
  private java.util.Map convertMax; // 达总上限转换
  private java.util.Map sellProfit; // 出售收益
  private java.util.Map dealProfit; // 交易收益
  private String des; // 描述
  private int desTextId; // 描述文字ID
  private int heroPower; // 角色能量机制
  private int beginForm; // 战斗初始形态
  private int[] allForm; // 战斗所有形态
  private int[] passivitySkills; // 被动技能配置
  private int[] angelSkills; // 技能类型（1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7切换登场技能,8下落技能,,9起身震飞,10被动）
  private int cost; // 价值
   
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
   * get 备注
   * @return
   */
   public String getName()
   {
     return name;
   }

  /**
   * set 备注
   */
   public void setName(String name)
   {
      this.name = name;
   }
  /**
   * get 精灵飞行初始形态(flyForm表中精灵ID)
   * @return
   */
   public int getHeroFlyForm()
   {
     return heroFlyForm;
   }

  /**
   * set 精灵飞行初始形态(flyForm表中精灵ID)
   */
   public void setHeroFlyForm(int heroFlyForm)
   {
      this.heroFlyForm = heroFlyForm;
   }
  /**
   * get 僚机飞行初始形态(flyForm表中僚机ID)
   * @return
   */
   public int getWingmanFlyForm()
   {
     return wingmanFlyForm;
   }

  /**
   * set 僚机飞行初始形态(flyForm表中僚机ID)
   */
   public void setWingmanFlyForm(int wingmanFlyForm)
   {
      this.wingmanFlyForm = wingmanFlyForm;
   }
  /**
   * get 皮肤名字
   * @return
   */
   public int getNameTextId()
   {
     return nameTextId;
   }

  /**
   * set 皮肤名字
   */
   public void setNameTextId(int nameTextId)
   {
      this.nameTextId = nameTextId;
   }
  /**
   * get 道具类型
   * @return
   */
   public int getSuperType()
   {
     return superType;
   }

  /**
   * set 道具类型
   */
   public void setSuperType(int superType)
   {
      this.superType = superType;
   }
  /**
   * get 子类型
   * @return
   */
   public int getSubType()
   {
     return subType;
   }

  /**
   * set 子类型
   */
   public void setSubType(int subType)
   {
      this.subType = subType;
   }
  /**
   * get 小类型
   * @return
   */
   public int getSmallType()
   {
     return smallType;
   }

  /**
   * set 小类型
   */
   public void setSmallType(int smallType)
   {
      this.smallType = smallType;
   }
  /**
   * get 图标
   * @return
   */
   public String getIcon()
   {
     return icon;
   }

  /**
   * set 图标
   */
   public void setIcon(String icon)
   {
      this.icon = icon;
   }
  /**
   * get 所属背包
   * @return
   */
   public int getBagType()
   {
     return bagType;
   }

  /**
   * set 所属背包
   */
   public void setBagType(int bagType)
   {
      this.bagType = bagType;
   }
  /**
   * get 排序索引
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 排序索引
   */
   public void setOrder(int order)
   {
      this.order = order;
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
   * get 总上限
   * @return
   */
   public int getTotalMax()
   {
     return totalMax;
   }

  /**
   * set 总上限
   */
   public void setTotalMax(int totalMax)
   {
      this.totalMax = totalMax;
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
   * get 达总上限转换
   * @return
   */
   public java.util.Map getConvertMax()
   {
     return convertMax;
   }

  /**
   * set 达总上限转换
   */
   public void setConvertMax(java.util.Map convertMax)
   {
      this.convertMax = convertMax;
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
   * get 描述文字ID
   * @return
   */
   public int getDesTextId()
   {
     return desTextId;
   }

  /**
   * set 描述文字ID
   */
   public void setDesTextId(int desTextId)
   {
      this.desTextId = desTextId;
   }
  /**
   * get 角色能量机制
   * @return
   */
   public int getHeroPower()
   {
     return heroPower;
   }

  /**
   * set 角色能量机制
   */
   public void setHeroPower(int heroPower)
   {
      this.heroPower = heroPower;
   }
  /**
   * get 战斗初始形态
   * @return
   */
   public int getBeginForm()
   {
     return beginForm;
   }

  /**
   * set 战斗初始形态
   */
   public void setBeginForm(int beginForm)
   {
      this.beginForm = beginForm;
   }
  /**
   * get 战斗所有形态
   * @return
   */
   public int[] getAllForm()
   {
     return allForm;
   }

  /**
   * set 战斗所有形态
   */
   public void setAllForm(int[] allForm)
   {
      this.allForm = allForm;
   }
  /**
   * get 被动技能配置
   * @return
   */
   public int[] getPassivitySkills()
   {
     return passivitySkills;
   }

  /**
   * set 被动技能配置
   */
   public void setPassivitySkills(int[] passivitySkills)
   {
      this.passivitySkills = passivitySkills;
   }
  /**
   * get 技能类型（1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7切换登场技能,8下落技能,,9起身震飞,10被动）
   * @return
   */
   public int[] getAngelSkills()
   {
     return angelSkills;
   }

  /**
   * set 技能类型（1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7切换登场技能,8下落技能,,9起身震飞,10被动）
   */
   public void setAngelSkills(int[] angelSkills)
   {
      this.angelSkills = angelSkills;
   }
  /**
   * get 价值
   * @return
   */
   public int getCost()
   {
     return cost;
   }

  /**
   * set 价值
   */
   public void setCost(int cost)
   {
      this.cost = cost;
   }
}
