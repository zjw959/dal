/**
 * Auto generated, do not edit it
 *
 * CityRole
 */
package data.bean;

public class CityRoleCfgBean 
{
  private int id; // id
  private int type; // 类型（1主角2配角）
  private int roleId; // 角色ID
  private int nameId; // 角色名称
  private int[] dress; // 模型组
  private int[] time; // 时间
  private String move; // 移动（AI1）
  private int[] dialog; // 台词（AI2）
  private String action; // 随机动作组（AI3）
  private int showCondition; // 出现概率
  private int[] condition; // 角色出现规则
   
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
   * get 类型（1主角2配角）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 类型（1主角2配角）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 角色ID
   * @return
   */
   public int getRoleId()
   {
     return roleId;
   }

  /**
   * set 角色ID
   */
   public void setRoleId(int roleId)
   {
      this.roleId = roleId;
   }
  /**
   * get 角色名称
   * @return
   */
   public int getNameId()
   {
     return nameId;
   }

  /**
   * set 角色名称
   */
   public void setNameId(int nameId)
   {
      this.nameId = nameId;
   }
  /**
   * get 模型组
   * @return
   */
   public int[] getDress()
   {
     return dress;
   }

  /**
   * set 模型组
   */
   public void setDress(int[] dress)
   {
      this.dress = dress;
   }
  /**
   * get 时间
   * @return
   */
   public int[] getTime()
   {
     return time;
   }

  /**
   * set 时间
   */
   public void setTime(int[] time)
   {
      this.time = time;
   }
  /**
   * get 移动（AI1）
   * @return
   */
   public String getMove()
   {
     return move;
   }

  /**
   * set 移动（AI1）
   */
   public void setMove(String move)
   {
      this.move = move;
   }
  /**
   * get 台词（AI2）
   * @return
   */
   public int[] getDialog()
   {
     return dialog;
   }

  /**
   * set 台词（AI2）
   */
   public void setDialog(int[] dialog)
   {
      this.dialog = dialog;
   }
  /**
   * get 随机动作组（AI3）
   * @return
   */
   public String getAction()
   {
     return action;
   }

  /**
   * set 随机动作组（AI3）
   */
   public void setAction(String action)
   {
      this.action = action;
   }
  /**
   * get 出现概率
   * @return
   */
   public int getShowCondition()
   {
     return showCondition;
   }

  /**
   * set 出现概率
   */
   public void setShowCondition(int showCondition)
   {
      this.showCondition = showCondition;
   }
  /**
   * get 角色出现规则
   * @return
   */
   public int[] getCondition()
   {
     return condition;
   }

  /**
   * set 角色出现规则
   */
   public void setCondition(int[] condition)
   {
      this.condition = condition;
   }
}
