package thread;

public class FightRoomProcessorManager extends AbstractRoomProcessorManager {
    protected FightRoomProcessorManager() {
        super(FightRoomProcessor.class);
    }

    protected FightRoomProcessorManager(int processorCount) {
        super(processorCount, FightRoomProcessor.class);
    }

    /**
     * 获取PvpRoomProcessorManager的实例对象.
     *
     * @return
     */
    public static FightRoomProcessorManager getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        FightRoomProcessorManager processor;

        Singleton() {
            this.processor = new FightRoomProcessorManager();
        }

        FightRoomProcessorManager getProcessor() {
            return processor;
        }
    }

    public void stop() {
        super.stop();
    }
}
