/**
 * Auto generated, do not edit it
 *
 * AngelSkillTree
 */
package data.bean;

public class AngelSkillTreeCfgBean 
{
  private int id; // id
  private int heroId; // 所属英雄
  private int[] skillId; // 技能ID(不同皮肤技能用数组形式)
  private int skillType; // 技能类型(1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7,出场技能,8.下落技能,9起身震飞,0被动)
  private int lvl; // 效果等级
  private int needSkillPiont; // 消耗天赋点
  private java.util.List frontCondition; // 前置效果要求
  private java.util.List postPositionSkill; // 后置技能
  private int needHeroLvl; // 英雄等级要求
  private int needAngelLvl; // 天使星级要求
  private int[] affixeID; // 附加效果
  private int[] attachBuff; // 附带buff
  private int position; // 服务器位置（警告:不可修改）
   
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
   * get 所属英雄
   * @return
   */
   public int getHeroId()
   {
     return heroId;
   }

  /**
   * set 所属英雄
   */
   public void setHeroId(int heroId)
   {
      this.heroId = heroId;
   }
  /**
   * get 技能ID(不同皮肤技能用数组形式)
   * @return
   */
   public int[] getSkillId()
   {
     return skillId;
   }

  /**
   * set 技能ID(不同皮肤技能用数组形式)
   */
   public void setSkillId(int[] skillId)
   {
      this.skillId = skillId;
   }
  /**
   * get 技能类型(1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7,出场技能,8.下落技能,9起身震飞,0被动)
   * @return
   */
   public int getSkillType()
   {
     return skillType;
   }

  /**
   * set 技能类型(1普攻,2技能一,3小技能二,4必杀,5闪避技能,6觉醒技能,7,出场技能,8.下落技能,9起身震飞,0被动)
   */
   public void setSkillType(int skillType)
   {
      this.skillType = skillType;
   }
  /**
   * get 效果等级
   * @return
   */
   public int getLvl()
   {
     return lvl;
   }

  /**
   * set 效果等级
   */
   public void setLvl(int lvl)
   {
      this.lvl = lvl;
   }
  /**
   * get 消耗天赋点
   * @return
   */
   public int getNeedSkillPiont()
   {
     return needSkillPiont;
   }

  /**
   * set 消耗天赋点
   */
   public void setNeedSkillPiont(int needSkillPiont)
   {
      this.needSkillPiont = needSkillPiont;
   }
  /**
   * get 前置效果要求
   * @return
   */
   public java.util.List getFrontCondition()
   {
     return frontCondition;
   }

  /**
   * set 前置效果要求
   */
   public void setFrontCondition(java.util.List frontCondition)
   {
      this.frontCondition = frontCondition;
   }
  /**
   * get 后置技能
   * @return
   */
   public java.util.List getPostPositionSkill()
   {
     return postPositionSkill;
   }

  /**
   * set 后置技能
   */
   public void setPostPositionSkill(java.util.List postPositionSkill)
   {
      this.postPositionSkill = postPositionSkill;
   }
  /**
   * get 英雄等级要求
   * @return
   */
   public int getNeedHeroLvl()
   {
     return needHeroLvl;
   }

  /**
   * set 英雄等级要求
   */
   public void setNeedHeroLvl(int needHeroLvl)
   {
      this.needHeroLvl = needHeroLvl;
   }
  /**
   * get 天使星级要求
   * @return
   */
   public int getNeedAngelLvl()
   {
     return needAngelLvl;
   }

  /**
   * set 天使星级要求
   */
   public void setNeedAngelLvl(int needAngelLvl)
   {
      this.needAngelLvl = needAngelLvl;
   }
  /**
   * get 附加效果
   * @return
   */
   public int[] getAffixeID()
   {
     return affixeID;
   }

  /**
   * set 附加效果
   */
   public void setAffixeID(int[] affixeID)
   {
      this.affixeID = affixeID;
   }
  /**
   * get 附带buff
   * @return
   */
   public int[] getAttachBuff()
   {
     return attachBuff;
   }

  /**
   * set 附带buff
   */
   public void setAttachBuff(int[] attachBuff)
   {
      this.attachBuff = attachBuff;
   }
  /**
   * get 服务器位置（警告:不可修改）
   * @return
   */
   public int getPosition()
   {
     return position;
   }

  /**
   * set 服务器位置（警告:不可修改）
   */
   public void setPosition(int position)
   {
      this.position = position;
   }
}
