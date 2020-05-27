/**
 * Auto generated, do not edit it
 *
 * Evolution
 */
package data.bean;

public class EvolutionCfgBean 
{
  private int id; // id
  private int heroId; // 对应精灵id
  private int cell; // 格子id
  private int partition; // 稀有度分区
  private int[] neighborId; // 相邻格子的id
  private java.util.List unlockCondition; // 解锁条件[格子id,格子id2]
  private java.util.Map attribute; // 加成效果{属性id,数值}
  private java.util.Map material; // 突破需要道具
  private int combatPower; // 战斗力加成
   
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
   * get 对应精灵id
   * @return
   */
   public int getHeroId()
   {
     return heroId;
   }

  /**
   * set 对应精灵id
   */
   public void setHeroId(int heroId)
   {
      this.heroId = heroId;
   }
  /**
   * get 格子id
   * @return
   */
   public int getCell()
   {
     return cell;
   }

  /**
   * set 格子id
   */
   public void setCell(int cell)
   {
      this.cell = cell;
   }
  /**
   * get 稀有度分区
   * @return
   */
   public int getPartition()
   {
     return partition;
   }

  /**
   * set 稀有度分区
   */
   public void setPartition(int partition)
   {
      this.partition = partition;
   }
  /**
   * get 相邻格子的id
   * @return
   */
   public int[] getNeighborId()
   {
     return neighborId;
   }

  /**
   * set 相邻格子的id
   */
   public void setNeighborId(int[] neighborId)
   {
      this.neighborId = neighborId;
   }
  /**
   * get 解锁条件[格子id,格子id2]
   * @return
   */
   public java.util.List getUnlockCondition()
   {
     return unlockCondition;
   }

  /**
   * set 解锁条件[格子id,格子id2]
   */
   public void setUnlockCondition(java.util.List unlockCondition)
   {
      this.unlockCondition = unlockCondition;
   }
  /**
   * get 加成效果{属性id,数值}
   * @return
   */
   public java.util.Map getAttribute()
   {
     return attribute;
   }

  /**
   * set 加成效果{属性id,数值}
   */
   public void setAttribute(java.util.Map attribute)
   {
      this.attribute = attribute;
   }
  /**
   * get 突破需要道具
   * @return
   */
   public java.util.Map getMaterial()
   {
     return material;
   }

  /**
   * set 突破需要道具
   */
   public void setMaterial(java.util.Map material)
   {
      this.material = material;
   }
  /**
   * get 战斗力加成
   * @return
   */
   public int getCombatPower()
   {
     return combatPower;
   }

  /**
   * set 战斗力加成
   */
   public void setCombatPower(int combatPower)
   {
      this.combatPower = combatPower;
   }
}
