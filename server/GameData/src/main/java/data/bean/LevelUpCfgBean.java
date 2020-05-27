/**
 * Auto generated, do not edit it
 *
 * LevelUp
 */
package data.bean;

public class LevelUpCfgBean 
{
  private int id; // ID
  private long playerExp; // 经验值
  private int maxEnergy; // 体力上限
  private int recovery; // 升级恢复体力
  private int maxVigour; // 精力上限
  private int recoveryVigour; // 升级恢复精力
  private long heroExp; // 精灵强化经验
   
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
   * get 经验值
   * @return
   */
   public long getPlayerExp()
   {
     return playerExp;
   }

  /**
   * set 经验值
   */
   public void setPlayerExp(long playerExp)
   {
      this.playerExp = playerExp;
   }
  /**
   * get 体力上限
   * @return
   */
   public int getMaxEnergy()
   {
     return maxEnergy;
   }

  /**
   * set 体力上限
   */
   public void setMaxEnergy(int maxEnergy)
   {
      this.maxEnergy = maxEnergy;
   }
  /**
   * get 升级恢复体力
   * @return
   */
   public int getRecovery()
   {
     return recovery;
   }

  /**
   * set 升级恢复体力
   */
   public void setRecovery(int recovery)
   {
      this.recovery = recovery;
   }
  /**
   * get 精力上限
   * @return
   */
   public int getMaxVigour()
   {
     return maxVigour;
   }

  /**
   * set 精力上限
   */
   public void setMaxVigour(int maxVigour)
   {
      this.maxVigour = maxVigour;
   }
  /**
   * get 升级恢复精力
   * @return
   */
   public int getRecoveryVigour()
   {
     return recoveryVigour;
   }

  /**
   * set 升级恢复精力
   */
   public void setRecoveryVigour(int recoveryVigour)
   {
      this.recoveryVigour = recoveryVigour;
   }
  /**
   * get 精灵强化经验
   * @return
   */
   public long getHeroExp()
   {
     return heroExp;
   }

  /**
   * set 精灵强化经验
   */
   public void setHeroExp(long heroExp)
   {
      this.heroExp = heroExp;
   }
}
