package core.robot;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import UI.window.frame.MainWindow;
import conf.RunConf;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventScanner;
import core.event.FunctionType;
import core.event.IsEvent;

/**
 * 
 * @function 机器人工厂
 */
public final class RobotThreadFactory {
    private RobotThreadFactory() {}

    public static volatile long counter = 0L;

    /**
     * 机器人池
     */
    private static Map<String, RobotThread> robotsMap =
            new ConcurrentHashMap<String, RobotThread>();

    private static Map<String, RobotThread> fightRobotsMap = new ConcurrentHashMap<>();

    /**
     * 初始化机器人
     */
    public static RobotThread createNewRobot(MainWindow window, boolean isSingle) {
        String name = RunConf.robotConf.getPrefixName();
        if (!isSingle) {
            // 轮询最大用户数进行迭代
            if (RunConf.robotConf.getRobotMaxNum() != -1
                    && counter >= RunConf.robotConf.getRobotMaxNum()) {
                counter = 0;
            }

            counter++;
            name += "_" + String.valueOf(counter);
        }

        RobotThread crt = new RobotThread(name, window);
        loadEvents(crt);
        return crt;
    }

    /**
     * 存入机器人池
     * 
     * @param channelId
     * @param cRobotThreads
     */
    public static void putRobot(String channelId, RobotThread cRobotThread) {
        // synchronized (robotsMap) {
        robotsMap.put(channelId, cRobotThread);
        // }
    }

    public static void putFightRobot(String conv, RobotThread cRobotThread) {
        fightRobotsMap.put(conv, cRobotThread);
    }

    /**
     * 存入机器人池
     * 
     * @param channelId
     * @param cRobotThreads
     */
    public static RobotThread removeRobot(String key) {
        // synchronized (robotsMap) {
        return robotsMap.remove(key);
        // }
    }

    public static RobotThread removeFightRobot(String conv) {
        // synchronized (robotsMap) {
        return fightRobotsMap.remove(conv);
        // }
    }

    /**
     * 为机器人加载事件
     * 
     * @param crt
     */
    private static void loadEvents(RobotThread crt) {
        try {
            crt.initEvents(constructEventsMap(EventScanner.getRequestOnceEventClazzs(), crt, true),
                    constructEventsMap(EventScanner.getResponseEventClazzs(), crt, false),
                    constructEventsMap(EventScanner.getSelectedRequestMultipleEventClazzs(), crt),
                    constructEventsMap(EventScanner.getSelectedRequestOnceEventClazzs(), crt));
        } catch (Exception e) {
            Log4jManager.getInstance().error(crt.getWindow(), e);
        }
    }

    /**
     * 获取机器人线程
     * 
     * @param name
     * @return
     */
    public static RobotThread getRobot(String ChannelId) {
        // synchronized (robotsMap) {
        return robotsMap.get(ChannelId);
        // }
    }

    public static RobotThread getFightRobot(String conv) {
        // synchronized (robotsMap) {
        return fightRobotsMap.get(conv);
        // }
    }

    /**
     * 构造单次请求以及响应事件MAP
     * 
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private static Map<Integer, AbstractEvent> constructEventsMap(
            List<Class<? extends AbstractEvent>> clazzs, RobotThread crt, boolean isOrder)
            throws Exception {
        Map<Integer, AbstractEvent> eventMap = isOrder ? new TreeMap<Integer, AbstractEvent>()
                : new HashMap<Integer, AbstractEvent>();
        IsEvent isEvent = null;
        for (Class<? extends AbstractEvent> clazz : clazzs) {
            isEvent = clazz.getAnnotation(IsEvent.class);
            if (null != isEvent) {
                Constructor<? extends AbstractEvent> constructor =
                        clazz.getConstructor(RobotThread.class);
                AbstractEvent event = constructor.newInstance(crt);
                eventMap.put(isEvent.order(), event);
            }
        }
        return eventMap;
    }

    /**
     * 构建可重复请求事件MAP
     * 
     * @param clazzMap
     * @param crt
     * @return
     * @throws Exception
     */
    private static Map<FunctionType, Map<Integer, AbstractEvent>> constructEventsMap(
            Map<FunctionType, List<Class<? extends AbstractEvent>>> clazzMap, RobotThread crt) {
        Iterator<Entry<FunctionType, List<Class<? extends AbstractEvent>>>> iterator =
                clazzMap.entrySet().iterator();
        Map<FunctionType, Map<Integer, AbstractEvent>> eventsMap = new HashMap<>();
        iterator.forEachRemaining(action -> {
            try {
                Map<Integer, AbstractEvent> eventMap =
                        constructEventsMap(action.getValue(), crt, true);
                eventsMap.put(action.getKey(), eventMap);
            } catch (Exception e) {
                Log4jManager.getInstance().error(crt.getWindow(), e);
            }
        });
        return eventsMap;
    }

}
