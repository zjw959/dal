package logic.marquee.bean;

import java.util.Comparator;

/**
 * 
 * @Description 跑马灯时间排序器
 * @author LiuJiang
 * @date 2018年7月31日 下午8:48:14
 *
 */
public class CompactorTime implements Comparator<MarqueeInfo> {

    @Override
    public int compare(MarqueeInfo o1, MarqueeInfo o2) {
        if (o1.getLastDoTime() != o2.getLastDoTime()) {
            return o1.getLastDoTime() > o2.getLastDoTime() ? 1 : -1;
        }
        return 0;
    }

    public static CompactorTime getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        CompactorTime instance;

        private Singleton() {
            instance = new CompactorTime();
        }

        CompactorTime getInstance() {
            return instance;
        }
    }
}
