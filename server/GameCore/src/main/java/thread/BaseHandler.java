package thread;



/**
 * handler(命令单元)基类
 */
public abstract class BaseHandler {
    /** 进入队列时间 **/
    protected long time;

    /** 执行结果 用于回调类型的hanler **/
    protected String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    /**
     * handler 执行接口
     */
    public abstract void action() throws Exception;
}
