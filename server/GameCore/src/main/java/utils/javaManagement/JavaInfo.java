package utils.javaManagement;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavaInfo {

    public static Map<String, Number> collectThreadInfo() {
        final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        Map<String, Number> map = new LinkedHashMap<String, Number>();
        map.put("jvm.thread.count", threadBean.getThreadCount());
        map.put("jvm.thread.daemon.count", threadBean.getDaemonThreadCount());
        map.put("jvm.thread.totalstarted.count", threadBean.getTotalStartedThreadCount());
        ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadBean.getAllThreadIds());

        int newThreadCount = 0;
        int runnableThreadCount = 0;
        int blockedThreadCount = 0;
        int waitThreadCount = 0;
        int timeWaitThreadCount = 0;
        int terminatedThreadCount = 0;

        if (threadInfos != null) {
            for (ThreadInfo threadInfo : threadInfos) {
                if (threadInfo != null) {
                    switch (threadInfo.getThreadState()) {
                        case NEW:
                            newThreadCount++;
                            break;
                        case RUNNABLE:
                            runnableThreadCount++;
                            break;
                        case BLOCKED:
                            blockedThreadCount++;
                            break;
                        case WAITING:
                            waitThreadCount++;
                            break;
                        case TIMED_WAITING:
                            timeWaitThreadCount++;
                            break;
                        case TERMINATED:
                            terminatedThreadCount++;
                            break;
                        default:
                            break;
                    }
                } else {
                    /*
                     * If a thread of a given ID is not alive or does not exist, the corresponding
                     * element in the returned array will, contain null,because is mut exist ,so the
                     * thread is terminated
                     */
                    terminatedThreadCount++;
                }
            }
        }

        map.put("jvm.thread.new.count", newThreadCount);
        map.put("jvm.thread.runnable.count", runnableThreadCount);
        map.put("jvm.thread.blocked.count", blockedThreadCount);
        map.put("jvm.thread.waiting.count", waitThreadCount);
        map.put("jvm.thread.time_waiting.count", timeWaitThreadCount);
        map.put("jvm.thread.terminated.count", terminatedThreadCount);

        long[] ids = threadBean.findDeadlockedThreads();
        map.put("jvm.thread.deadlock.count", ids == null ? 0 : ids.length);

        return map;
    }

    public static Map<String, Number> collectClassLoadingInfo() {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        Map<String, Number> map = new LinkedHashMap<String, Number>();

        map.put("jvm.classloading.loaded.count", classLoadingMXBean.getLoadedClassCount());
        map.put("jvm.classloading.totalloaded.count",
                classLoadingMXBean.getTotalLoadedClassCount());
        map.put("jvm.classloading.unloaded.count", classLoadingMXBean.getUnloadedClassCount());

        return map;
    }

    public static void main(String[] args) {

    }

}
