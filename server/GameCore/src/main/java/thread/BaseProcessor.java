package thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import utils.ConstDefineCore;
import utils.ExceptionEx;

/**
 * 线程基类
 */
public abstract class BaseProcessor implements IPrintExecHanler {
    protected final static Logger LOGGER = Logger.getLogger(BaseProcessor.class);

    protected final ThreadPoolExecutor executor;
    /** 线程名字 **/
    protected final String name;
    /** 线程队列 **/
    protected final BlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>();

    private long _logDoTime = 200l;

    private static AtomicLong execNum = new AtomicLong();
    public AtomicLong exceAloneNum = new AtomicLong();

    protected int currentQueueSize;

    protected long checktime;

    protected int defalutPrintSize = 200;
    protected long defaultLogWaitTime = 2000l;


    /**
     * 单线程
     * 
     * @param name
     */
    public BaseProcessor(String name) {
        this(name, 1, 1, 0l, false);
    }

    /**
     * 固定大小线程池
     * 
     * @param name 线程池名
     * @param threadCount 线程属相
     */
    public BaseProcessor(String name, int threadCount) {
        this(name, threadCount, threadCount, 0L, false);
    }

    /**
     * 线程池
     * 
     * @param name 线程池名
     * @param corePoolSize 核心数量
     * @param maximumPoolSize 最大数量
     */
    public BaseProcessor(String name, int corePoolSize, int maxPoolSize) {
        this(name, corePoolSize, maxPoolSize, 0L, false);
    }

    /**
     * 线程池
     * 
     * @param name 线程池名
     * @param corePoolSize 核心数量
     * @param maximumPoolSize 最大数量
     * @param keepTime 保持时间
     * @param allowCoreThreadTimeOut 是否允许核心线程超时
     */
    public BaseProcessor(String name, int corePoolSize, int maxPoolSize, long keepTime,
            boolean allowCoreThreadTimeOut) {
        // 线程名字
        this.name = name;

        // 线程实例
        this.executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepTime,
                TimeUnit.MILLISECONDS, linkedBlockingQueue, new HandlerThreadFactory(name));

        // 是否允许核心线程超时
        this.executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);

        // 启动所有核心线程，使其处于等待工作的空闲状态。仅当执行新任务时，此操作才重写默认的启动核心线程策略。
        this.executor.prestartAllCoreThreads();
    }

    public int getQueueSize() {
        return this.linkedBlockingQueue.size();
    }

    /**
     * 向线程池投递带有返回值的handler
     * 
     * @param handler
     * @return
     */
    protected Future<String> submitHandler(BaseHandler handler) {
        try {
            if (executor.isShutdown()) {
                LOGGER.error(
                        "线程:" + this.name + " 已经停止,Handler被丢弃: " + handler.getClass().getName());
                return null;
            }

            handler.setTime(System.currentTimeMillis());
            return executor.submit(new Callable<String>() {
                private BaseHandler handler;

                public Callable<String> setHandler(BaseHandler handler) {
                    this.handler = handler;
                    return this;
                }

                @Override
                public String call() {
                    try {
                        long _begin = System.currentTimeMillis();
                        long _waitTime = System.currentTimeMillis() - handler.getTime();

                        handler.action();
                        long _doTime = System.currentTimeMillis() - _begin;

                        doPrintAction(handler, _doTime);
                        waitPrintAction(handler, _waitTime);


                        execNum.incrementAndGet();
                        exceAloneNum.incrementAndGet();
                    } catch (Exception ex) {
                        String exStr = ExceptionEx.e2s(ex);
                        LOGGER.error(exStr);
                        handler.setResult(exStr);
                    }

                    return handler.getResult();
                }
            }.setHandler(handler));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return null;
        }
    }


    /**
     * 向该线程投递Handler(Runnable)
     * 
     * @param handler
     */
    protected void executeHandler(BaseHandler handler) {
        try {
            if (executor.isShutdown()) {
                LOGGER.error(
                        "线程" + this.name + " 已经停止, Handler被丢弃: " + handler.getClass().getName());
                return;
            }
            // if (!"LPlayerProcessSecdTickHandler".equals(handler.getClass().getSimpleName())) {
            // System.err.println(handler.getClass().getSimpleName());
            // }

            handler.setTime(System.currentTimeMillis());
            executor.execute(new Runnable() {
                private BaseHandler handler;

                public Runnable setHandler(BaseHandler handler) {
                    this.handler = handler;
                    return this;
                }

                @Override
                public void run() {
                    execHandler(this.handler);
                    execNum.incrementAndGet();
                    exceAloneNum.incrementAndGet();
                }
            }.setHandler(handler));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    /**
     * 执行投递到该线程中的handler，每次执行一个
     * 
     * @param handler
     */
    protected void execHandler(BaseHandler handler) {
        try {
            if (handler != null) {
                long _begin = System.currentTimeMillis();
                long _waitTime = _begin - handler.getTime();

                if (!gameBeforeHandler(handler)) {
                    return;
                }

                handler.action();
                long _doTime = System.currentTimeMillis() - _begin;

                doPrintAction(handler, _doTime);
                waitPrintAction(handler, _waitTime);
            } else {
                throw new Exception("向线程投递了NULL的Handler:" + this.name);
            }
        } catch (Exception e) {
            // if (!(e instanceof AbstractLogicModelException)) {
            // LOGGER.error(ExceptionEx.e2s(e));
            // }
            gameExceptionHandler(handler, e);
        } finally {
            if (handler instanceof IAfterExecHanler) {
                ((IAfterExecHanler) handler).afterAction();
            }
        }

        int size = linkedBlockingQueue.size();
        long now = System.currentTimeMillis();
        this.sizePrintAction(handler, now, size);
        this.currentQueueSize = size;
    }

    @Override
    public void sizePrintAction(BaseHandler handler, long now, int size) {
        // 持续3秒都超过阈值
        if (this.checktime + 3000 > now) {
            this.checktime = now;
            if (size > this.defalutPrintSize && this.currentQueueSize > this.defalutPrintSize) {
                LOGGER.warn("线程队列长度超过警戒值:" + size + " " + getName());
            }
        }
    }

    @Override
    public void waitPrintAction(BaseHandler handler, long waitTime) {
        if (waitTime > defaultLogWaitTime) {
            LOGGER.warn(ConstDefineCore.LOG_WAIT_OVER_TIME + " threadName:" + this.name
                    + ",handler:" + handler.getClass().getName() + ",waitTime:" + waitTime);
        }
    }

    @Override
    public void doPrintAction(BaseHandler handler, long doTime) {
        if (doTime > _logDoTime) {
            LOGGER.warn(ConstDefineCore.LOG_DO_OVER_TIME + " threadName:" + this.name + ",handler:"
                    + handler.getClass().getName() + ",doTime:" + doTime);
        }
    }

    /**
     * 具体游戏逻辑内部对前置条件的处理
     * 
     * @param handler
     * @return
     */
    protected boolean gameBeforeHandler(BaseHandler handler) {
        return true;
    }

    /**
     * 具体游戏逻辑内部对异常的处理
     * 
     * @param handler
     */
    protected void gameExceptionHandler(BaseHandler handler, Exception e) {
        LOGGER.error("Handler异常: " + handler.getClass().getName() + ExceptionEx.e2s(e));
    }


    /**
     * 线程是否已关闭
     * 
     * 如果isCompleteAllCommand为true, 则已经执行过stop, 并且已提交的命令全部执行完的情况下返回true
     *
     * @param isCompleteAllCommand 是否完成所有任务
     * @return
     */
    public boolean isStop(boolean isCompleteAllCommand) {
        if (isCompleteAllCommand) {
            return executor.isTerminated();
        } else {
            return executor.isShutdown();
        }
    }

    /**
     * 关闭线程池
     * 
     * @param await 是否等待队列全部执行完成
     */
    public void stop() {
        stop(true);
    }

    /**
     * 关闭线程池
     * 
     * @param await 是否等待队列全部执行完成
     */
    private void stop(boolean await) {
        if (await) {
            executor.shutdown();
        } else {
            executor.shutdownNow();
        }

        try {
            while (!this.executor.isTerminated()) {
                LOGGER.info("Thread: " + this.name + " 执行未结束....., 剩余队列长度: "
                        + this.linkedBlockingQueue.size());
                this.executor.awaitTermination(1L, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    public String getName() {
        return name;
    }

    public long getAloneNum() {
        return exceAloneNum.get();
    }

    public static long getExecNum() {
        return execNum.get();
    }

    /** 是否设置允许核心线程超时（为true且keepTime>0时，会移除超时的核心线程） */
    protected void allowCoreThreadTimeOut(boolean value) {
        executor.allowCoreThreadTimeOut(value);
    }
}
