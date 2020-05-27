package thread.sys.base;

import org.apache.log4j.Logger;
import thread.timer.TimerEvent;
import utils.ExceptionEx;

/**
 * 逻辑处理线程 Tick 定时器
 */
public class ProcessorTimerEvent extends TimerEvent {
    private static final Logger LOGGER = Logger.getLogger(ProcessorTimerEvent.class); 
    
    SysFunctionProcessor sysFunctionProcessor;
    public ProcessorTimerEvent(long firstDelay, long delay, boolean loopFixed,
            SysFunctionProcessor p) {
        super(firstDelay, delay, loopFixed);
        this.sysFunctionProcessor = p;
    }

    @Override
    public void run() {
        try {
            this.sysFunctionProcessor.checkPerSecond();
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }
}
