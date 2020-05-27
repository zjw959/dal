/**
 * Auto generated, do not edit it
 *
 * CookQte
 */
package data.bean;

public class CookQteCfgBean 
{
  private int id; // ID
  private int type; // QTE类型（1重合点击、2长按、3连续点击）
  private String des; // 注释（策划用）
  private int time; // 持续时间
  private int[] judge; // 判定标准（出现后时间）
  private int[] point; // 获得分值（需与判定标准一一对应）
  private String[] showimg; // 展示美术字
  private int max; // 每秒最大允许点击次数（配置前请和后端核对）
   
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
   * get QTE类型（1重合点击、2长按、3连续点击）
   * @return
   */
   public int getType()
   {
     return type;
   }

  /**
   * set QTE类型（1重合点击、2长按、3连续点击）
   */
   public void setType(int type)
   {
      this.type = type;
   }
  /**
   * get 注释（策划用）
   * @return
   */
   public String getDes()
   {
     return des;
   }

  /**
   * set 注释（策划用）
   */
   public void setDes(String des)
   {
      this.des = des;
   }
  /**
   * get 持续时间
   * @return
   */
   public int getTime()
   {
     return time;
   }

  /**
   * set 持续时间
   */
   public void setTime(int time)
   {
      this.time = time;
   }
  /**
   * get 判定标准（出现后时间）
   * @return
   */
   public int[] getJudge()
   {
     return judge;
   }

  /**
   * set 判定标准（出现后时间）
   */
   public void setJudge(int[] judge)
   {
      this.judge = judge;
   }
  /**
   * get 获得分值（需与判定标准一一对应）
   * @return
   */
   public int[] getPoint()
   {
     return point;
   }

  /**
   * set 获得分值（需与判定标准一一对应）
   */
   public void setPoint(int[] point)
   {
      this.point = point;
   }
  /**
   * get 展示美术字
   * @return
   */
   public String[] getShowimg()
   {
     return showimg;
   }

  /**
   * set 展示美术字
   */
   public void setShowimg(String[] showimg)
   {
      this.showimg = showimg;
   }
  /**
   * get 每秒最大允许点击次数（配置前请和后端核对）
   * @return
   */
   public int getMax()
   {
     return max;
   }

  /**
   * set 每秒最大允许点击次数（配置前请和后端核对）
   */
   public void setMax(int max)
   {
      this.max = max;
   }
}
