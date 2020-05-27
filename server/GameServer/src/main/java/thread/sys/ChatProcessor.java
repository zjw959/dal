package thread.sys;

import logic.chat.ChatService;
import thread.sys.base.SysFunctionProcessor;

/**
 * 聊天线程
 */
public class ChatProcessor extends SysFunctionProcessor {

    static int coreSize = Runtime.getRuntime().availableProcessors() < 2 ? 1 : 2;

    static {
        coreSize = Runtime.getRuntime().availableProcessors() / 4;
        if (coreSize < 2) {
            coreSize = 1;
        }
    }

    private ChatProcessor() {
        super(ChatProcessor.class.getSimpleName(), coreSize, coreSize);
    }

    @Override
    protected void registerService() {
        services.add(ChatService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
    }
}
