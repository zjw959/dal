package message;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import utils.ExceptionEx;

public class MessageHandlerFactory {

    /**
     * 回收Handler
     * 
     * @param messageHandler
     */
    public void recycleHandler(MessageHandler messageHandler) {
        RMessage rMessage = messageHandler.getMessage();
        if (rMessage == null) {
            LOGGER.warn("回收的Handler未携带RMessage：" + messageHandler.toString());
            return;
        }

        HandlerFactoryElement element = elements.get(rMessage.getId());
        if (element == null) {
            LOGGER.error(
                    "消息映射不存在: " + rMessage.getId() + ", 无法回收Hanlder: " + messageHandler.toString());
            return;
        }

        if (element.getMsgClazz().isInstance(messageHandler)) {
            element.getQueue().offer(messageHandler);
        }
    }

    /**
     * 获取Handler
     * 
     * @param msgId
     * @param data
     * @return
     */
    public MessageHandler fetchHandler(short msgId, long receiveTime, byte[] data) {
        HandlerFactoryElement element = elements.get(msgId);
        if (element == null) {
            LOGGER.error("消息没有对应的处理对象:" + msgId);
            return null;
        }

        MessageHandler messageHandler = null;
        ConcurrentLinkedQueue<MessageHandler> queue = element.getQueue();
        if (queue != null && queue.peek() != null) {
            messageHandler = queue.poll();
        }

        try {
            if (messageHandler == null) {
                messageHandler = element.createNewHandler();
            }

            // 重置缓存

            // 一定要在不使用的时候就清理掉,否则会造成player对象不回收.
            messageHandler.setGameData(null);

            messageHandler.getMessage().setId(msgId);
            messageHandler.getMessage().setReceiveTime(receiveTime);
            messageHandler.getMessage().setData(element.getProtoClazz(), data);

            return messageHandler;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException
                | InvocationTargetException e) {
            LOGGER.error(ExceptionEx.e2s(e) + " msgId:" + msgId);
        }
        return null;
    }

    public void initMessageHandler(Class protoClazzName, Class<MessageHandler> mClazz, int size)
            throws Exception {
        // message id
        Class<?> clazz = Class.forName(protoClazzName.getName() + "$MsgID");
        short msgId = (short)clazz.getField("eMsgID_VALUE").getInt(clazz);
        HandlerFactoryElement element = elements.get(msgId);
        if (element != null) {
            throw new Exception("启动服务器初始化消息,存在重复的消息映射,消息:" + msgId + ",clazzName:"
                    + clazz.getName() + " + " + element.getMsgClazz().getSimpleName());
        }

        element = new HandlerFactoryElement(mClazz, protoClazzName, size);
        elements.put(msgId, element);
    }

    /**
     * 单件枚举
     */
    private enum Singleton {
        INSTANCE;

        MessageHandlerFactory instance;

        Singleton() {
            this.instance = new MessageHandlerFactory();
        }

        MessageHandlerFactory getInstance() {
            return instance;
        }
    }

    /**
     * 单件
     * 
     * @return
     */
    public static MessageHandlerFactory getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private static final Logger LOGGER = Logger.getLogger(MessageHandlerFactory.class);
    private ConcurrentHashMap<Short, HandlerFactoryElement> elements =
            new ConcurrentHashMap<Short, HandlerFactoryElement>();
}
