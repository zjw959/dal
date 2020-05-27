package script;

/**
 * 脚本容器
 * 
 * 用于描述和管理当前关联的script
 */
public class ScriptBean extends AbsScriptBean {

    public ScriptBean(String name, IScript script, String sign) {
        super(name, sign);
        this.m_id = script.getScriptId();
        this.m_script = script;
    }

    public int getId() {
        return m_id;
    }

    public IScript getScript() {
        return m_script;
    }

    /**
     * 脚本ID
     */
    private volatile int m_id;

    /**
     * 脚本实例
     */
    private volatile IScript m_script;
}
