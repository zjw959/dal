package thread.sys.base;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import thread.base.GameBaseProcessor;
import thread.timer.TimeEventProcessor;
import utils.ExceptionEx;

/**
 * 公共系统处理线程基类
 */
public abstract class SysFunctionProcessor extends GameBaseProcessor {
    private static final Logger LOGGER = Logger.getLogger(SysFunctionProcessor.class);

    /** 存储本线程所管理的功能Service */
    private final Set<AbsSysFuctionStore> storeServices = new HashSet<>();

    protected final Set<SysService> services = new HashSet<>();

    public SysFunctionProcessor(String name) {
        super(name);
    }


    public SysFunctionProcessor(String simpleName, int corePoolSize, int maxPoolSize) {
        super(simpleName, corePoolSize, maxPoolSize);
    }

    /**
     * 初始化线程所管理的所有系统
     * 
     * @throws Exception
     */
    public void initialize() throws Exception {
        registerService();
        if(services.size() == 0) {
            throw new Exception("can not find process services." + this.getClass().getName());
        }
        for (SysService service : services) {
            service.setProcess(this);
            // 存储线程管理
            if (service instanceof AbsSysFuctionStore) {
                AbsSysFuctionStore store = ((AbsSysFuctionStore) service);
                storeServices.add(store);
                store.initialize();
            }
        }
    }

    /**
     * 注册线程需要管理的系统
     */
    protected abstract void registerService();

    protected void _initTimer(long firstDelay, long delay, boolean loopFixed) {
        TimeEventProcessor.getInstance()
                .addEvent(new ProcessorTimerEvent(firstDelay, delay, loopFixed, this));
    }

    /**
     * 定时器方法
     */
    protected void checkPerSecond() {
        
    }

    /**
     * 停止当前线程并且等待所有消息处理完毕后回存系统数据 顺序不可更改
     */
    public void stopAndSave() {
        try {
            // 先停止线程 并且处理完队列中handler
            LOGGER.info(String.format("thread ：%s begin stop...", getName()));
            super.stop();
            // 所有操作处理完后回存完整数据
            LOGGER.info(String.format("thread ：%s handle all handler and begin save system data...",
                    getName()));
            for (AbsSysFuctionStore manager : storeServices) {
                manager.save();
            }
            LOGGER.info(String.format("thread ：%s  save system data finish.", getName()));
        } catch (Exception e) {
            LOGGER.error(String.format("thread ：%s  stop and save abort !!!. exception: %s ",
                    getName(), ExceptionEx.e2s(e)));
        }
    }
}
