import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import server.GameServer;

public class Main {

    public static void main(String[] args) throws IOException {
        // 保存进程Id
        String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
        try (FileWriter writer = new FileWriter("server.pid")) {
            writer.write(pid);
            writer.flush();
        }

        // 启动服务器
        GameServer.getInstance().start(args);
    }
}
