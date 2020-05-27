package utils;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @Description 玩家id转换器（目的：不让玩家通过id判断出我们实际有多少用户，目前支持1亿-8.9亿范围内的id）
 * @author LiuJiang
 * @date 2018年8月6日 下午6:50:35
 *
 */
public class PlayerIdAdaptor {
    /** 数字互换规则src-数组下标 tar-下标对应值 */
    private static final int[] replacesNum = {9, 5, 3, 6, 8, 2, 7, 1, 4, 0};// 让9替换0是为了让取值范围更广，因为最高位不能替换为0
    /** 位置互换规则src-数组0 tar-数组1（位置1代表个位，2代表十位，依次类推） */
    private static final int[][] replacesSite = { {5, 6, 7}, {3, 2, 4}};

    /**
     * playerId转换
     * 
     * @param playerId 原始id
     * @return
     */
    public static int change(int playerId) {
        int len = String.valueOf(playerId).length();
        int value = 0;
        int[] arr = new int[len];
        // 先替换数字
        for (int i = len - 1; i >= 0; i--) {
            int mod = (int) Math.pow(10, i + 1);// 取模
            int bottom = (int) Math.pow(10, i);// 取余
            int k = playerId % mod / bottom;// 当前位上的实际数值
            arr[len - i - 1] = replacesNum[k];
            // System.out.println("--arr[" + (len - i - 1) + "]=" + arr[len - i - 1]);
        }
        // 再替换位置
        for (int i = 0; i < len; i++) {
            int site = len - i;// i对应的位置
            for (int j = 0; j < replacesSite[0].length; j++) {
                if (site == replacesSite[0][j]) {
                    int temp = arr[i];
                    int tarIndex = len - replacesSite[1][j];
                    arr[i] = arr[tarIndex];
                    arr[tarIndex] = temp;
                    // System.out.println("---替换 srcSite=" + replacesSite[0][j] + " tarSite="
                    // + replacesSite[1][j] + "     srcIndex=" + i + " tarIndex=" + tarIndex);
                    break;
                }
            }
            value += arr[i] * ((int) Math.pow(10, len - i - 1));
        }
        return value;
    }

    public static void main(String[] args) {
        int start = 123456789;// 起始值
        int count = 1000;// 执行次数
        long t = System.currentTimeMillis();
        Set<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < count; i++) {
            int v = change(start + i);
            if (set.contains(v)) {
                System.err.println("--有重复-v=" + v);
                break;
            } else {
                set.add(v);
                System.out.println(Thread.currentThread().getName() + " v= " + v);
            }
            if ((i + 1) % 10000 == 0) {
                System.out.println(Thread.currentThread().getName() + " 执行到第 " + (i + 1) / 10000
                        + " 万次");
            }
        }
        System.out.println("---执行完毕=" + (System.currentTimeMillis() - t) + "毫秒");
    }
}
