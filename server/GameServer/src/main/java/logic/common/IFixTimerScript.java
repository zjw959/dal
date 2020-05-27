package logic.common;

import script.IScript;

/**
 * 公关方法脚本文件接口
 */
public interface IFixTimerScript extends IScript {
    public abstract void second();

    public abstract void minute();

    public abstract void hour();
}
