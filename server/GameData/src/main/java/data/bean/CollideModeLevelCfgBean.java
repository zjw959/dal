/**
 * Auto generated, do not edit it
 *
 * CollideModeLevel
 */
package data.bean;

public class CollideModeLevelCfgBean 
{
  private int id; // 排序
  private int fixedMonster; // 怪物ID(填写monster.csv表中怪物ID)
  private int levelID; // 关卡ID(填写dungeonLevel.csv表中关卡ID)
  private int level; // 怪物等级
  private int timing; // 怪物出现时间(从关卡第0秒开始计时，直到关卡结束，单位毫秒)
  private java.util.Map createCoordinate; // 怪物出现坐标(以1屏尺寸为基础，坐标可出屏，场景左下角为(0,0))
  private java.util.Map itemAward; // 怪物掉落奖励
   
  /**
   * get 排序
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set 排序
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 怪物ID(填写monster.csv表中怪物ID)
   * @return
   */
   public int getFixedMonster()
   {
     return fixedMonster;
   }

  /**
   * set 怪物ID(填写monster.csv表中怪物ID)
   */
   public void setFixedMonster(int fixedMonster)
   {
      this.fixedMonster = fixedMonster;
   }
  /**
   * get 关卡ID(填写dungeonLevel.csv表中关卡ID)
   * @return
   */
   public int getLevelID()
   {
     return levelID;
   }

  /**
   * set 关卡ID(填写dungeonLevel.csv表中关卡ID)
   */
   public void setLevelID(int levelID)
   {
      this.levelID = levelID;
   }
  /**
   * get 怪物等级
   * @return
   */
   public int getLevel()
   {
     return level;
   }

  /**
   * set 怪物等级
   */
   public void setLevel(int level)
   {
      this.level = level;
   }
  /**
   * get 怪物出现时间(从关卡第0秒开始计时，直到关卡结束，单位毫秒)
   * @return
   */
   public int getTiming()
   {
     return timing;
   }

  /**
   * set 怪物出现时间(从关卡第0秒开始计时，直到关卡结束，单位毫秒)
   */
   public void setTiming(int timing)
   {
      this.timing = timing;
   }
  /**
   * get 怪物出现坐标(以1屏尺寸为基础，坐标可出屏，场景左下角为(0,0))
   * @return
   */
   public java.util.Map getCreateCoordinate()
   {
     return createCoordinate;
   }

  /**
   * set 怪物出现坐标(以1屏尺寸为基础，坐标可出屏，场景左下角为(0,0))
   */
   public void setCreateCoordinate(java.util.Map createCoordinate)
   {
      this.createCoordinate = createCoordinate;
   }
  /**
   * get 怪物掉落奖励
   * @return
   */
   public java.util.Map getItemAward()
   {
     return itemAward;
   }

  /**
   * set 怪物掉落奖励
   */
   public void setItemAward(java.util.Map itemAward)
   {
      this.itemAward = itemAward;
   }
}
