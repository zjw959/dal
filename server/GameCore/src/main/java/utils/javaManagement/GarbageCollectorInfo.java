package utils.javaManagement;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GarbageCollectorInfo {

    private long m_lastGcCount = 0;

    private long m_lastGcTime = 0;

    private long m_lastFullgcTime = 0;

    private long m_lastFullgcCount = 0;

    private long m_lastYounggcTime = 0;

    private long m_lastYounggcCount = 0;

    public long getM_lastGcCount() {
        return this.m_lastGcCount;
    }

    public long getM_lastGcTime() {
        return this.m_lastGcTime;
    }

    public long getM_lastFullgcTime() {
        return this.m_lastFullgcTime;
    }

    public long getM_lastFullgcCount() {
        return this.m_lastFullgcCount;
    }

    public long getM_lastYounggcTime() {
        return this.m_lastYounggcTime;
    }

    public long getM_lastYounggcCount() {
        return this.m_lastYounggcCount;
    }

    private Set<String> younggcAlgorithm = new LinkedHashSet<String>() {
        private static final long serialVersionUID = -4970284659158304450L;

        {
            add("Copy");
            add("ParNew");
            add("PS Scavenge");
            add("G1 Young Generation");
        }
    };

    private Set<String> oldgcAlgorithm = new LinkedHashSet<String>() {
        private static final long serialVersionUID = -5928912186336375797L;

        {
            add("MarkSweepCompact");
            add("PS MarkSweep");
            add("ConcurrentMarkSweep");
            add("G1 Old Generation");
        }
    };

    public Map<String, Number> collectGC() {
        long gcCount = 0;
        long gcTime = 0;
        long oldGCount = 0;
        long oldGcTime = 0;
        long youngGcCount = 0;
        long youngGcTime = 0;
        Map<String, Number> map = new LinkedHashMap<>();

        for (final GarbageCollectorMXBean garbageCollector : ManagementFactory
                .getGarbageCollectorMXBeans()) {

            gcTime += garbageCollector.getCollectionTime();
            gcCount += garbageCollector.getCollectionCount();
            String gcAlgorithm = garbageCollector.getName();

            if (younggcAlgorithm.contains(gcAlgorithm)) {
                youngGcTime += garbageCollector.getCollectionTime();
                youngGcCount += garbageCollector.getCollectionCount();
            } else if (oldgcAlgorithm.contains(gcAlgorithm)) {
                oldGcTime += garbageCollector.getCollectionTime();
                oldGCount += garbageCollector.getCollectionCount();
            }
        }

        //
        // GC实时统计信息
        //
        map.put("jvm.gc.count", gcCount - m_lastGcCount);
        map.put("jvm.gc.time", gcTime - m_lastGcTime);
        final long fullGcCount = oldGCount - m_lastFullgcCount;
        map.put("jvm.fullgc.count", fullGcCount);
        map.put("jvm.fullgc.time", oldGcTime - m_lastFullgcTime);
        map.put("jvm.younggc.count", youngGcCount - m_lastYounggcCount);
        map.put("jvm.younggc.time", youngGcTime - m_lastYounggcTime);

        if (youngGcCount > m_lastYounggcCount) {
            map.put("jvm.younggc.meantime",
                    (youngGcTime - m_lastYounggcTime) / (youngGcCount - m_lastYounggcCount));
        } else {
            map.put("jvm.younggc.meantime", 0);
        }

        //
        // GC增量统计信息
        //
        m_lastGcCount = gcCount;
        m_lastGcTime = gcTime;
        m_lastYounggcCount = youngGcCount;
        m_lastYounggcTime = youngGcTime;
        m_lastFullgcCount = oldGCount;
        m_lastFullgcTime = oldGcTime;

        return map;
    }
}
