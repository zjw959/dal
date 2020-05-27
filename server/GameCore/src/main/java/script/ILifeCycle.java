package script;

/**
 * func：提供具有生命周期的接口
 * 
 * 这两个接口的异常需要自行处理,不在重载逻辑中抛出中断重载操作.该异常处理有很多二义性,比如无法回滚其他脚本的init和destroy.
 * 
 * 注:一般情况下不鼓励在脚本中放置变量, 除非特殊情况(fix bug), 此接口 实现结合脚本IScript接口复合使用
 * 
 */
public interface ILifeCycle {

    /**
     * 脚本初始化时调用
     */
    void init();

    /**
     * 脚本重载后当前实例被替换的时候调用执行 主要用于脚本实例被替换时，清理当前脚本功能逻辑
     */
    void destroy();
}
