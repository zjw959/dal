/**
 * Auto generated, do not edit it
 *
 * HeroProgress
 */
package data.bean;

public class HeroProgressCfgBean 
{
  private int id; // ID
  private java.util.Map consume; // 进阶消耗
  private java.util.Map baseAttr; // 基础属性
  private java.util.Map upAttr; // 成长属性
  private int[] openAngelSkillTree; // 解锁技能
   
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
   * get 进阶消耗
   * @return
   */
   public java.util.Map getConsume()
   {
     return consume;
   }

  /**
   * set 进阶消耗
   */
   public void setConsume(java.util.Map consume)
   {
      this.consume = consume;
   }
  /**
   * get 基础属性
   * @return
   */
   public java.util.Map getBaseAttr()
   {
     return baseAttr;
   }

  /**
   * set 基础属性
   */
   public void setBaseAttr(java.util.Map baseAttr)
   {
      this.baseAttr = baseAttr;
   }
  /**
   * get 成长属性
   * @return
   */
   public java.util.Map getUpAttr()
   {
     return upAttr;
   }

  /**
   * set 成长属性
   */
   public void setUpAttr(java.util.Map upAttr)
   {
      this.upAttr = upAttr;
   }
  /**
   * get 解锁技能
   * @return
   */
   public int[] getOpenAngelSkillTree()
   {
     return openAngelSkillTree;
   }

  /**
   * set 解锁技能
   */
   public void setOpenAngelSkillTree(int[] openAngelSkillTree)
   {
      this.openAngelSkillTree = openAngelSkillTree;
   }
}
