/**
 * Auto generated, do not edit it
 *
 * Dating101
 */
package data.bean;

public class Dating101CfgBean extends BaseDating
{
  private int id; // 
  private int scriptId; // 剧本号
  private int datingType; // 约会类型(1主线2日常4特殊）
  private java.util.Map condition; // 条件
  private int weighting; // 分支节点权重
  private int chance; // 触发几率
  private int[] jump; // 跳转（选项）
  private int eventType; // 事件类型
  private int score; // 评分
  private java.util.Map reward; // 奖励内容
  private java.util.Map repeatReward; // 重复完成奖励内容
  private int completionTimes; // 可完成次数
  private int endType; // 结局
  private java.util.Map nodeCost; // 节点消耗
  private java.util.Map nodeReward; // 节点奖励
  private int game; // 小游戏
  private java.util.Map gameRules; // 规则
   
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
   * get 约会类型(1主线2日常4特殊）
   * @return
   */
   public int getDatingType()
   {
     return datingType;
   }

  /**
   * set 约会类型(1主线2日常4特殊）
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
   * get 规则
   * @return
   */
   public java.util.Map getGameRules()
   {
     return gameRules;
   }

  /**
   * set 规则
   */
   public void setGameRules(java.util.Map gameRules)
   {
      this.gameRules = gameRules;
   }
}
