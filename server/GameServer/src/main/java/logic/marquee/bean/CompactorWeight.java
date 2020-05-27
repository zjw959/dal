package logic.marquee.bean;

import java.util.Comparator;

/**
 * 
 * @Description 跑马灯权重排序器
 * @author LiuJiang
 * @date 2018年7月31日 下午8:48:14
 *
 */
public class CompactorWeight implements Comparator<MarqueeInfo> {

    @Override
    public int compare(MarqueeInfo o1, MarqueeInfo o2) {
        if (o1.getWeight() != o2.getWeight()) {
            return o2.getWeight() - o1.getWeight();
        }
        return 0;
    }

    public static CompactorWeight getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        CompactorWeight instance;

        private Singleton() {
            instance = new CompactorWeight();
        }

        CompactorWeight getInstance() {
            return instance;
        }
    }
}
