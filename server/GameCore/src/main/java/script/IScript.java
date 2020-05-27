package script;

/**
 * 所有脚本接口的基类
 *
 * 脚本使用简单描述: 在需要使用脚本的地方, 提供接口继承IScript, 脚本Id按照规则添加, 然后在scripts中添加脚本类实现该接口. 详情请参考服务器文档.
 * 
 * 调用的地方通过ScriptManager获得该接口调用
 *
 * 注意: 脚本涉及到类的热替换, 因此脚本类中不应该包含任何需要持久保存的成员数据,基本上只应该提供逻辑处理方法.
 *
 */
public interface IScript {
    /**
     * 返回脚本的Id
     * 
     * @return
     */
    int getScriptId();
}
