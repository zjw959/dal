package com.constant;

/**
 * @rules 脚本编号 脚本Id分配规则为: 编号 0 为禁用id 1. 99 （包含99）以下的ID为特殊脚本。 2. 100 - 199 为道具使用脚本 3. 其余功能脚本规则为:
 *        1000(4位功能Id 对应消息ID) + 01 (2位方法Id) 比如: 10010 为某个功能的脚本Id
 * @since 1.0.0
 */
public enum EScriptIdDefine {
    // 注意,临时脚本以负数开头
    // 分布时没有的脚本一定要注意id序号,不能重复进行覆盖.
    /**
     * 类加载器检查类
     */
    LOADCHECK_SCRIPT(1), SHUTDOWN_SCRIPT(2),
    /**
     * controller 处理脚本
     */
    ACCOUNT_CONTROLLER_SCRIPTID(10),
    Account_Permission_Controller_SCRIPTID(11),
    Recharge_Controller_SCRIPTID(12),
    Server_Group_Controller_SCRIPTID(13),
    Server_Manager_Controller_SCRIPTID(14),
    

    /**
     * 脚本ID结束
     */
    END_SCRIPT;

    private final int value;

    private static class Counter {
        private static int nextValue = 0;
    }

    EScriptIdDefine() {
        this(++Counter.nextValue);
    }

    EScriptIdDefine(int value) {
        this.value = value;
        Counter.nextValue = value;
    }

    public int Value() {
        return this.value;
    }
}
