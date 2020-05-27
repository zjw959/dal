package javascript.common;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.map.MapCacheManager;

import logic.character.PlayerManager;
import logic.character.PlayerViewService;
import logic.chat.ChatService;
import logic.common.ICommonScript;
import logic.constant.EScriptIdDefine;
import logic.friend.FriendService;
import logic.login.service.LoginCheckService;
import logic.login.service.LoginService;
import net.codec.ExternalTcpDecoder;
import net.codec.ExternalTcpEncoder;
import redis.service.ERedisType;
import server.GameServer;
import server.MessageDispatchService;
import thread.BaseProcessor;
import thread.log.LogProcessor;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessorManager;
import utils.SpringContextUtils;
import utils.javaManagement.GarbageCollectorInfo;
import utils.javaManagement.MemoryInformation;

public class CommonScript implements ICommonScript {
    private static final Logger LOGGER = Logger.getLogger(CommonScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.COMMON_SCRIPT.Value();
    }

    @Override
    public void printSysInfo() {
        if (GameServer.getInstance().isPrintQueueInfo()) {
            try {
                Runtime runtime = Runtime.getRuntime();
                long freeMem = runtime.freeMemory();
                long totalMemory = runtime.totalMemory();
                long tMemory = ((totalMemory) / (1024 * 1024));
                long fMemory = ((freeMem) / (1024 * 1024));
                long uMemory = tMemory - fMemory;

                String name = ManagementFactory.getRuntimeMXBean().getName();
                String pid = name.split("@")[0];

                MemoryInformation memoryInformation = new MemoryInformation();

                int springType = ERedisType.VIEW.getSpringConextType().getType();
                MapCacheManager cacheManager = (MapCacheManager) SpringContextUtils
                        .getBeanByName(springType, "localCacheManager");
                int localViewCacheSize = cacheManager.getCacheSize();

                CacheHandler cacheHandler =
                        (CacheHandler) SpringContextUtils.getBeanByName(springType, "cacheHandler");
                int autoLoadCacheSize = cacheHandler.getAutoLoadHandler().getSize();

                GarbageCollectorInfo collectorInfo = new GarbageCollectorInfo();
                collectorInfo.collectGC();
                long fgctime = collectorInfo.getM_lastFullgcCount();
                long ygctime = collectorInfo.getM_lastYounggcCount();

                double oldPer = memoryInformation.getUsedOldGenPercentage();
                DecimalFormat df = new DecimalFormat("#.#");

                LOGGER.info(new StringBuilder()
                        .append("pid:" + pid + " totalMemory:[" + tMemory + "]MB" + " freeMemory:["
                                + fMemory + "]MB" + " usedMemory:[" + uMemory + "]MB")
                        .append(" oldPer:" + df.format(oldPer) + "%")
                        .append(" directByteSize:["
                                + Arrays.toString(MemoryInformation.dirMemForBits()) + "]MB")
                        .append(" directNettySize:[" + (MemoryInformation.nettyDirectMem()) + "]MB")
                        .append(" nonHeapSize:[" + memoryInformation.getUsedNonHeapMemory() + "]MB")
                        .append(" ygcTimes:" + ygctime + " cms(full)GC:" + fgctime)
                        .append(". 服务器接收消息:[").append(ExternalTcpDecoder.num).append("],服务器发送消息:[")
                        .append(ExternalTcpEncoder.num).append("],当前在线人数:[")
                        .append(Arrays.toString(PlayerManager.getPlayerNum()))
                        .append(" ,登陆人数:[" + LoginService.getInstance().ctxs.size())
                        .append("],排队人数:[" + LoginCheckService.getInstance().getSize())
                        .append("] 缓存的推荐数量:[" + FriendService.getInstance().getMapSize())
                        .append("] 缓存的view数量:[" + localViewCacheSize)
                        .append("] autoLoadCache数量:[" + autoLoadCacheSize).append("],分发线程队列数量:[")
                        .append(MessageDispatchService.getInstance().getProcess().getQueueSize())
                        .append("],[")
                        .append(MessageDispatchService.getInstance().getProcess().getAloneNum())
                        .append("],登陆线程队列数量:[")
                        .append(LoginService.getInstance().getProcess().getQueueSize()).append(",")
                        .append(LoginService.getInstance().getProcess().getAloneNum())
                        .append("],验证线程队列数量:[")
                        .append(LoginCheckService.getInstance().getProcess().getQueueSize())
                        .append(",")
                        .append(LoginCheckService.getInstance().getProcess().getAloneNum())
                        .append("],回存线程队列数量:[size:")
                        .append(Arrays
                                .toString(PlayerDBProcessorManager.getInstance().totalLineSize()))
                        .append("],[exce:")
                        .append(Arrays
                                .toString(PlayerDBProcessorManager.getInstance().totalAloneCount()))
                        .append("],[iSize:")
                        .append(PlayerDBProcessorManager.getInstance().getInsertSize())
                        .append("],[sSize:")
                        .append(PlayerDBProcessorManager.getInstance().getSelectSize())
                        .append("],[uSize:")
                        .append(PlayerDBProcessorManager.getInstance().getUpdateSize())
                        .append("],[uCount:")
                        .append(PlayerDBProcessorManager.getInstance().getDoUpdateCount())
                        .append(",")
                        .append(PlayerDBProcessorManager.getInstance().getDelayUpdateCount())
                        .append(",").append(PlayerProcessorManager.getInstance().getSingleDBTime())
                        // .append("],主线程队列数量:[")
                        // .append(GloablService.getInstance().getProcess().getQueueSize())
                        // .append("],[")
                        // .append(GloablService.getInstance().getProcess().getAloneNum())
                        .append("],").append("],聊天线程队列数量:[")
                        .append(ChatService.getInstance().getProcess().getQueueSize())
                        .append("],玩家信息线程队列数量:[")
                        .append(PlayerViewService.getInstance().getProcess().getQueueSize())
                        .append("," + PlayerViewService.getInstance().getProcess().getAloneNum())
                        .append("],玩家线程队列数量:")
                        .append(Arrays
                                .toString(PlayerProcessorManager.getInstance().totalLineSize()))
                        .append("],日志线程队列数量:[").append(LogProcessor.getInstance().getQueueSize())
                        .append(",").append(LogProcessor.getInstance().getAloneNum()).append("]")
                        .append(",线程总处理消息:[").append(BaseProcessor.getExecNum()).append("]")
                        .toString());
            } catch (Exception e) {
                LOGGER.info("CMessageNumInfoProcessor exception", e);
            }
        }
    }

}

