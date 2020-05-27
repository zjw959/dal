/**
 * Auto generated, do not edit it
 *
 * CityRoleModel
 */
package data.bean;

public class CityRoleModelCfgBean 
{
  private int id; // id
  private int modelId; // 对应看板娘ID
  private int roleType; // 类型（1：精灵，2：npc）
   
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
   * get 对应看板娘ID
   * @return
   */
   public int getModelId()
   {
     return modelId;
   }

  /**
   * set 对应看板娘ID
   */
   public void setModelId(int modelId)
   {
      this.modelId = modelId;
   }
  /**
   * get 类型（1：精灵，2：npc）
   * @return
   */
   public int getRoleType()
   {
     return roleType;
   }

  /**
   * set 类型（1：精灵，2：npc）
   */
   public void setRoleType(int roleType)
   {
      this.roleType = roleType;
   }
}
