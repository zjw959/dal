package utils;

import java.util.UUID;

import server.ServerConfig;

public class IdGenerator {


    public static  long getUniqueId() {
        return IdCreator.getUniqueId(ServerConfig.getInstance().getServerId(),
                ServerConfig.getInstance().getSpecialId());
    }

    public static  long getLogId() {
        return IdCreator.getUniqueId(ServerConfig.getInstance().getServerId(),
                ServerConfig.getInstance().getSpecialId());
    }

    // public static long getItemId() {
    // return IdCreator.getUniqueId(ServerConfig.getInstance().getServerId(),
    // ServerConfig.getInstance().getSpecialId());
    // }

    public static long getItemId(int superType) {
        return IdItemCreator.getItemId(ServerConfig.getInstance().getServerId(), superType);
    }

    /**
     * 解析id包含的服务器id
     * 
     * @param uniqueId
     * @return
     */
    public static int parseServerId(long uniqueId) {
        return IdCreator.parseServerId(uniqueId);
    }


    /**
     * 根据uuid生成id <br>
     * 无规则可言
     * 
     * @return
     */
    public static long getLongByUUID() {
        String toString = getUUID();
        byte[] bytes = toString.getBytes();
        long num = 0;
        for (int ix = 0; ix < 7; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        return num;
    }

    /**
     * 根据uuid生成id <br>
     * 无规则可言
     * 
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        System.out.println(getLongByUUID());
    }
}
