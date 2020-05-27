/**
 * Auto generated, do not edit it
 *
 * DungeonChapter
 */
package data.bean;

public class DungeonChapterCfgBean 
{
  private int id; // id
  private int type; // 章节类型（1剧情章节，2日常副本）
  private int order; // 顺序
  private int unlockLevel; // 开放等级
  private int orderName; // 章节编号名
  private String simpleName; // 关卡类型简名（获取系统用）
  private int[] endShowRole; // 章节完成展示角色
  private String endWord; // 章节完成文字
  private String finishPicture; // 章节完成报幕背景图
  private int unlockHero; // 章节完成奖励精灵(同样以道具形式配置)
   
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
   * get 章节类型（1剧情章节，2日常副本）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set 章节类型（1剧情章节，2日常副本）
   */
   public void setType(int type)
   {
      this.type = type;
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
   * get 开放等级
   * @return
   */
   public int getUnlockLevel()
   {
     return unlockLevel;
   }

  /**
   * set 开放等级
   */
   public void setUnlockLevel(int unlockLevel)
   {
      this.unlockLevel = unlockLevel;
   }
  /**
   * get 章节编号名
   * @return
   */
   public int getOrderName()
   {
     return orderName;
   }

  /**
   * set 章节编号名
   */
   public void setOrderName(int orderName)
   {
      this.orderName = orderName;
   }
  /**
   * get 关卡类型简名（获取系统用）
   * @return
   */
   public String getSimpleName()
   {
     return simpleName;
   }

  /**
   * set 关卡类型简名（获取系统用）
   */
   public void setSimpleName(String simpleName)
   {
      this.simpleName = simpleName;
   }
  /**
   * get 章节完成展示角色
   * @return
   */
   public int[] getEndShowRole()
   {
     return endShowRole;
   }

  /**
   * set 章节完成展示角色
   */
   public void setEndShowRole(int[] endShowRole)
   {
      this.endShowRole = endShowRole;
   }
  /**
   * get 章节完成文字
   * @return
   */
   public String getEndWord()
   {
     return endWord;
   }

  /**
   * set 章节完成文字
   */
   public void setEndWord(String endWord)
   {
      this.endWord = endWord;
   }
  /**
   * get 章节完成报幕背景图
   * @return
   */
   public String getFinishPicture()
   {
     return finishPicture;
   }

  /**
   * set 章节完成报幕背景图
   */
   public void setFinishPicture(String finishPicture)
   {
      this.finishPicture = finishPicture;
   }
  /**
   * get 章节完成奖励精灵(同样以道具形式配置)
   * @return
   */
   public int getUnlockHero()
   {
     return unlockHero;
   }

  /**
   * set 章节完成奖励精灵(同样以道具形式配置)
   */
   public void setUnlockHero(int unlockHero)
   {
      this.unlockHero = unlockHero;
   }
}
