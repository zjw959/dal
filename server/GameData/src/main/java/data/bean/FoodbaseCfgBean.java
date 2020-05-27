/**
 * Auto generated, do not edit it
 *
 * Foodbase
 */
package data.bean;

public class FoodbaseCfgBean 
{
  private int id; // ID
  private int datingId; // 功能类型（约会）
  private int effectorder; // 显示优先级（约会）
  private int foodType; // 餐厅类型
  private String name; // 料理名字
  private int presentId; // 展示料理（对应item表中物品ID）
  private java.util.Map ability; // 所需能力
  private java.util.Map materials; // 所需食材
  private int order; // 料理排序
  private java.util.List integral; // 奖励料理（所需积分,物品ID,数量）
  private java.util.Map jump; // 分数对应跳转（约会）
  private int maxintegral; // 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
  private int cooktime; // 制作总时长
  private String[] qte; // qte(类型，出现时间点)
  private int natureTime; // 每秒增长积分
   
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
   * get 功能类型（约会）
   * @return
   */
   public int getDatingId()
   {
     return datingId;
   }

  /**
   * set 功能类型（约会）
   */
   public void setDatingId(int datingId)
   {
      this.datingId = datingId;
   }
  /**
   * get 显示优先级（约会）
   * @return
   */
   public int getEffectorder()
   {
     return effectorder;
   }

  /**
   * set 显示优先级（约会）
   */
   public void setEffectorder(int effectorder)
   {
      this.effectorder = effectorder;
   }
  /**
   * get 餐厅类型
   * @return
   */
   public int getFoodType()
   {
     return foodType;
   }

  /**
   * set 餐厅类型
   */
   public void setFoodType(int foodType)
   {
      this.foodType = foodType;
   }
  /**
   * get 料理名字
   * @return
   */
   public String getName()
   {
     return name;
   }

  /**
   * set 料理名字
   */
   public void setName(String name)
   {
      this.name = name;
   }
  /**
   * get 展示料理（对应item表中物品ID）
   * @return
   */
   public int getPresentId()
   {
     return presentId;
   }

  /**
   * set 展示料理（对应item表中物品ID）
   */
   public void setPresentId(int presentId)
   {
      this.presentId = presentId;
   }
  /**
   * get 所需能力
   * @return
   */
   public java.util.Map getAbility()
   {
     return ability;
   }

  /**
   * set 所需能力
   */
   public void setAbility(java.util.Map ability)
   {
      this.ability = ability;
   }
  /**
   * get 所需食材
   * @return
   */
   public java.util.Map getMaterials()
   {
     return materials;
   }

  /**
   * set 所需食材
   */
   public void setMaterials(java.util.Map materials)
   {
      this.materials = materials;
   }
  /**
   * get 料理排序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 料理排序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 奖励料理（所需积分,物品ID,数量）
   * @return
   */
   public java.util.List getIntegral()
   {
     return integral;
   }

  /**
   * set 奖励料理（所需积分,物品ID,数量）
   */
   public void setIntegral(java.util.List integral)
   {
      this.integral = integral;
   }
  /**
   * get 分数对应跳转（约会）
   * @return
   */
   public java.util.Map getJump()
   {
     return jump;
   }

  /**
   * set 分数对应跳转（约会）
   */
   public void setJump(java.util.Map jump)
   {
      this.jump = jump;
   }
  /**
   * get 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
   * @return
   */
   public int getMaxintegral()
   {
     return maxintegral;
   }

  /**
   * set 最大积分（用于计算制作提前结束，一定要与最大所需积分相等）
   */
   public void setMaxintegral(int maxintegral)
   {
      this.maxintegral = maxintegral;
   }
  /**
   * get 制作总时长
   * @return
   */
   public int getCooktime()
   {
     return cooktime;
   }

  /**
   * set 制作总时长
   */
   public void setCooktime(int cooktime)
   {
      this.cooktime = cooktime;
   }
  /**
   * get qte(类型，出现时间点)
   * @return
   */
   public String[] getQte()
   {
     return qte;
   }

  /**
   * set qte(类型，出现时间点)
   */
   public void setQte(String[] qte)
   {
      this.qte = qte;
   }
  /**
   * get 每秒增长积分
   * @return
   */
   public int getNatureTime()
   {
     return natureTime;
   }

  /**
   * set 每秒增长积分
   */
   public void setNatureTime(int natureTime)
   {
      this.natureTime = natureTime;
   }
}
