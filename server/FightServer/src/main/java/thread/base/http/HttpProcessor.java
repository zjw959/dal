package thread.base.http;

import thread.BaseHandler;
import thread.BaseProcessor;

/**
 * Http线程处理器
 */
public class HttpProcessor extends BaseProcessor {

    public HttpProcessor() {
        super(HttpProcessor.class.getSimpleName());
    }

    @Override
    public void executeHandler(BaseHandler handler) {
        super.executeHandler(handler);
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        HttpProcessor processor;

        Singleton() {
            this.processor = new HttpProcessor();
        }

        HttpProcessor getProcessor() {
            return processor;
        }
    }


    /**
     * 获取GlobalProcessor的单例对象
     * 
     * @return
     */
    public static HttpProcessor getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }
}
