package utils.javaManagement;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

import utils.ExceptionEx;

public class MemoryInformation {

    private transient static final Logger LOGGER = Logger.getLogger(MemoryInformation.class);


    // usedMemory 是heap使用内存 (eden+survivor+old)
    private final long m_usedMemory;

    // maxMemory 是heap最大内存
    private final long m_maxMemory;

    // usedOldGen "Old Gen"使用内存
    private final long m_usedOldGen;

    // maxOldGen "Old Gen"最大内存
    private final long m_maxOldGen;

    // usedPermGen "Perm Gen"使用内存
    private final long m_usedPermGen;

    // maxPermGen "Perm Gen"最大内存
    private final long m_maxPermGen;

    // usedEdenSpace "Eden Space"使用内存
    private final long m_usedEdenSpace;

    // maxEdenSpace "Eden Space"最大内存
    private final long m_maxEdenSpace;

    // usedSurvivorSpace "Survivor Space"使用内存
    private final long m_usedSurvivorSpace;

    // maxSurvivorSpace "Survivor Space"最大内存
    private final long m_maxSurvivorSpace;

    private final long m_usedNonHeapMemory;

    private final long m_maxNonHeapMemory;

    private MBeanServer m_mbeanServer = ManagementFactory.getPlatformMBeanServer();

    private static final String DIRECT_BUFFER_MBEAN = "java.nio:type=BufferPool,name=direct";

    private static final String MAPPED_BUFFER_MBEAN = "java.nio:type=BufferPool,name=mapped";

    public MemoryInformation() {
        m_usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        m_maxMemory = Runtime.getRuntime().maxMemory();
        final MemoryPoolMXBean permGenMemoryPool = getPermGenMemoryPool();
        if (permGenMemoryPool != null) {
            final MemoryUsage usage = permGenMemoryPool.getUsage();
            m_usedPermGen = usage.getUsed();
            m_maxPermGen = usage.getMax();
        } else {
            m_usedPermGen = 0;
            m_maxPermGen = 0;
        }
        final MemoryPoolMXBean oldGenMemoryPool = getOldGenMemoryPool();
        if (oldGenMemoryPool != null) {
            final MemoryUsage usage = oldGenMemoryPool.getUsage();
            m_usedOldGen = usage.getUsed();
            m_maxOldGen = usage.getMax();
        } else {
            m_usedOldGen = 0;
            m_maxOldGen = 0;
        }

        final MemoryPoolMXBean edenSpaceMemoryPool = getEdenSpacePool();
        if (edenSpaceMemoryPool != null) {
            final MemoryUsage usage = edenSpaceMemoryPool.getUsage();
            m_usedEdenSpace = usage.getUsed();
            m_maxEdenSpace = usage.getMax();
        } else {
            m_usedEdenSpace = 0;
            m_maxEdenSpace = 0;
        }

        final MemoryPoolMXBean survivorSpacePool = getSurvivorSpaceMemoryPool();
        if (survivorSpacePool != null) {
            final MemoryUsage usage = survivorSpacePool.getUsage();
            m_usedSurvivorSpace = usage.getUsed();
            m_maxSurvivorSpace = usage.getMax();
        } else {
            m_usedSurvivorSpace = 0;
            m_maxSurvivorSpace = 0;
        }

        final MemoryUsage nonHeapMemoryUsage =
                ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();

        m_usedNonHeapMemory = nonHeapMemoryUsage.getUsed();
        m_maxNonHeapMemory = nonHeapMemoryUsage.getMax();
    }

    public long getMaxEdenSpace() {
        return m_maxEdenSpace / (1024 * 1024);
    }

    public long getMaxMemory() {
        return m_maxMemory / (1024 * 1024);
    }

    public long getMaxNonHeapMemory() {
        return m_maxNonHeapMemory / (1024 * 1024);
    }

    public long getMaxOldGen() {
        return m_maxOldGen / (1024 * 1024);
    }

    public long getMaxPermGen() {
        return m_maxPermGen / (1024 * 1024);
    }

    public long getMaxSurvivorSpace() {
        return m_maxSurvivorSpace / (1024 * 1024);
    }

    private MemoryPoolMXBean getEdenSpacePool() {
        for (final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memoryPool.getName().endsWith("Eden Space")) {
                return memoryPool;
            }
        }
        return null;
    }

    private MemoryPoolMXBean getOldGenMemoryPool() {
        for (final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memoryPool.getName().endsWith("Old Gen")) {
                return memoryPool;
            }
        }
        return null;
    }

    private MemoryPoolMXBean getPermGenMemoryPool() {
        for (final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memoryPool.getName().endsWith("Perm Gen")) {
                return memoryPool;
            }
        }
        return null;
    }

    private MemoryPoolMXBean getSurvivorSpaceMemoryPool() {
        for (final MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (memoryPool.getName().endsWith("Survivor Space")) {
                return memoryPool;
            }
        }
        return null;
    }

    public long getUsedDirectBufferSize() {
        long directBufferSize = 0;
        try {
            ObjectName directPool = new ObjectName(DIRECT_BUFFER_MBEAN);
            directBufferSize = (Long) m_mbeanServer.getAttribute(directPool, "MemoryUsed");
            // MBeanInfo info = m_mbeanServer.getMBeanInfo(directPool);
            // for (MBeanAttributeInfo i : info.getAttributes()) {
            // System.out.println(
            // i.getName() + ":" + m_mbeanServer.getAttribute(directPool, i.getName()));
            // }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return directBufferSize;
    }

    public long getUsedEdenSpace() {
        return m_usedEdenSpace / (1024 * 1024);
    }

    public double getUsedEdenSpacePercentage() {
        if (m_usedEdenSpace > 0 && m_maxEdenSpace > 0) {
            return 100d * m_usedEdenSpace / m_maxEdenSpace;
        }
        return 0d;
    }

    public long getUsedMappedSize() {
        long mappedBufferSize = 0;
        try {
            ObjectName directPool = new ObjectName(MAPPED_BUFFER_MBEAN);
            mappedBufferSize = (Long) m_mbeanServer.getAttribute(directPool, "MemoryUsed");
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return mappedBufferSize;
    }

    public long getUsedMemory() {
        return m_usedMemory / (1024 * 1024);
    }

    public double getUsedMemoryPercentage() {
        return 100d * m_usedMemory / m_maxMemory;
    }

    public long getUsedNonHeapMemory() {
        return m_usedNonHeapMemory / (1024 * 1024);
    }

    public double getUsedNonHeapPercentage() {
        if (m_usedNonHeapMemory > 0 && m_maxNonHeapMemory > 0) {
            return 100d * m_usedNonHeapMemory / m_maxNonHeapMemory;
        }
        return 0d;
    }

    public long getUsedOldGen() {
        return m_usedOldGen / (1024 * 1024);
    }

    public double getUsedOldGenPercentage() {
        if (m_usedOldGen > 0 && m_maxOldGen > 0) {
            return 100d * m_usedOldGen / m_maxOldGen;
        }
        return 0d;
    }

    public long getUsedPermGen() {
        return m_usedPermGen / (1024 * 1024);
    }

    public double getUsedPermGenPercentage() {
        if (m_usedPermGen > 0 && m_maxPermGen > 0) {
            return 100d * m_usedPermGen / m_maxPermGen;
        }
        return 0d;
    }

    public long getUsedSurvivorSpace() {
        return m_usedSurvivorSpace / (1024 * 1024);
    }

    public double getUsedSurvivorSpacePercentage() {
        if (m_usedSurvivorSpace > 0 && m_maxSurvivorSpace > 0) {
            return 100d * m_usedSurvivorSpace / m_maxSurvivorSpace;
        }
        return 0d;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UsedMemory:[" + this.getUsedMemory());
        sb.append("],MaxMemory:[" + this.getMaxMemory());
        sb.append("],UsedNon:[" + this.getUsedNonHeapMemory());
        sb.append("],MaxNon:[" + this.getMaxNonHeapMemory());
        sb.append("],UsedEden:[" + this.getUsedEdenSpace());
        sb.append("],MaxEden:[" + this.getMaxEdenSpace());
        sb.append("],UsedSurvivor:[" + this.getUsedSurvivorSpace());
        sb.append("],MaxSurvivor:[" + this.getMaxSurvivorSpace());
        sb.append("],UsedOldGen:[" + this.getUsedOldGen());
        sb.append("],MaxOldGen:[" + this.getMaxOldGen());
        sb.append("],UsedPerm:[" + this.getUsedPermGen());
        sb.append("],MaxPerm:[" + this.getMaxPermGen());
        sb.append("],directBuff:[" + (int) (this.getUsedDirectBufferSize() / (1024 * 1024)));
        sb.append("],mappedSize:[" + (int) (this.getUsedMappedSize() / (1024 * 1024)));
        sb.append("],\nUsedMemoryPercentage:[" + (int) (this.getUsedMemoryPercentage()));
        sb.append("%],UsedNonHeapPercentage:[" + (int) (this.getUsedNonHeapPercentage()));
        sb.append("%],UsedEdenSpacePercentage:[" + (int) (this.getUsedEdenSpacePercentage()));
        sb.append(
                "%],UsedSurvivorSpacePercentage:[" + (int) (this.getUsedSurvivorSpacePercentage()));
        sb.append("%],UsedOldGenPercentage:[" + (int) (this.getUsedOldGenPercentage()));
        sb.append("%],UsedPermGenPercentage:[" + (int) (this.getUsedPermGenPercentage()));
        sb.append("%]");
        return sb.toString();
    }

    public static long nettyDirectMem() {
        try {
            Class c = Class.forName("io.netty.util.internal.PlatformDependent");
            Field DIRECT_MEMORY_COUNTER = c.getDeclaredField("DIRECT_MEMORY_COUNTER");
            DIRECT_MEMORY_COUNTER.setAccessible(true);
            AtomicLong direct_memory_counter_long = (AtomicLong) DIRECT_MEMORY_COUNTER.get(null);
            return direct_memory_counter_long.get() / 1024 / 1024;
        } catch (Exception e) {
            ExceptionEx.e2s(e);
        }
        return -1;
    }


    public static long[] dirMemForBits() throws ClassNotFoundException, NoSuchFieldException,
            SecurityException, IllegalArgumentException, IllegalAccessException {
        Class c = Class.forName("java.nio.Bits");
        Field maxMemory = c.getDeclaredField("maxMemory");
        maxMemory.setAccessible(true);
        Field reservedMemory = c.getDeclaredField("reservedMemory");
        reservedMemory.setAccessible(true);
        Field totalCapacity = c.getDeclaredField("totalCapacity");
        totalCapacity.setAccessible(true);

        Field count = c.getDeclaredField("count");
        count.setAccessible(true);

        Long maxMemoryValue = (Long) maxMemory.get(null);
        AtomicLong reservedMemoryValue = (AtomicLong) reservedMemory.get(null);
        AtomicLong totalCapacityValue = (AtomicLong) totalCapacity.get(null);
        AtomicLong countValue = (AtomicLong) count.get(null);
        // System.out.println("maxMemoryValue:" + (maxMemoryValue / (1024 * 1024)) + "m");
        // System.out.println(
        // "reservedMemoryValue:" + (reservedMemoryValue.get() / (1024 * 1024)) + "m");
        // System.out
        // .println("totalCapacityValue:" + (totalCapacityValue.get() / (1024 * 1024)) + "m");
        // System.out.println("countValue:" + (countValue.get()));
        // System.out.println("----------");

        long[] arrays = new long[4];
        arrays[0] = maxMemoryValue / (1024 * 1024);
        arrays[1] = reservedMemoryValue.get() / (1024 * 1024);
        arrays[2] = totalCapacityValue.get() / (1024 * 1024);
        arrays[3] = countValue.get();
        return arrays;
    }

    public static void main(String[] args) throws NoSuchFieldException, SecurityException,
            IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        System.out.println(new MemoryInformation().toString());
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 1024);// 1G
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024 * 1024 * 1024);// 2G

        System.out.println(new MemoryInformation().toString());
        System.out.println(Arrays.toString(dirMemForBits()));

        ((sun.nio.ch.DirectBuffer) buffer).cleaner().clean();
        ((sun.nio.ch.DirectBuffer) buffer2).cleaner().clean();

        System.out.println(Arrays.toString(dirMemForBits()));

        System.out.println(new MemoryInformation().toString());

        Field f = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        sun.misc.Unsafe us = (sun.misc.Unsafe) f.get(null);
        long id = us.allocateMemory(1024 * 1024 * 1024);

        us.setMemory(id, 1024 * 1024 * 1024, (byte) 0);

        System.out.println(new MemoryInformation().toString());
        System.out.println(Arrays.toString(dirMemForBits()));

        System.out.println(new MemoryInformation().toString());
    }
}
