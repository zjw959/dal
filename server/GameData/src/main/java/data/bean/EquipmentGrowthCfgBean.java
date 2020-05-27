/**
 * Auto generated, do not edit it
 *
 * EquipmentGrowth
 */
package data.bean;

public class EquipmentGrowthCfgBean 
{
  private int id; // id
  private int star; // 星级
  private int[] needExp; // 所需经验（非累计）
   
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
   * get 所需经验（非累计）
   * @return
   */
   public int[] getNeedExp()
   {
     return needExp;
   }

  /**
   * set 所需经验（非累计）
   */
   public void setNeedExp(int[] needExp)
   {
      this.needExp = needExp;
   }
}
