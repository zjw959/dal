/**
 * Auto generated, do not edit it
 *
 * MainLine
 */
package data.bean;

public class MainLineCfgBean 
{
  private int id; // ID
  private int levelGroupId; // 章节
  private int order; // 顺序
  private int type; // 类型，1关卡，2视频，3对话剧本，4CG动画
  private String argument; // 参数
  private int condType; // 进入条件类型
  private String condArgument; // 进入条件参数
   
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
   * get 章节
   * @return
   */
   public int getLevelGroupId()
   {
     return levelGroupId;
   }

  /**
   * set 章节
   */
   public void setLevelGroupId(int levelGroupId)
   {
      this.levelGroupId = levelGroupId;
   }
  /**
   * get 顺序
   * @return
   */
   public int getOrder()
   {
     return order;
   }

  /**
   * set 顺序
   */
   public void setOrder(int order)
   {
      this.order = order;
   }
  /**
   * get 类型，1关卡，2视频，3对话剧本，4CG动画
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 类型，1关卡，2视频，3对话剧本，4CG动画
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 参数
   * @return
   */
   public String getArgument()
   {
     return argument;
   }

  /**
   * set 参数
   */
   public void setArgument(String argument)
   {
      this.argument = argument;
   }
  /**
   * get 进入条件类型
   * @return
   */
   public int getCondType()
   {
     return condType;
   }

  /**
   * set 进入条件类型
   */
   public void setCondType(int condType)
   {
      this.condType = condType;
   }
  /**
   * get 进入条件参数
   * @return
   */
   public String getCondArgument()
   {
     return condArgument;
   }

  /**
   * set 进入条件参数
   */
   public void setCondArgument(String condArgument)
   {
      this.condArgument = condArgument;
   }
}
