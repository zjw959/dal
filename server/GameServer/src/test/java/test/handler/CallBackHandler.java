package test.handler;

import thread.base.GameInnerHandler;

public abstract class CallBackHandler extends GameInnerHandler {

    @Override
    public void action() {
        exec();
    }

    public abstract void doCallBack();

    public abstract void exec();


}
