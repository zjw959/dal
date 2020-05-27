/**
 * Auto generated, do not edit it
 *
 * DatingRule
 */
package data.bean;

public class DatingRuleCfgBean 
{
  private int id; // id
  private int type; // 约会类型（主1日2预3事4工5游6）
  private String callTableName; // 调用剧情表格文件名
  private int phoneRole; // 对应手机角色
  private int phoneScript; // 对应手机剧本ID
  private java.util.Map enterCondition; // 进入条件
  private int startID; // 手机剧本第一句
  private java.util.Map phoneType; // 节点处理（手机）
  private java.util.Map phoneEnd; // 结局结算（手机）
  private int cityLines; // 城市台词
  private java.util.Map failCondition; // 失败条件（主线约会）
  private java.util.Map otherInfo; // 其他信息（预定约会）
  private int buildingId; // 建筑
  private int itemType; // 道具约会道具类型
  private int roleId; // 乱入约会主人物
  private int dungeonRoleId; // 奖励发放对应角色(副本约会关卡)
  private int startNodeId; // 开始节点id
  private java.util.List otherScriptIds; // 其它剧本id
  private int roleModelId; // 人物路径
  private int finallyType; // 结算类型
  private String item; // 道具（道具约会）
  private int datingTitle; // 约会标题
  private int datingContent; // 约会内容
  private java.util.List dungeonDateHeart; // 主线挑战目标
   
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
   * get 约会类型（主1日2预3事4工5游6）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 约会类型（主1日2预3事4工5游6）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 调用剧情表格文件名
   * @return
   */
   public String getCallTableName()
   {
     return callTableName;
   }

  /**
   * set 调用剧情表格文件名
   */
   public void setCallTableName(String callTableName)
   {
      this.callTableName = callTableName;
   }
  /**
   * get 对应手机角色
   * @return
   */
   public int getPhoneRole()
   {
     return phoneRole;
   }

  /**
   * set 对应手机角色
   */
   public void setPhoneRole(int phoneRole)
   {
      this.phoneRole = phoneRole;
   }
  /**
   * get 对应手机剧本ID
   * @return
   */
   public int getPhoneScript()
   {
     return phoneScript;
   }

  /**
   * set 对应手机剧本ID
   */
   public void setPhoneScript(int phoneScript)
   {
      this.phoneScript = phoneScript;
   }
  /**
   * get 进入条件
   * @return
   */
   public java.util.Map getEnterCondition()
   {
     return enterCondition;
   }

  /**
   * set 进入条件
   */
   public void setEnterCondition(java.util.Map enterCondition)
   {
      this.enterCondition = enterCondition;
   }
  /**
   * get 手机剧本第一句
   * @return
   */
   public int getStartID()
   {
     return startID;
   }

  /**
   * set 手机剧本第一句
   */
   public void setStartID(int startID)
   {
      this.startID = startID;
   }
  /**
   * get 节点处理（手机）
   * @return
   */
   public java.util.Map getPhoneType()
   {
     return phoneType;
   }

  /**
   * set 节点处理（手机）
   */
   public void setPhoneType(java.util.Map phoneType)
   {
      this.phoneType = phoneType;
   }
  /**
   * get 结局结算（手机）
   * @return
   */
   public java.util.Map getPhoneEnd()
   {
     return phoneEnd;
   }

  /**
   * set 结局结算（手机）
   */
   public void setPhoneEnd(java.util.Map phoneEnd)
   {
      this.phoneEnd = phoneEnd;
   }
  /**
   * get 城市台词
   * @return
   */
   public int getCityLines()
   {
     return cityLines;
   }

  /**
   * set 城市台词
   */
   public void setCityLines(int cityLines)
   {
      this.cityLines = cityLines;
   }
  /**
   * get 失败条件（主线约会）
   * @return
   */
   public java.util.Map getFailCondition()
   {
     return failCondition;
   }

  /**
   * set 失败条件（主线约会）
   */
   public void setFailCondition(java.util.Map failCondition)
   {
      this.failCondition = failCondition;
   }
  /**
   * get 其他信息（预定约会）
   * @return
   */
   public java.util.Map getOtherInfo()
   {
     return otherInfo;
   }

  /**
   * set 其他信息（预定约会）
   */
   public void setOtherInfo(java.util.Map otherInfo)
   {
      this.otherInfo = otherInfo;
   }
  /**
   * get 建筑
   * @return
   */
   public int getBuildingId()
   {
     return buildingId;
   }

  /**
   * set 建筑
   */
   public void setBuildingId(int buildingId)
   {
      this.buildingId = buildingId;
   }
  /**
   * get 道具约会道具类型
   * @return
   */
   public int getItemType()
   {
     return itemType;
   }

  /**
   * set 道具约会道具类型
   */
   public void setItemType(int itemType)
   {
      this.itemType = itemType;
   }
  /**
   * get 乱入约会主人物
   * @return
   */
   public int getRoleId()
   {
     return roleId;
   }

  /**
   * set 乱入约会主人物
   */
   public void setRoleId(int roleId)
   {
      this.roleId = roleId;
   }
  /**
   * get 奖励发放对应角色(副本约会关卡)
   * @return
   */
   public int getDungeonRoleId()
   {
     return dungeonRoleId;
   }

  /**
   * set 奖励发放对应角色(副本约会关卡)
   */
   public void setDungeonRoleId(int dungeonRoleId)
   {
      this.dungeonRoleId = dungeonRoleId;
   }
  /**
   * get 开始节点id
   * @return
   */
   public int getStartNodeId()
   {
     return startNodeId;
   }

  /**
   * set 开始节点id
   */
   public void setStartNodeId(int startNodeId)
   {
      this.startNodeId = startNodeId;
   }
  /**
   * get 其它剧本id
   * @return
   */
   public java.util.List getOtherScriptIds()
   {
     return otherScriptIds;
   }

  /**
   * set 其它剧本id
   */
   public void setOtherScriptIds(java.util.List otherScriptIds)
   {
      this.otherScriptIds = otherScriptIds;
   }
  /**
   * get 人物路径
   * @return
   */
   public int getRoleModelId()
   {
     return roleModelId;
   }

  /**
   * set 人物路径
   */
   public void setRoleModelId(int roleModelId)
   {
      this.roleModelId = roleModelId;
   }
  /**
   * get 结算类型
   * @return
   */
   public int getFinallyType()
   {
     return finallyType;
   }

  /**
   * set 结算类型
   */
   public void setFinallyType(int finallyType)
   {
      this.finallyType = finallyType;
   }
  /**
   * get 道具（道具约会）
   * @return
   */
   public String getItem()
   {
     return item;
   }

  /**
   * set 道具（道具约会）
   */
   public void setItem(String item)
   {
      this.item = item;
   }
  /**
   * get 约会标题
   * @return
   */
   public int getDatingTitle()
   {
     return datingTitle;
   }

  /**
   * set 约会标题
   */
   public void setDatingTitle(int datingTitle)
   {
      this.datingTitle = datingTitle;
   }
  /**
   * get 约会内容
   * @return
   */
   public int getDatingContent()
   {
     return datingContent;
   }

  /**
   * set 约会内容
   */
   public void setDatingContent(int datingContent)
   {
      this.datingContent = datingContent;
   }
  /**
   * get 主线挑战目标
   * @return
   */
   public java.util.List getDungeonDateHeart()
   {
     return dungeonDateHeart;
   }

  /**
   * set 主线挑战目标
   */
   public void setDungeonDateHeart(java.util.List dungeonDateHeart)
   {
      this.dungeonDateHeart = dungeonDateHeart;
   }
}
