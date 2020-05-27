package core.net.message;

/**
 * 发送消息
 */
public class SMessage {

    public SMessage(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }
    
    public SMessage(int id, byte[] data, int resOrder) {
        this.id = id;
        this.data = data;
        this.resOrder = resOrder;
    }
    
    public SMessage(int id, int status) {
        this.id = id;
        this.status = status;
    }

    public SMessage(SMessageFactory factory) {
        this.factory = factory;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    /**
     * 消息工厂
     */
    private SMessageFactory factory;

    /** 消息id **/
    private int id;
    /** 消息内容 **/
    private byte[] data;
    /** 状态码 0-正常  非0-异常 **/
    private int status;
    /**
     * 默认为 -1
     * 
     * -2 为没有对应的response驱动
     */
    private int resOrder = -2;

    public void reuse() {
        if (factory != null) {
            factory.recycleSMessage(this);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void clear() {
        this.data = null;
        this.status = 0;
    }
	
	public int getResOrder() {
        return resOrder;
    }
}
