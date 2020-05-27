/**
 * Auto generated, do not edit it
 *
 * Favor
 */
package data.bean;

public class FavorCfgBean 
{
  private int id; // ID
  private int role; // 对应看板娘
  private String callTableNameF; // 剧本文件名
  private java.util.Map condition; // 章节开启条件（1好感度等级2副本进度）
  private int branchCondition; // 分支奖励条件
  private java.util.Map branchReward; // 分支奖励
  private int type; // 类型（1：主线，2：奖励）
  private int prepose; // 前置关卡
   
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
   * get 对应看板娘
   * @return
   */
   public int getRole()
   {
     return role;
   }

  /**
   * set 对应看板娘
   */
   public void setRole(int role)
   {
      this.role = role;
   }
  /**
   * get 剧本文件名
   * @return
   */
   public String getCallTableNameF()
   {
     return callTableNameF;
   }

  /**
   * set 剧本文件名
   */
   public void setCallTableNameF(String callTableNameF)
   {
      this.callTableNameF = callTableNameF;
   }
  /**
   * get 章节开启条件（1好感度等级2副本进度）
   * @return
   */
   public java.util.Map getCondition()
   {
     return condition;
   }

  /**
   * set 章节开启条件（1好感度等级2副本进度）
   */
   public void setCondition(java.util.Map condition)
   {
      this.condition = condition;
   }
  /**
   * get 分支奖励条件
   * @return
   */
   public int getBranchCondition()
   {
     return branchCondition;
   }

  /**
   * set 分支奖励条件
   */
   public void setBranchCondition(int branchCondition)
   {
      this.branchCondition = branchCondition;
   }
  /**
   * get 分支奖励
   * @return
   */
   public java.util.Map getBranchReward()
   {
     return branchReward;
   }

  /**
   * set 分支奖励
   */
   public void setBranchReward(java.util.Map branchReward)
   {
      this.branchReward = branchReward;
   }
  /**
   * get 类型（1：主线，2：奖励）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 类型（1：主线，2：奖励）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 前置关卡
   * @return
   */
   public int getPrepose()
   {
     return prepose;
   }

  /**
   * set 前置关卡
   */
   public void setPrepose(int prepose)
   {
      this.prepose = prepose;
   }
}
