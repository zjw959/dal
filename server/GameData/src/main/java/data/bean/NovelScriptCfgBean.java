/**
 * Auto generated, do not edit it
 *
 * NovelScript
 */
package data.bean;

public class NovelScriptCfgBean 
{
  private int id; // id
  private int stepId; // 阶段
  private boolean use; // 是否使用中
  private int isGuide; // 是否引导
  private int stepJump; // 阶段跳转
  private int eventType; // 事件类型（0地点1角色2信息3功能）
  private int bindType; // 绑定类型（0地点1角色）
  private int bindBuild; // 地点
  private String bindBuildDes; // 地点（策划看）
  private int bindRole; // 角色
  private String bindRoleDes; // 角色（策划看）
  private int action; // 角色动作
  private int scriptId; // 剧本
  private int startId; // 开始节点id
  private int message; // 信息
  private int game; // 调用功能
  private java.util.Map gameRule; // 完成规则
  private boolean hide; // 是否隐藏
  private java.util.Map hideCondition1; // 隐藏显示条件（拥有）
  private java.util.Map hideCondition2; // 隐藏显示条件（不拥有）
  private int costTime; // 消耗时间
  private String limitTime; // 限制时间
  private String addTime; // 强制推动时间
  private boolean delete; // 完成删除
   
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
   * get 阶段
   * @return
   */
   public int getStepId()
   {
     return stepId;
   }

  /**
   * set 阶段
   */
   public void setStepId(int stepId)
   {
      this.stepId = stepId;
   }
  /**
   * get 是否使用中
   * @return
   */
   public boolean getUse()
   {
     return use;
   }

  /**
   * set 是否使用中
   */
   public void setUse(boolean use)
   {
      this.use = use;
   }
  /**
   * get 是否引导
   * @return
   */
   public int getIsGuide()
   {
     return isGuide;
   }

  /**
   * set 是否引导
   */
   public void setIsGuide(int isGuide)
   {
      this.isGuide = isGuide;
   }
  /**
   * get 阶段跳转
   * @return
   */
   public int getStepJump()
   {
     return stepJump;
   }

  /**
   * set 阶段跳转
   */
   public void setStepJump(int stepJump)
   {
      this.stepJump = stepJump;
   }
  /**
   * get 事件类型（0地点1角色2信息3功能）
   * @return
   */
   public int getEventType()
   {
     return eventType;
   }

  /**
   * set 事件类型（0地点1角色2信息3功能）
   */
   public void setEventType(int eventType)
   {
      this.eventType = eventType;
   }
  /**
   * get 绑定类型（0地点1角色）
   * @return
   */
   public int getBindType()
   {
     return bindType;
   }

  /**
   * set 绑定类型（0地点1角色）
   */
   public void setBindType(int bindType)
   {
      this.bindType = bindType;
   }
  /**
   * get 地点
   * @return
   */
   public int getBindBuild()
   {
     return bindBuild;
   }

  /**
   * set 地点
   */
   public void setBindBuild(int bindBuild)
   {
      this.bindBuild = bindBuild;
   }
  /**
   * get 地点（策划看）
   * @return
   */
   public String getBindBuildDes()
   {
     return bindBuildDes;
   }

  /**
   * set 地点（策划看）
   */
   public void setBindBuildDes(String bindBuildDes)
   {
      this.bindBuildDes = bindBuildDes;
   }
  /**
   * get 角色
   * @return
   */
   public int getBindRole()
   {
     return bindRole;
   }

  /**
   * set 角色
   */
   public void setBindRole(int bindRole)
   {
      this.bindRole = bindRole;
   }
  /**
   * get 角色（策划看）
   * @return
   */
   public String getBindRoleDes()
   {
     return bindRoleDes;
   }

  /**
   * set 角色（策划看）
   */
   public void setBindRoleDes(String bindRoleDes)
   {
      this.bindRoleDes = bindRoleDes;
   }
  /**
   * get 角色动作
   * @return
   */
   public int getAction()
   {
     return action;
   }

  /**
   * set 角色动作
   */
   public void setAction(int action)
   {
      this.action = action;
   }
  /**
   * get 剧本
   * @return
   */
   public int getScriptId()
   {
     return scriptId;
   }

  /**
   * set 剧本
   */
   public void setScriptId(int scriptId)
   {
      this.scriptId = scriptId;
   }
  /**
   * get 开始节点id
   * @return
   */
   public int getStartId()
   {
     return startId;
   }

  /**
   * set 开始节点id
   */
   public void setStartId(int startId)
   {
      this.startId = startId;
   }
  /**
   * get 信息
   * @return
   */
   public int getMessage()
   {
     return message;
   }

  /**
   * set 信息
   */
   public void setMessage(int message)
   {
      this.message = message;
   }
  /**
   * get 调用功能
   * @return
   */
   public int getGame()
   {
     return game;
   }

  /**
   * set 调用功能
   */
   public void setGame(int game)
   {
      this.game = game;
   }
  /**
   * get 完成规则
   * @return
   */
   public java.util.Map getGameRule()
   {
     return gameRule;
   }

  /**
   * set 完成规则
   */
   public void setGameRule(java.util.Map gameRule)
   {
      this.gameRule = gameRule;
   }
  /**
   * get 是否隐藏
   * @return
   */
   public boolean getHide()
   {
     return hide;
   }

  /**
   * set 是否隐藏
   */
   public void setHide(boolean hide)
   {
      this.hide = hide;
   }
  /**
   * get 隐藏显示条件（拥有）
   * @return
   */
   public java.util.Map getHideCondition1()
   {
     return hideCondition1;
   }

  /**
   * set 隐藏显示条件（拥有）
   */
   public void setHideCondition1(java.util.Map hideCondition1)
   {
      this.hideCondition1 = hideCondition1;
   }
  /**
   * get 隐藏显示条件（不拥有）
   * @return
   */
   public java.util.Map getHideCondition2()
   {
     return hideCondition2;
   }

  /**
   * set 隐藏显示条件（不拥有）
   */
   public void setHideCondition2(java.util.Map hideCondition2)
   {
      this.hideCondition2 = hideCondition2;
   }
  /**
   * get 消耗时间
   * @return
   */
   public int getCostTime()
   {
     return costTime;
   }

  /**
   * set 消耗时间
   */
   public void setCostTime(int costTime)
   {
      this.costTime = costTime;
   }
  /**
   * get 限制时间
   * @return
   */
   public String getLimitTime()
   {
     return limitTime;
   }

  /**
   * set 限制时间
   */
   public void setLimitTime(String limitTime)
   {
      this.limitTime = limitTime;
   }
  /**
   * get 强制推动时间
   * @return
   */
   public String getAddTime()
   {
     return addTime;
   }

  /**
   * set 强制推动时间
   */
   public void setAddTime(String addTime)
   {
      this.addTime = addTime;
   }
  /**
   * get 完成删除
   * @return
   */
   public boolean getDelete()
   {
     return delete;
   }

  /**
   * set 完成删除
   */
   public void setDelete(boolean delete)
   {
      this.delete = delete;
   }
}
