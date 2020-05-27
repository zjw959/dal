package util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IDUtil {

    public static String getCreateId() {
        String id = "";
        // 获取当前时间戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        String temp = sf.format(new Date());
        // 获取6位随机数
        int random = (int) ((Math.random() + 1) * 10000);
        id = temp + random;
        return id;
    }
}
