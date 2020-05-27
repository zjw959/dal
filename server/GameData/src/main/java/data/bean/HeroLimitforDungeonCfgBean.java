/**
 * Auto generated, do not edit it
 *
 * HeroLimitforDungeon
 */
package data.bean;

public class HeroLimitforDungeonCfgBean 
{
  private int id; // ID
  private int heroID; // 精灵ID(读取Hero表中ID)
  private int skinID; // 灵装(读取heroskin表中ID)
  private int level; // 精灵等级(与突破等级对应，=10+突破等级*5)
  private int rarity; // 进阶品质(1-B,2-A,3-S,4-SS,5-SSS)
  private int breakthrough; // 突破等级(0~10)
  private java.util.List sephiroth; // 携带质点(质点ID+质点Lv，Cost=10+(精灵等级-1)*1)
  private int[] angelUp; // 天使/装置强化情况(填angerskillFunction表的ID)
  private int powerValue; // 精灵战力(通过公式计算后直接填数值)
  private java.util.Map dateBegin; // 试用开始时间
  private java.util.Map dateEnd; // 试用结束时间
   
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
   * get 精灵ID(读取Hero表中ID)
   * @return
   */
   public int getHeroID()
   {
     return heroID;
   }

  /**
   * set 精灵ID(读取Hero表中ID)
   */
   public void setHeroID(int heroID)
   {
      this.heroID = heroID;
   }
  /**
   * get 灵装(读取heroskin表中ID)
   * @return
   */
   public int getSkinID()
   {
     return skinID;
   }

  /**
   * set 灵装(读取heroskin表中ID)
   */
   public void setSkinID(int skinID)
   {
      this.skinID = skinID;
   }
  /**
   * get 精灵等级(与突破等级对应，=10+突破等级*5)
   * @return
   */
   public int getLevel()
   {
     return level;
   }

  /**
   * set 精灵等级(与突破等级对应，=10+突破等级*5)
   */
   public void setLevel(int level)
   {
      this.level = level;
   }
  /**
   * get 进阶品质(1-B,2-A,3-S,4-SS,5-SSS)
   * @return
   */
   public int getRarity()
   {
     return rarity;
   }

  /**
   * set 进阶品质(1-B,2-A,3-S,4-SS,5-SSS)
   */
   public void setRarity(int rarity)
   {
      this.rarity = rarity;
   }
  /**
   * get 突破等级(0~10)
   * @return
   */
   public int getBreakthrough()
   {
     return breakthrough;
   }

  /**
   * set 突破等级(0~10)
   */
   public void setBreakthrough(int breakthrough)
   {
      this.breakthrough = breakthrough;
   }
  /**
   * get 携带质点(质点ID+质点Lv，Cost=10+(精灵等级-1)*1)
   * @return
   */
   public java.util.List getSephiroth()
   {
     return sephiroth;
   }

  /**
   * set 携带质点(质点ID+质点Lv，Cost=10+(精灵等级-1)*1)
   */
   public void setSephiroth(java.util.List sephiroth)
   {
      this.sephiroth = sephiroth;
   }
  /**
   * get 天使/装置强化情况(填angerskillFunction表的ID)
   * @return
   */
   public int[] getAngelUp()
   {
     return angelUp;
   }

  /**
   * set 天使/装置强化情况(填angerskillFunction表的ID)
   */
   public void setAngelUp(int[] angelUp)
   {
      this.angelUp = angelUp;
   }
  /**
   * get 精灵战力(通过公式计算后直接填数值)
   * @return
   */
   public int getPowerValue()
   {
     return powerValue;
   }

  /**
   * set 精灵战力(通过公式计算后直接填数值)
   */
   public void setPowerValue(int powerValue)
   {
      this.powerValue = powerValue;
   }
  /**
   * get 试用开始时间
   * @return
   */
   public java.util.Map getDateBegin()
   {
     return dateBegin;
   }

  /**
   * set 试用开始时间
   */
   public void setDateBegin(java.util.Map dateBegin)
   {
      this.dateBegin = dateBegin;
   }
  /**
   * get 试用结束时间
   * @return
   */
   public java.util.Map getDateEnd()
   {
     return dateEnd;
   }

  /**
   * set 试用结束时间
   */
   public void setDateEnd(java.util.Map dateEnd)
   {
      this.dateEnd = dateEnd;
   }
}
