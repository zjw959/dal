package logic.basecore;

/**
 * 登陆初始化
 * 
 * 注意: 以前改接口的作用是 数据延迟发送. 但客户端不支持 (登录发送必要数据.客户端初始化加载完成)
 * 
 * {@param 1.是否在客户端请求加载完成后发送功能系统数据}
 * 
 * {@param 2.还是玩家请求功能系统相关功能在发送数据} 如果需要 [1] 这种方式需要实现本借口
 * 
 */
public interface ILoginInit {
    public void loginInit();
}
