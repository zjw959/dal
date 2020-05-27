package utils.snowflake;

import java.util.Date;

public class IDGenerator {

    private static class DEFAULT {
        private static final IDGenerator provider = new IDGenerator();
    }

    public static IDGenerator getDefault() {
        return DEFAULT.provider;
    }

    private static IdWorker idWorker;

    public synchronized void initIdWorker(int serverId) {
        if (idWorker != null) {
            throw new RuntimeException("idWorker!=null!");
        }
        idWorker = new IdWorker(serverId);
    }

    public long nextId() throws Exception {
        return idWorker.nextId();
    }

    public static void main(String[] args) {
        System.out.println("" + new Date().getTime());
    }

}
