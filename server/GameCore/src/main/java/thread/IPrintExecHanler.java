package thread;

public interface IPrintExecHanler {

    void waitPrintAction(BaseHandler handler, long waitTime);

    void doPrintAction(BaseHandler handler, long doTime);

    void sizePrintAction(BaseHandler handler, long now, int size);
}
