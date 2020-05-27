package script;

public abstract class AbsScriptBean {
    public AbsScriptBean(String name, String sign) {
        this.m_name = name;
        this.m_sign = sign;
        this.isNew = true;
    }

    public String getSign() {
        return m_sign;
    }

    public String getName() {
        return m_name;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setNotNew() {
        isNew = false;
    }

    /**
     * 脚本类名(包含报名)
     */
    private volatile String m_name;

    /**
     * 脚本签名(MD5)，用于区分是否需要重新编译
     */
    private volatile String m_sign;

    private volatile boolean isNew;
}
