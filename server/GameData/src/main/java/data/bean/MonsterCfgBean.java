/**
 * Auto generated, do not edit it
 *
 * Monster
 */
package data.bean;

public class MonsterCfgBean 
{
  private int id; // 怪物id
  private int type; // 敌人类型（1普通怪，2精英怪物，3boss怪，4守护目标，5陷阱）
  private int subType; // 生物类型（0.人类1.生物 2.机械 3.灵体 4.能量）
  private int orgnazationType; // 阵营类型（1-AST，2-机械，3-精灵，4-DEM，5-能量）
  private int corporeity; // 怪物体质（用于受击音效判定，1普通怪，2机械怪，3能量怪）
  private int camp; // 敌我阵营（1英雄阵营，2怪物阵营）
  private int monsterType; // 怪物属性（对应MonsterLevel表确定属性）
  private int[] skills; // 技能
  private int[] passivitySkills; // 被动技能效果
   
  /**
   * get 怪物id
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set 怪物id
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 敌人类型（1普通怪，2精英怪物，3boss怪，4守护目标，5陷阱）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 敌人类型（1普通怪，2精英怪物，3boss怪，4守护目标，5陷阱）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 生物类型（0.人类1.生物 2.机械 3.灵体 4.能量）
   * @return
   */
   public int getSubType()
   {
     return subType;
   }

  /**
   * set 生物类型（0.人类1.生物 2.机械 3.灵体 4.能量）
   */
   public void setSubType(int subType)
   {
      this.subType = subType;
   }
  /**
   * get 阵营类型（1-AST，2-机械，3-精灵，4-DEM，5-能量）
   * @return
   */
   public int getOrgnazationType()
   {
     return orgnazationType;
   }

  /**
   * set 阵营类型（1-AST，2-机械，3-精灵，4-DEM，5-能量）
   */
   public void setOrgnazationType(int orgnazationType)
   {
      this.orgnazationType = orgnazationType;
   }
  /**
   * get 怪物体质（用于受击音效判定，1普通怪，2机械怪，3能量怪）
   * @return
   */
   public int getCorporeity()
   {
     return corporeity;
   }

  /**
   * set 怪物体质（用于受击音效判定，1普通怪，2机械怪，3能量怪）
   */
   public void setCorporeity(int corporeity)
   {
      this.corporeity = corporeity;
   }
  /**
   * get 敌我阵营（1英雄阵营，2怪物阵营）
   * @return
   */
   public int getCamp()
   {
     return camp;
   }

  /**
   * set 敌我阵营（1英雄阵营，2怪物阵营）
   */
   public void setCamp(int camp)
   {
      this.camp = camp;
   }
  /**
   * get 怪物属性（对应MonsterLevel表确定属性）
   * @return
   */
   public int getMonsterType()
   {
     return monsterType;
   }

  /**
   * set 怪物属性（对应MonsterLevel表确定属性）
   */
   public void setMonsterType(int monsterType)
   {
      this.monsterType = monsterType;
   }
  /**
   * get 技能
   * @return
   */
   public int[] getSkills()
   {
     return skills;
   }

  /**
   * set 技能
   */
   public void setSkills(int[] skills)
   {
      this.skills = skills;
   }
  /**
   * get 被动技能效果
   * @return
   */
   public int[] getPassivitySkills()
   {
     return passivitySkills;
   }

  /**
   * set 被动技能效果
   */
   public void setPassivitySkills(int[] passivitySkills)
   {
      this.passivitySkills = passivitySkills;
   }
}
