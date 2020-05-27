package message;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.google.protobuf.GeneratedMessage;

public class MessageResClazz {
    private ConcurrentHashMap<Class<? extends GeneratedMessage.Builder>, Integer> elements =
            new ConcurrentHashMap<Class<? extends GeneratedMessage.Builder>, Integer>();

    public Integer getMsgId(Class<? extends GeneratedMessage.Builder> clazz) {
        return this.elements.get(clazz);
    }

    public void init(Class protoClass) throws Exception {
        // 解析此proto中所有message
        for (Class<?> msgClass : protoClass.getClasses()) {
            // 解析message中的对象
            for (Class<?> innerClass : msgClass.getClasses()) {
                // 如果该message中包含枚举类 MsgID, 就证明是属于交互数据的对象,而非单独的消息体
                if (innerClass.getSimpleName().equals("MsgID")) {
                    // message id
                    Class<?> clazzMsg = Class.forName(innerClass.getName());
                    if (clazzMsg == null) {
                        continue;
                    }
                    int msgId = clazzMsg.getField("eMsgID_VALUE").getInt(msgClass);
//                    String val = String.valueOf(msgId);
//                    String charVal = String.valueOf(val.charAt(val.length() - 4));
//                    if (!charVal.equals("1")) {
//                        continue;
//                    }
                    Integer integer = elements.get(msgClass);
                    if (integer != null) {
                        throw new Exception("启动服务器初始化消息,Respone存在重复的消息映射,消息:" + msgId
                                + ",clazzName:" + msgClass.getSimpleName());
                    }
                    Class<? extends GeneratedMessage.Builder> _clazz =
                            (Class<? extends GeneratedMessage.Builder>) Class
                                    .forName(msgClass.getName() + "$Builder");
                    elements.put(_clazz, msgId);
                }
            }
        }
    }

    /**
     * 单件枚举
     */
    private enum Singleton {
        INSTANCE;

        MessageResClazz instance;

        Singleton() {
            this.instance = new MessageResClazz();
        }

        MessageResClazz getInstance() {
            return instance;
        }
    }

    /**
     * 单件
     * 
     * @return
     */
    public static MessageResClazz getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private static final Logger LOGGER = Logger.getLogger(MessageResClazz.class);
}
