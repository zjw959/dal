/**
 * Auto generated, do not edit it
 *
 * Task
 */
package data.bean;

public class TaskCfgBean 
{
  private int id; // id
  private int type; // 任务类型（1主线任务 2日常任务 3成就 4活动任务 5活跃度奖励 6七日签到 7次日奖励 8 十香活动任务）
  private int[] playerLevel; // 日常任务开放等级
  private int resetType; // 重置类型（1.不会重置2.每天重置）
  private int subType; // 内容类型（1.指定副本关卡 2.其他）
  private int acceptCondId; // 接取条件id（关联事件表）
  private java.util.Map acceptParams; // 接取参数
  private int finishCondId; // 完成条件id（关联事件表）
  private java.util.Map finishParams; // 完成参数
  private int progress; // 需要进度
  private java.util.Map reward; // 完成奖励
  private int extendsTaskId; // 继承进度的任务id
  private boolean open; // 是否开启
  private int effectiveTime; // 有效时间(单位为天，-1为永久生效）
   
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
   * get 任务类型（1主线任务 2日常任务 3成就 4活动任务 5活跃度奖励 6七日签到 7次日奖励 8 十香活动任务）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 任务类型（1主线任务 2日常任务 3成就 4活动任务 5活跃度奖励 6七日签到 7次日奖励 8 十香活动任务）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 日常任务开放等级
   * @return
   */
   public int[] getPlayerLevel()
   {
     return playerLevel;
   }

  /**
   * set 日常任务开放等级
   */
   public void setPlayerLevel(int[] playerLevel)
   {
      this.playerLevel = playerLevel;
   }
  /**
   * get 重置类型（1.不会重置2.每天重置）
   * @return
   */
   public int getResetType()
   {
     return resetType;
   }

  /**
   * set 重置类型（1.不会重置2.每天重置）
   */
   public void setResetType(int resetType)
   {
      this.resetType = resetType;
   }
  /**
   * get 内容类型（1.指定副本关卡 2.其他）
   * @return
   */
   public int getSubType()
   {
     return subType;
   }

  /**
   * set 内容类型（1.指定副本关卡 2.其他）
   */
   public void setSubType(int subType)
   {
      this.subType = subType;
   }
  /**
   * get 接取条件id（关联事件表）
   * @return
   */
   public int getAcceptCondId()
   {
     return acceptCondId;
   }

  /**
   * set 接取条件id（关联事件表）
   */
   public void setAcceptCondId(int acceptCondId)
   {
      this.acceptCondId = acceptCondId;
   }
  /**
   * get 接取参数
   * @return
   */
   public java.util.Map getAcceptParams()
   {
     return acceptParams;
   }

  /**
   * set 接取参数
   */
   public void setAcceptParams(java.util.Map acceptParams)
   {
      this.acceptParams = acceptParams;
   }
  /**
   * get 完成条件id（关联事件表）
   * @return
   */
   public int getFinishCondId()
   {
     return finishCondId;
   }

  /**
   * set 完成条件id（关联事件表）
   */
   public void setFinishCondId(int finishCondId)
   {
      this.finishCondId = finishCondId;
   }
  /**
   * get 完成参数
   * @return
   */
   public java.util.Map getFinishParams()
   {
     return finishParams;
   }

  /**
   * set 完成参数
   */
   public void setFinishParams(java.util.Map finishParams)
   {
      this.finishParams = finishParams;
   }
  /**
   * get 需要进度
   * @return
   */
   public int getProgress()
   {
     return progress;
   }

  /**
   * set 需要进度
   */
   public void setProgress(int progress)
   {
      this.progress = progress;
   }
  /**
   * get 完成奖励
   * @return
   */
   public java.util.Map getReward()
   {
     return reward;
   }

  /**
   * set 完成奖励
   */
   public void setReward(java.util.Map reward)
   {
      this.reward = reward;
   }
  /**
   * get 继承进度的任务id
   * @return
   */
   public int getExtendsTaskId()
   {
     return extendsTaskId;
   }

  /**
   * set 继承进度的任务id
   */
   public void setExtendsTaskId(int extendsTaskId)
   {
      this.extendsTaskId = extendsTaskId;
   }
  /**
   * get 是否开启
   * @return
   */
   public boolean getOpen()
   {
     return open;
   }

  /**
   * set 是否开启
   */
   public void setOpen(boolean open)
   {
      this.open = open;
   }
  /**
   * get 有效时间(单位为天，-1为永久生效）
   * @return
   */
   public int getEffectiveTime()
   {
     return effectiveTime;
   }

  /**
   * set 有效时间(单位为天，-1为永久生效）
   */
   public void setEffectiveTime(int effectiveTime)
   {
      this.effectiveTime = effectiveTime;
   }
}
