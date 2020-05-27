package thread;

import message.SMessageFactory;

public class FightRoomPrepareProcessor extends BaseProcessor {
    // 消息工厂
    private final SMessageFactory sMsgFactory = new SMessageFactory(100);
    
    public FightRoomPrepareProcessor() {
        super(FightRoomPrepareProcessor.class.getSimpleName());
    }

    /**
     * 获取PvpMatchProcessor的实例对象.
     *
     * @return
     */
    public static FightRoomPrepareProcessor getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    public SMessageFactory getsMsgFactory() {
        return sMsgFactory;
    }

    public void executeHandler(BaseHandler handler) {
        super.executeHandler(handler);
    }
    
    /**
     * 用枚举来实现单例
     */
    private enum Singleton {

        INSTANCE;
        FightRoomPrepareProcessor processor;

        Singleton() {
            this.processor = new FightRoomPrepareProcessor();
        }

        FightRoomPrepareProcessor getProcessor() {
            return processor;
        }
    }
}
