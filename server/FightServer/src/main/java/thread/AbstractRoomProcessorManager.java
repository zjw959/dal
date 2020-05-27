package thread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import thread.timer.TimeEventProcessor;
import thread.timer.TimerEvent;

public class AbstractRoomProcessorManager {
    private final Logger log = Logger.getLogger(AbstractRoomProcessorManager.class);

    private final Map<Integer, AbstractRoomProcessor> idRoomProcessors = new ConcurrentHashMap<>();

    private int processorCount = 0;

    private int lastChooseProcessor;

    protected AbstractRoomProcessorManager() {

    }

    protected AbstractRoomProcessorManager(int processorCount, Class classz) {
        try {
            initRoomProcessor(processorCount, classz);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.error(e);
            System.exit(1);
        }
    }

    protected AbstractRoomProcessorManager(Class classz) {
        try {
            initRoomProcessor(classz);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            log.error(e);
            System.exit(1);
        }
    }

    private void initRoomProcessor(int processorCount, Class classz)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        log.info(classz.getSimpleName() + ":create " + processorCount + " RoomLine");
        for (int i = 0; i < processorCount; ++i) {
            Constructor<AbstractRoomProcessor> constructor = classz.getConstructor(int.class);
            this.idRoomProcessors.put(i, constructor.newInstance(i));
        }
    }

    private void initRoomProcessor(Class classz)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        this.processorCount = Runtime.getRuntime().availableProcessors() - 6; // 线数量 = CPU核心数 - 2
        if (this.processorCount <= 0) {
            log.warn("processorCount <= 0, processorCount:" + processorCount
                    + ",realProcessorCountSize:" + Runtime.getRuntime().availableProcessors());
            this.processorCount = 1;
        }
        initRoomProcessor(this.processorCount, classz);
    }

    /**
     * 为房间线程注册TickTimer
     */
    public void registerRoomLineTickTimer(long firstDelay, long delay, boolean loopFixed) {
        TimeEventProcessor.getInstance().addEvent(new TimerEvent(firstDelay, delay, loopFixed) {
            @Override
            public void run() {
                for (Entry<Integer, AbstractRoomProcessor> entry : idRoomProcessors.entrySet()) {
                    int processorId = entry.getKey();
                    AbstractRoomProcessor roomProcessor = entry.getValue();
                    addCommand(processorId, new TickHandler(roomProcessor));
                }
            }
        });
    }

    public void stop() {
        List<AbstractRoomProcessor> abstractRoomProcessors = new ArrayList<>();
        abstractRoomProcessors.addAll(idRoomProcessors.values());
        for (AbstractRoomProcessor processor : abstractRoomProcessors) {
            processor.stop();
        }
    }

    public AbstractRoomProcessor getRoomProcessor(int processorId) {
        return idRoomProcessors.get(processorId);
    }

    public Collection<AbstractRoomProcessor> getAllProcessor() {
        return idRoomProcessors.values();
    }

    public void addCommand(int processorId, BaseHandler baseHandler) {
        AbstractRoomProcessor roomProcessor = idRoomProcessors.get(processorId);
        if (roomProcessor != null) {
            roomProcessor.executeHandler(baseHandler);
        } else {
            log.error("cannot find roomProcessor, processorId = " + processorId);
        }
    }

    /**
     * 随机选线
     *
     * @return
     */
    public AbstractRoomProcessor randomLine() {
        int randomLineId = RandomUtils.nextInt() % idRoomProcessors.size();
        return idRoomProcessors.get(randomLineId);
    }

    /**
     * 轮询选线
     * 
     * @return
     */
    public AbstractRoomProcessor chooseLineBySequence() {
        int lineId = Math.abs(lastChooseProcessor++) % this.idRoomProcessors.size();
        return this.idRoomProcessors.get(lineId);
    }

    public class TickHandler extends BaseHandler {
        AbstractRoomProcessor roomProcessor;

        public TickHandler(AbstractRoomProcessor roomProcessor) {
            this.roomProcessor = roomProcessor;
        }

        @Override
        public void action() {
            this.roomProcessor.tick();
        }
    }
}
