package javascript;

import script.IScript;

import com.constant.EScriptIdDefine;

/**
 * loadclass检查类
 * 
 * 绝对不要删除 绝对不要删除 绝对不要删除
 * 
 * 非逻辑实现类 检查用
 */
public class LoadCheckScript implements IScript {
    @Override
    public int getScriptId() {
        return EScriptIdDefine.LOADCHECK_SCRIPT.Value();
    }
}
