/**
 * Auto generated, do not edit it
 *
 * KabalaMission
 */
package data.bean;

public class KabalaMissionCfgBean 
{
  private int id; // ID
  private int missionType; // 任务类型(1003-击杀特定敌人([[关卡ID，[怪物ID，怪物数量]]包括Boss)，1001-击杀敌人总数([关卡ID,敌人总数])，1002-任务道具([任务道具ID,道具数量])，1004-区域探索([需求数量])，1005-通过关卡([关卡ID]，包括占领据点，任务剧情-战斗类型，1006-触发事件(不填参数，包括任务剧情))
  private java.util.List checkParam; // 任务检测参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
  private int eventID; // 事件ID(kabalaevent表中的ID)
  private java.util.Map missionAward; // 任务奖励(每个阶段任务完成后的奖励)
  private int missionDes; // 任务描述id
   
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
   * get 任务类型(1003-击杀特定敌人([[关卡ID，[怪物ID，怪物数量]]包括Boss)，1001-击杀敌人总数([关卡ID,敌人总数])，1002-任务道具([任务道具ID,道具数量])，1004-区域探索([需求数量])，1005-通过关卡([关卡ID]，包括占领据点，任务剧情-战斗类型，1006-触发事件(不填参数，包括任务剧情))
   * @return
   */
   public int getMissionType()
   {
     return missionType;
   }

  /**
   * set 任务类型(1003-击杀特定敌人([[关卡ID，[怪物ID，怪物数量]]包括Boss)，1001-击杀敌人总数([关卡ID,敌人总数])，1002-任务道具([任务道具ID,道具数量])，1004-区域探索([需求数量])，1005-通过关卡([关卡ID]，包括占领据点，任务剧情-战斗类型，1006-触发事件(不填参数，包括任务剧情))
   */
   public void setMissionType(int missionType)
   {
      this.missionType = missionType;
   }
  /**
   * get 任务检测参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
   * @return
   */
   public java.util.List getCheckParam()
   {
     return checkParam;
   }

  /**
   * set 任务检测参数(每个任务或事件配置参数不一样，具体按文档里的设计来定)
   */
   public void setCheckParam(java.util.List checkParam)
   {
      this.checkParam = checkParam;
   }
  /**
   * get 事件ID(kabalaevent表中的ID)
   * @return
   */
   public int getEventID()
   {
     return eventID;
   }

  /**
   * set 事件ID(kabalaevent表中的ID)
   */
   public void setEventID(int eventID)
   {
      this.eventID = eventID;
   }
  /**
   * get 任务奖励(每个阶段任务完成后的奖励)
   * @return
   */
   public java.util.Map getMissionAward()
   {
     return missionAward;
   }

  /**
   * set 任务奖励(每个阶段任务完成后的奖励)
   */
   public void setMissionAward(java.util.Map missionAward)
   {
      this.missionAward = missionAward;
   }
  /**
   * get 任务描述id
   * @return
   */
   public int getMissionDes()
   {
     return missionDes;
   }

  /**
   * set 任务描述id
   */
   public void setMissionDes(int missionDes)
   {
      this.missionDes = missionDes;
   }
}
