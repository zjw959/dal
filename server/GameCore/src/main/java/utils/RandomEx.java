package utils;


import java.util.List;
import java.util.Random;

public class RandomEx {
    public static final Random rnd = new Random(System.currentTimeMillis());

    public static final boolean nextBool() {
        return rnd.nextBoolean();
    }

    public static final boolean nextBool(int max, int f) {
        int v = getRandomNumIntMax(max);
        return (v < f);
    }

    public static final <T> T rand(List<?> objs) {
        int size = objs.size();
        if (size < 1)
            return null;
        else if (size == 1)
            return (T) objs.get(0);

        int v = rnd.nextInt(size - 1);
        return (T) objs.get(v);
    }

    /**
     * 包含0,不包含最大值
     * 
     * @param max
     * @return
     */
    public static final int nextInt(int max) {
        if (max < 0)
            return 0;
        return rnd.nextInt(max);
    }

    /**
     * 包含最小值,不包含最大值
     * 
     * @param f
     * @param t
     * @return
     */
    public static final int nextInt(int f, int t) {
        if (t <= f)
            return f;
        return rnd.nextInt(t - f) + f;
    }

    /**
     * 包含最小值和最大值
     * 
     * @param f
     * @param t
     * @return
     */
    public static final int nextIntInclude(int f, int t) {
        if (t + 1 <= f)
            return f;
        return rnd.nextInt(t + 1 - f) + f;
    }

    /**
     * 1-max之间的随机整数(包含)
     * 
     * @return
     */
    public static final int getRandomNumIntMax(int max) {
        return nextIntInclude(1, max);
    }

    /**
     * 1-100之间的随机整数(包含)
     * 
     * @return
     */
    public static final int getRandomNumInt_100() {
        return nextIntInclude(1, 100);
    }

    /**
     * 1-10000之间的随机整数(包含)
     * 
     * @return
     */
    public static final int getRandomNumInt_10000() {
        return nextIntInclude(1, 10000);
    }

    /**
     * 0-1之间的随机小数(包含0,包含1)
     * 
     * @return
     */
    public static final double getRandomNumDou() {
        double v = rnd.nextFloat();
        return v;
    }

    /**
     * 0-1之间的随机小数,精度为小数点后两位(不包含0,包含1)
     * 
     * @return
     */
    public static final double getRandomNumDouble_01() {
        return (double) nextIntInclude(1, 100) / 100;
    }


    /** 取范围随机数：大于等于较小的数，小于较大的数 */
    public static double randomValue(double v1, double v2) {
        if (v1 == v2)
            return v1;
        rnd.setSeed(System.nanoTime());
        if (v1 > v2)
            return rnd.nextDouble() * (v1 - v2) + v2;
        else
            return rnd.nextDouble() * (v2 - v1) + v1;
    }

    /**
     * f是否命中几率(百分比)
     * 
     * @param f
     * @return
     */
    public static final boolean getRandomBoolean100(int f) {
        return (getRandomNumInt_100() <= f);
    }

    /**
     * 是否命中
     * 
     * @param f
     * @return
     */
    public static final boolean getRandomBoolean10000(int f) {
        return getRandomNumInt_10000() <= f;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // double a = getRandomNumDouble_01();
        for (int i = 0; i < 100; i++) {
            System.out.println(getRandomNumInt_10000());
        }
    }

}
