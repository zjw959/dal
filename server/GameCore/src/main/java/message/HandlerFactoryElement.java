package message;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.protobuf.GeneratedMessage;

/**
 * 记录message和handler的映射关系，同时保存handler队列
 */
public class HandlerFactoryElement {

    public HandlerFactoryElement(Class<MessageHandler> msgClazz, Class<GeneratedMessage> protoClazz,
            int size) throws IllegalAccessException, InstantiationException {
        this.protoClazz = protoClazz;
        this.msgClazz = msgClazz;
        this.queue = new ConcurrentLinkedQueue<>();
        for (int a = 0; a < size; ++a) {
            MessageHandler messageHandler = msgClazz.newInstance();
            messageHandler.setMessage(new RMessage());
            queue.offer(messageHandler);
        }
    }

    public MessageHandler createNewHandler() throws IllegalAccessException, InstantiationException {
        MessageHandler messageHandler = msgClazz.newInstance();
        messageHandler.setMessage(new RMessage());
        return messageHandler;
    }

    public ConcurrentLinkedQueue<MessageHandler> getQueue() {
        return queue;
    }

    public Class<? extends GeneratedMessage> getProtoClazz() {
        return protoClazz;
    }

    public Class<? extends MessageHandler> getMsgClazz() {
        return msgClazz;
    }

    private Class<? extends GeneratedMessage> protoClazz;
    private Class<? extends MessageHandler> msgClazz;
    private ConcurrentLinkedQueue<MessageHandler> queue;
}
