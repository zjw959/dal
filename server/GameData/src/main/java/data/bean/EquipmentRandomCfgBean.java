/**
 * Auto generated, do not edit it
 *
 * EquipmentRandom
 */
package data.bean;

public class EquipmentRandomCfgBean 
{
  private int id; // id
  private int superType; // 属性大类
  private int level; // 属性等级（颜色,星级等级决定上限，然后根据权重随机）
  private int weight; // 权重
  private int attrType; // 属性类型
  private int[] attribute; // 属性(最低属性值,最高属性值)
   
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
   * get 属性大类
   * @return
   */
   public int getSuperType()
   {
     return superType;
   }

  /**
   * set 属性大类
   */
   public void setSuperType(int superType)
   {
      this.superType = superType;
   }
  /**
   * get 属性等级（颜色,星级等级决定上限，然后根据权重随机）
   * @return
   */
   public int getLevel()
   {
     return level;
   }

  /**
   * set 属性等级（颜色,星级等级决定上限，然后根据权重随机）
   */
   public void setLevel(int level)
   {
      this.level = level;
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
   * get 属性类型
   * @return
   */
   public int getAttrType()
   {
     return attrType;
   }

  /**
   * set 属性类型
   */
   public void setAttrType(int attrType)
   {
      this.attrType = attrType;
   }
  /**
   * get 属性(最低属性值,最高属性值)
   * @return
   */
   public int[] getAttribute()
   {
     return attribute;
   }

  /**
   * set 属性(最低属性值,最高属性值)
   */
   public void setAttribute(int[] attribute)
   {
      this.attribute = attribute;
   }
}
