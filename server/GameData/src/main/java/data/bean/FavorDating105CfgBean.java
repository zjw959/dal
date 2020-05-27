/**
 * Auto generated, do not edit it
 *
 * FavorDating105
 */
package data.bean;

public class FavorDating105CfgBean extends BaseFavorDating
{
  private int id; // 
  private int scriptId; // 剧本号
  private int datingType; // 约会类型(1主线2日常4特殊5外传）
  private java.util.Map condition; // 条件
  private int weighting; // 分支节点权重
  private int chance; // 触发几率
  private int[] jump; // 跳转（选项）
  private int[] addSign; // 添加标记
  private int[] delSign; // 删除标记
  private int eventType; // 事件类型
  private int score; // 评分
  private java.util.Map reward; // 奖励内容
  private java.util.Map repeatReward; // 重复完成奖励内容
  private int completionTimes; // 可完成次数
  private int endType; // 结局
  private java.util.Map nodeCost; // 节点消耗
  private java.util.Map nodeReward; // 节点奖励
  private int game; // 小游戏
  private boolean outsideType; // 外传结局
  private int outsideUnlock; // 解锁结局
  private java.util.Map outsideReward; // 事件结算内容
  private java.util.Map outsideEnd; // 外传结算内容（初次达成）
  private java.util.Map outsideRepeat; // 外传结算内容（多次达成）
  private java.util.Map extraBonus; // 额外奖励
   
  /**
   * get 
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set 
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 剧本号
   * @return
   */
   public int getScriptId()
   {
     return scriptId;
   }

  /**
   * set 剧本号
   */
   public void setScriptId(int scriptId)
   {
      this.scriptId = scriptId;
   }
  /**
   * get 约会类型(1主线2日常4特殊5外传）
   * @return
   */
   public int getDatingType()
   {
     return datingType;
   }

  /**
   * set 约会类型(1主线2日常4特殊5外传）
   */
   public void setDatingType(int datingType)
   {
      this.datingType = datingType;
   }
  /**
   * get 条件
   * @return
   */
   public java.util.Map getCondition()
   {
     return condition;
   }

  /**
   * set 条件
   */
   public void setCondition(java.util.Map condition)
   {
      this.condition = condition;
   }
  /**
   * get 分支节点权重
   * @return
   */
   public int getWeighting()
   {
     return weighting;
   }

  /**
   * set 分支节点权重
   */
   public void setWeighting(int weighting)
   {
      this.weighting = weighting;
   }
  /**
   * get 触发几率
   * @return
   */
   public int getChance()
   {
     return chance;
   }

  /**
   * set 触发几率
   */
   public void setChance(int chance)
   {
      this.chance = chance;
   }
  /**
   * get 跳转（选项）
   * @return
   */
   public int[] getJump()
   {
     return jump;
   }

  /**
   * set 跳转（选项）
   */
   public void setJump(int[] jump)
   {
      this.jump = jump;
   }
  /**
   * get 添加标记
   * @return
   */
   public int[] getAddSign()
   {
     return addSign;
   }

  /**
   * set 添加标记
   */
   public void setAddSign(int[] addSign)
   {
      this.addSign = addSign;
   }
  /**
   * get 删除标记
   * @return
   */
   public int[] getDelSign()
   {
     return delSign;
   }

  /**
   * set 删除标记
   */
   public void setDelSign(int[] delSign)
   {
      this.delSign = delSign;
   }
  /**
   * get 事件类型
   * @return
   */
   public int getEventType()
   {
     return eventType;
   }

  /**
   * set 事件类型
   */
   public void setEventType(int eventType)
   {
      this.eventType = eventType;
   }
  /**
   * get 评分
   * @return
   */
   public int getScore()
   {
     return score;
   }

  /**
   * set 评分
   */
   public void setScore(int score)
   {
      this.score = score;
   }
  /**
   * get 奖励内容
   * @return
   */
   public java.util.Map getReward()
   {
     return reward;
   }

  /**
   * set 奖励内容
   */
   public void setReward(java.util.Map reward)
   {
      this.reward = reward;
   }
  /**
   * get 重复完成奖励内容
   * @return
   */
   public java.util.Map getRepeatReward()
   {
     return repeatReward;
   }

  /**
   * set 重复完成奖励内容
   */
   public void setRepeatReward(java.util.Map repeatReward)
   {
      this.repeatReward = repeatReward;
   }
  /**
   * get 可完成次数
   * @return
   */
   public int getCompletionTimes()
   {
     return completionTimes;
   }

  /**
   * set 可完成次数
   */
   public void setCompletionTimes(int completionTimes)
   {
      this.completionTimes = completionTimes;
   }
  /**
   * get 结局
   * @return
   */
   public int getEndType()
   {
     return endType;
   }

  /**
   * set 结局
   */
   public void setEndType(int endType)
   {
      this.endType = endType;
   }
  /**
   * get 节点消耗
   * @return
   */
   public java.util.Map getNodeCost()
   {
     return nodeCost;
   }

  /**
   * set 节点消耗
   */
   public void setNodeCost(java.util.Map nodeCost)
   {
      this.nodeCost = nodeCost;
   }
  /**
   * get 节点奖励
   * @return
   */
   public java.util.Map getNodeReward()
   {
     return nodeReward;
   }

  /**
   * set 节点奖励
   */
   public void setNodeReward(java.util.Map nodeReward)
   {
      this.nodeReward = nodeReward;
   }
  /**
   * get 小游戏
   * @return
   */
   public int getGame()
   {
     return game;
   }

  /**
   * set 小游戏
   */
   public void setGame(int game)
   {
      this.game = game;
   }
  /**
   * get 外传结局
   * @return
   */
   public boolean getOutsideType()
   {
     return outsideType;
   }

  /**
   * set 外传结局
   */
   public void setOutsideType(boolean outsideType)
   {
      this.outsideType = outsideType;
   }
  /**
   * get 解锁结局
   * @return
   */
   public int getOutsideUnlock()
   {
     return outsideUnlock;
   }

  /**
   * set 解锁结局
   */
   public void setOutsideUnlock(int outsideUnlock)
   {
      this.outsideUnlock = outsideUnlock;
   }
  /**
   * get 事件结算内容
   * @return
   */
   public java.util.Map getOutsideReward()
   {
     return outsideReward;
   }

  /**
   * set 事件结算内容
   */
   public void setOutsideReward(java.util.Map outsideReward)
   {
      this.outsideReward = outsideReward;
   }
  /**
   * get 外传结算内容（初次达成）
   * @return
   */
   public java.util.Map getOutsideEnd()
   {
     return outsideEnd;
   }

  /**
   * set 外传结算内容（初次达成）
   */
   public void setOutsideEnd(java.util.Map outsideEnd)
   {
      this.outsideEnd = outsideEnd;
   }
  /**
   * get 外传结算内容（多次达成）
   * @return
   */
   public java.util.Map getOutsideRepeat()
   {
     return outsideRepeat;
   }

  /**
   * set 外传结算内容（多次达成）
   */
   public void setOutsideRepeat(java.util.Map outsideRepeat)
   {
      this.outsideRepeat = outsideRepeat;
   }
  /**
   * get 额外奖励
   * @return
   */
   public java.util.Map getExtraBonus()
   {
     return extraBonus;
   }

  /**
   * set 额外奖励
   */
   public void setExtraBonus(java.util.Map extraBonus)
   {
      this.extraBonus = extraBonus;
   }
}
