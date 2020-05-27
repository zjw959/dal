/**
 * Auto generated, do not edit it
 *
 * Medal
 */
package data.bean;

public class MedalCfgBean extends BaseGoods
{
  private int id; // id
  private int superType; // 勋章类型（勋章类型固定为12）
  private int star; // 星级
  private int quality; // 品质
  private int[] size; // 图标大小（缩列图大小，展示图大小）
  private int effectivetime; // 有效时间（单位：S）
  private java.util.Map baseAttribute; // 携带属性({属性号：属性值，属性号：属性值})
  private java.util.Map baseskill; // 携带技能({技能ID：属性})
  private java.util.Map accessway; // 获取途径（途径ID，值）[1参与删档测试，2通关指定关卡]
   
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
   * get 勋章类型（勋章类型固定为12）
   * @return
   */
   public int getSuperType()
   {
     return superType;
   }

  /**
   * set 勋章类型（勋章类型固定为12）
   */
   public void setSuperType(int superType)
   {
      this.superType = superType;
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
   * get 图标大小（缩列图大小，展示图大小）
   * @return
   */
   public int[] getSize()
   {
     return size;
   }

  /**
   * set 图标大小（缩列图大小，展示图大小）
   */
   public void setSize(int[] size)
   {
      this.size = size;
   }
  /**
   * get 有效时间（单位：S）
   * @return
   */
   public int getEffectivetime()
   {
     return effectivetime;
   }

  /**
   * set 有效时间（单位：S）
   */
   public void setEffectivetime(int effectivetime)
   {
      this.effectivetime = effectivetime;
   }
  /**
   * get 携带属性({属性号：属性值，属性号：属性值})
   * @return
   */
   public java.util.Map getBaseAttribute()
   {
     return baseAttribute;
   }

  /**
   * set 携带属性({属性号：属性值，属性号：属性值})
   */
   public void setBaseAttribute(java.util.Map baseAttribute)
   {
      this.baseAttribute = baseAttribute;
   }
  /**
   * get 携带技能({技能ID：属性})
   * @return
   */
   public java.util.Map getBaseskill()
   {
     return baseskill;
   }

  /**
   * set 携带技能({技能ID：属性})
   */
   public void setBaseskill(java.util.Map baseskill)
   {
      this.baseskill = baseskill;
   }
  /**
   * get 获取途径（途径ID，值）[1参与删档测试，2通关指定关卡]
   * @return
   */
   public java.util.Map getAccessway()
   {
     return accessway;
   }

  /**
   * set 获取途径（途径ID，值）[1参与删档测试，2通关指定关卡]
   */
   public void setAccessway(java.util.Map accessway)
   {
      this.accessway = accessway;
   }
}
