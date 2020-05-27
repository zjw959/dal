package javascript.common;

import org.apache.log4j.Logger;

import logic.common.IFixTimerScript;
import logic.constant.EScriptIdDefine;

public class FixTimerScript implements IFixTimerScript {
    private static final Logger LOGGER = Logger.getLogger(FixTimerScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.COMMON_FIX_TIMER_SCRIPT.Value();
    }

    @Override
    public void second() {
    }

    @Override
    public void minute() {
    }

    @Override
    public void hour() {
    }
}

