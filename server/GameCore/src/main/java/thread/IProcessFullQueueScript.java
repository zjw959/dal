package thread;

import java.util.concurrent.BlockingQueue;

import script.IScript;

public interface IProcessFullQueueScript extends IScript {
    void process(BlockingQueue<Runnable> queue);
}
