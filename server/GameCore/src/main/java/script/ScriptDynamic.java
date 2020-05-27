package script;

/**
 * 脚本IDynamicScript容器
 * 
 * 用于描述和管理当前关联的script
 */
public class ScriptDynamic extends AbsScriptBean {

    public ScriptDynamic(String name, IDynamicScript script, String sign) {
        super(name, sign);
        this.m_script = script;
    }

    public IDynamicScript getScript() {
        return m_script;
    }

    /**
     * 脚本实例
     */
    private volatile IDynamicScript m_script;
}
