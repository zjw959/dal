/**
 * Auto generated, do not edit it
 *
 * EquipmentCombination
 */
package data.bean;

public class EquipmentCombinationCfgBean 
{
  private int id; // id
  private int type; // 组合类型
  private int[] needParticle; // 组合需要质点
  private int[] skill; // 组合技能
   
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
   * get 组合类型
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 组合类型
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 组合需要质点
   * @return
   */
   public int[] getNeedParticle()
   {
     return needParticle;
   }

  /**
   * set 组合需要质点
   */
   public void setNeedParticle(int[] needParticle)
   {
      this.needParticle = needParticle;
   }
  /**
   * get 组合技能
   * @return
   */
   public int[] getSkill()
   {
     return skill;
   }

  /**
   * set 组合技能
   */
   public void setSkill(int[] skill)
   {
      this.skill = skill;
   }
}
