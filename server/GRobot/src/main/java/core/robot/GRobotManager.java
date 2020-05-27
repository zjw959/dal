package core.robot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import core.event.FunctionType;

public class GRobotManager {

    private static AtomicLong globalRobotId = new AtomicLong(0);

    /**
     * 所有的activeRobots
     */
    private LinkedHashMap<String, RobotThread> robots = new LinkedHashMap<String, RobotThread>();

    private Set<FunctionType> IncludeActionContainer = new HashSet<>();

    public GRobotManager() {

    }

    public synchronized boolean containsAction(FunctionType actionKey) {
        return IncludeActionContainer.contains(actionKey);
    }

    public synchronized boolean exclude(FunctionType actionKey) {
        return IncludeActionContainer.remove(actionKey);
    }

    public synchronized boolean include(FunctionType actionKey) {
        return IncludeActionContainer.add(actionKey);
    }

    public synchronized void refill(Set<FunctionType> hashSet) {
        IncludeActionContainer = new HashSet<>(hashSet);
    }

    public synchronized List<RobotThread> getRobots() {
        List list = new ArrayList<>();
        Collection<RobotThread> robotThreads = robots.values();
        List<RobotThread> robotlist = new ArrayList(robotThreads);
        Collections.shuffle(robotlist);
        int max = 20;
        if (max > robotThreads.size()) {
            max = robotThreads.size();
        }
        int i = 0;
        for (RobotThread robotThread : robotlist) {
            list.add(robotThread);
            i++;
            if (i >= max) {
                break;
            }
        }
        return list;
    }

    public synchronized void addRobot(RobotThread robot) {
        robots.put(robot.getChannel().id().asLongText(), robot);
    }

    public synchronized void remove(String channelId) {
        this.robots.remove(channelId);
    }

    public synchronized void removeRobot(int count) {
        if (count <= 0) {
            return;
        }
        Iterator<Map.Entry<String, RobotThread>> iterator = robots.entrySet().iterator();
        while (iterator.hasNext() && count > 0) {
            Map.Entry<String, RobotThread> kvs = iterator.next();
            kvs.getValue().cancel();
            count--;
            iterator.remove();
        }
    }

    public String uniqueRobotName(String prefix) {
        return prefix + "-" + globalRobotId.incrementAndGet();
    }

    public synchronized int connectedRobotCount() {
        return robots.size();
    }


    public static GRobotManager instance() {
        return Singleton.INSTANCE.getInstance();
    }


    private enum Singleton {
        INSTANCE;
        GRobotManager manager;

        Singleton() {
            this.manager = new GRobotManager();
        }

        GRobotManager getInstance() {
            return manager;
        }
    }
}
