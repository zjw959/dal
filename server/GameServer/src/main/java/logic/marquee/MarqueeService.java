package logic.marquee;

import gm.db.global.bean.MarqueeDBBean;
import gm.utils.MarqueeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import logic.chat.ChatService;
import logic.constant.ConstDefine;
import logic.marquee.bean.CompactorTime;
import logic.marquee.bean.CompactorWeight;
import logic.marquee.bean.MarqueeInfo;

import org.apache.log4j.Logger;

import thread.sys.base.SysService;
import utils.ExceptionEx;

import com.google.common.collect.Lists;

/**
 * 
 * @Description 跑马灯服务类
 * @author LiuJiang
 * @date 2018年7月31日 上午10:54:49
 *
 */
public class MarqueeService extends SysService {
    private static final Logger LOGGER = Logger.getLogger(MarqueeService.class);

    /** 定时获取跑马灯的时间间隔 */
    public static int DELAY = 10 * 1000;
    /** 每条跑马灯之间的最少间隔时间 */
    public static int MIN_INTERVAL = 30 * 1000;
    /** 当前进行中的跑马灯缓存 */
    private List<MarqueeInfo> marquees = new ArrayList<MarqueeInfo>();
    /** 下次tick检查时间 */
    private long nextTickGetTime;
    /** 下次tick检查时间 */
    private long nextTickNoticeTime;
    /** 上一条跑马灯发送时间 */
    private long lastSendTime;

    /** 跑马灯获取tick */
    public void tickGetMarquee() {
        try {
            long now = System.currentTimeMillis();
            MarqueeService.getInstance().setNextTickGetTime(now + MarqueeService.DELAY);
            List<MarqueeDBBean> list = MarqueeUtils.selectExistMarquees();
            if (list.size() > 0) {
                Set<Long> ids = new HashSet<Long>();
                // 检查新增和更新
                for (MarqueeDBBean bean : list) {
                    ids.add(bean.getId());
                    boolean isIn = false;
                    for (MarqueeInfo info : marquees) {
                        if (bean.getId() == info.getId()) {// 更新数据
                            isIn = true;
                            info.setWeight(bean.getWeight());
                            info.setLoop_count(bean.getLoop_count());
                            info.setBody(bean.getBody());
                            info.setInterval_time(bean.getInterval_time());
                            break;
                        }
                    }
                    if (!isIn) {// 有新增
                        marquees.add(new MarqueeInfo(bean));
                    }
                }
                // 检查清理已经被移除的
                for (int i = marquees.size() - 1; i > 0; i--) {
                    MarqueeInfo info = marquees.get(i);
                    if (!ids.contains(Long.valueOf(info.getId()))) {// 更新数据
                        marquees.remove(info);
                    }
                }
                Collections.sort(marquees, CompactorWeight.getInstance());// 根据权重排序
            } else if (marquees.size() > 0) {
                marquees.clear();
            }
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }

    /** 跑马灯通知客户端tick */
    public void tickNoticeMarquee() {
        try {
            if (marquees.size() == 0) {
                return;
            }
            long now = System.currentTimeMillis();
            if (now - lastSendTime < MIN_INTERVAL) {
                return;
            }
            List<MarqueeInfo> runList = Lists.newArrayList();// 同时都到达执行时间的集合
            for (int i = 0; i < marquees.size(); i++) {
                MarqueeInfo info = marquees.get(i);
                if (info.getLoop_count() > 0 && info.getCount() >= info.getLoop_count()) {
                    continue;// 跳过达到循环次数限制的跑马灯
                }
                long ndTime = info.getNextDoTime();
                if (ndTime == 0) {// 设置首次执行时间
                    info.setLastDoTime(now + i);
                    info.setNextDoTime(now + i);// 错开时间
                } else if (now >= ndTime) {
                    runList.add(info);
                }
            }
            if (runList.size() == 0) {
                return;
            }
            if (runList.size() > 1) {
                // 再按最近一次执行时间排序
                Collections.sort(runList, CompactorTime.getInstance());// 根据上次执行时间排序
            }
            MarqueeInfo info = runList.get(0);
            lastSendTime = now;
            info.setLastDoTime(now);
            info.setNextDoTime(info.getNextDoTime() + info.getInterval_time() * 1000);
            if (info.getLoop_count() > 0) {
                info.setCount(info.getCount() + 1);
            }
            // LOGGER.info("---执行跑马灯：" + info.getBody() + " loop:" + info.getLoop_count() +
            // " count:"
            // + info.getCount());
            // 通知在线玩家
            ChatService.getInstance().sendSystemMsg(info.getBody());
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }


    public long getNextTickGetTime() {
        return nextTickGetTime;
    }

    public void setNextTickGetTime(long nextTickGetTime) {
        this.nextTickGetTime = nextTickGetTime;
    }

    public long getNextTickNoticeTime() {
        return nextTickNoticeTime;
    }

    public void setNextTickNoticeTime(long nextTickNoticeTime) {
        this.nextTickNoticeTime = nextTickNoticeTime;
    }

    public List<MarqueeInfo> getMarquees() {
        return marquees;
    }

    public static MarqueeService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        MarqueeService instance;

        private Singleton() {
            instance = new MarqueeService();
        }

        MarqueeService getInstance() {
            return instance;
        }
    }
}
