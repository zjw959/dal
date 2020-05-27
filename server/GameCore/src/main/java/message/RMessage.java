package message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.google.protobuf.GeneratedMessage;

/**
 * 接收的消息类
 */
public class RMessage {
    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public void setData(Class<? extends GeneratedMessage> clazz, byte[] msgData)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = clazz.getDeclaredMethod("parseFrom", byte[].class);
        this.data = method.invoke(null, msgData);
    }

    /** 消息Id **/
    private short id = 0;

    /** 消息收到的时间 **/
    private long receiveTime = 0;

    /** 消息内容 **/
    private Object data = null;
}
