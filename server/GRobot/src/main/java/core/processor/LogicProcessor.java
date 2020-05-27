package core.processor;

import message.MessageHandler;
import message.MessageHandlerFactory;

public class LogicProcessor extends thread.BaseProcessor {
    public LogicProcessor() {
        super("LogicProcessor", 3);
    }

    public void executeHandler(MessageHandler handler) {
        try {
            if (handler != null) {
                handler.action();
                if (handler.getMessage() != null) {
                    MessageHandlerFactory.getInstance().recycleHandler(handler);
                }
            } else {
                throw new Exception("向线程投递了NULL的Handler: " + this.getName());
            }
        } catch (Exception ex) {
            LOGGER.error(ex, ex);
        }
    }

    /**
     * 单例接口
     *
     * @return
     */
    public static LogicProcessor getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;

        LogicProcessor processor;

        Singleton() {
            this.processor = new LogicProcessor();
        }

        LogicProcessor getProcessor() {
            return processor;
        }
    }
}
