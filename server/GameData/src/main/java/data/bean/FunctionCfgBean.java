/**
 * Auto generated, do not edit it
 *
 * Function
 */
package data.bean;

public class FunctionCfgBean 
{
  private int id; // id
  private int serverId; // 功能分类（1服务端功能 2客户端功能 3公用功能）
  private int funcId; // 对应客户端功能id
  private int isOpen; // 是否开启
   
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
   * get 功能分类（1服务端功能 2客户端功能 3公用功能）
   * @return
   */
   public int getServerId()
   {
     return serverId;
   }

  /**
   * set 功能分类（1服务端功能 2客户端功能 3公用功能）
   */
   public void setServerId(int serverId)
   {
      this.serverId = serverId;
   }
  /**
   * get 对应客户端功能id
   * @return
   */
   public int getFuncId()
   {
     return funcId;
   }

  /**
   * set 对应客户端功能id
   */
   public void setFuncId(int funcId)
   {
      this.funcId = funcId;
   }
  /**
   * get 是否开启
   * @return
   */
   public int getIsOpen()
   {
     return isOpen;
   }

  /**
   * set 是否开启
   */
   public void setIsOpen(int isOpen)
   {
      this.isOpen = isOpen;
   }
}
