/**
 * Auto generated, do not edit it
 *
 * Hero
 */
package data.bean;

public class HeroCfgBean extends BaseGoods
{
  private int id; // ID
  private int role; // 所属精灵(看伴娘数据role表)
  private int superType; // 物品类型
  private int bagType; // 所属背包
  private java.util.Map compose; // 对应碎片合成
  private java.util.Map convert; // 重复英雄回收
  private int rarity; // 卡牌初始品质
  private int site; // 角色列表显示
  private int order; // 排位顺序
  private int defaultSkin; // 默认皮肤
  private int[] optionalSkin; // 可选皮肤组
  private int attribute; // 突破属性项（ID*100+突破等级=growup表ID）
  private int attribute2; // 品质属性(ID*100+hero品质=HeroProgress表ID)
  private int[] expitem; // 经验道具
  private int bornEffect; // 是否播放出场效果
  private int money; // 价值
  private int weaponType; // 武器类型（1天使2恶魔3装置）
  private java.util.List skillPoint; // 星级技能点（1-5星）
  private java.util.List angelWakeCons; // 天使觉醒消耗（1-5星）
  private int heroLimitType; // 精灵技能标识（联机副本使用）
  private int angelID; // 天使技能索引ID
   
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
   * get 所属精灵(看伴娘数据role表)
   * @return
   */
   public int getRole()
   {
     return role;
   }

  /**
   * set 所属精灵(看伴娘数据role表)
   */
   public void setRole(int role)
   {
      this.role = role;
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
   * get 对应碎片合成
   * @return
   */
   public java.util.Map getCompose()
   {
     return compose;
   }

  /**
   * set 对应碎片合成
   */
   public void setCompose(java.util.Map compose)
   {
      this.compose = compose;
   }
  /**
   * get 重复英雄回收
   * @return
   */
   public java.util.Map getConvert()
   {
     return convert;
   }

  /**
   * set 重复英雄回收
   */
   public void setConvert(java.util.Map convert)
   {
      this.convert = convert;
   }
  /**
   * get 卡牌初始品质
   * @return
   */
   public int getRarity()
   {
     return rarity;
   }

  /**
   * set 卡牌初始品质
   */
   public void setRarity(int rarity)
   {
      this.rarity = rarity;
   }
  /**
   * get 角色列表显示
   * @return
   */
   public int getSite()
   {
     return site;
   }

  /**
   * set 角色列表显示
   */
   public void setSite(int site)
   {
      this.site = site;
   }
  /**
   * get 排位顺序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 排位顺序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 默认皮肤
   * @return
   */
   public int getDefaultSkin()
   {
     return defaultSkin;
   }

  /**
   * set 默认皮肤
   */
   public void setDefaultSkin(int defaultSkin)
   {
      this.defaultSkin = defaultSkin;
   }
  /**
   * get 可选皮肤组
   * @return
   */
   public int[] getOptionalSkin()
   {
     return optionalSkin;
   }

  /**
   * set 可选皮肤组
   */
   public void setOptionalSkin(int[] optionalSkin)
   {
      this.optionalSkin = optionalSkin;
   }
  /**
   * get 突破属性项（ID*100+突破等级=growup表ID）
   * @return
   */
   public int getAttribute()
   {
     return attribute;
   }

  /**
   * set 突破属性项（ID*100+突破等级=growup表ID）
   */
   public void setAttribute(int attribute)
   {
      this.attribute = attribute;
   }
  /**
   * get 品质属性(ID*100+hero品质=HeroProgress表ID)
   * @return
   */
   public int getAttribute2()
   {
     return attribute2;
   }

  /**
   * set 品质属性(ID*100+hero品质=HeroProgress表ID)
   */
   public void setAttribute2(int attribute2)
   {
      this.attribute2 = attribute2;
   }
  /**
   * get 经验道具
   * @return
   */
   public int[] getExpitem()
   {
     return expitem;
   }

  /**
   * set 经验道具
   */
   public void setExpitem(int[] expitem)
   {
      this.expitem = expitem;
   }
  /**
   * get 是否播放出场效果
   * @return
   */
   public int getBornEffect()
   {
     return bornEffect;
   }

  /**
   * set 是否播放出场效果
   */
   public void setBornEffect(int bornEffect)
   {
      this.bornEffect = bornEffect;
   }
  /**
   * get 价值
   * @return
   */
   public int getMoney()
   {
     return money;
   }

  /**
   * set 价值
   */
   public void setMoney(int money)
   {
      this.money = money;
   }
  /**
   * get 武器类型（1天使2恶魔3装置）
   * @return
   */
   public int getWeaponType()
   {
     return weaponType;
   }

  /**
   * set 武器类型（1天使2恶魔3装置）
   */
   public void setWeaponType(int weaponType)
   {
      this.weaponType = weaponType;
   }
  /**
   * get 星级技能点（1-5星）
   * @return
   */
   public java.util.List getSkillPoint()
   {
     return skillPoint;
   }

  /**
   * set 星级技能点（1-5星）
   */
   public void setSkillPoint(java.util.List skillPoint)
   {
      this.skillPoint = skillPoint;
   }
  /**
   * get 天使觉醒消耗（1-5星）
   * @return
   */
   public java.util.List getAngelWakeCons()
   {
     return angelWakeCons;
   }

  /**
   * set 天使觉醒消耗（1-5星）
   */
   public void setAngelWakeCons(java.util.List angelWakeCons)
   {
      this.angelWakeCons = angelWakeCons;
   }
  /**
   * get 精灵技能标识（联机副本使用）
   * @return
   */
   public int getHeroLimitType()
   {
     return heroLimitType;
   }

  /**
   * set 精灵技能标识（联机副本使用）
   */
   public void setHeroLimitType(int heroLimitType)
   {
      this.heroLimitType = heroLimitType;
   }
  /**
   * get 天使技能索引ID
   * @return
   */
   public int getAngelID()
   {
     return angelID;
   }

  /**
   * set 天使技能索引ID
   */
   public void setAngelID(int angelID)
   {
      this.angelID = angelID;
   }
}
