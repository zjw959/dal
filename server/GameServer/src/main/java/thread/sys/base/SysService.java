package thread.sys.base;

public abstract class SysService {
    protected SysFunctionProcessor sysFunctionProcessor = null;

    public void setProcess(SysFunctionProcessor process) {
        this.sysFunctionProcessor = process;
    }

    public SysFunctionProcessor getProcess() {
        return this.sysFunctionProcessor;
    }
}
