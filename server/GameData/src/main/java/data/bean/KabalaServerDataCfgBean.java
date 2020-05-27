/**
 * Auto generated, do not edit it
 *
 * KabalaServerData
 */
package data.bean;

public class KabalaServerDataCfgBean 
{
  private int id; // 编号
  private int[] mapSize; // 地图尺寸(长x宽，单位：格)
  private java.util.List firstJsonData; // 第1层地图文件的json数据
  private java.util.List thirdJsonData; // 第3层地图文件的json数据
   
  /**
   * get 编号
   * @return
   */
   public int getId()
   {
     return id;
   }

  /**
   * set 编号
   */
   public void setId(int id)
   {
      this.id = id;
   }
  /**
   * get 地图尺寸(长x宽，单位：格)
   * @return
   */
   public int[] getMapSize()
   {
     return mapSize;
   }

  /**
   * set 地图尺寸(长x宽，单位：格)
   */
   public void setMapSize(int[] mapSize)
   {
      this.mapSize = mapSize;
   }
  /**
   * get 第1层地图文件的json数据
   * @return
   */
   public java.util.List getFirstJsonData()
   {
     return firstJsonData;
   }

  /**
   * set 第1层地图文件的json数据
   */
   public void setFirstJsonData(java.util.List firstJsonData)
   {
      this.firstJsonData = firstJsonData;
   }
  /**
   * get 第3层地图文件的json数据
   * @return
   */
   public java.util.List getThirdJsonData()
   {
     return thirdJsonData;
   }

  /**
   * set 第3层地图文件的json数据
   */
   public void setThirdJsonData(java.util.List thirdJsonData)
   {
      this.thirdJsonData = thirdJsonData;
   }
}
