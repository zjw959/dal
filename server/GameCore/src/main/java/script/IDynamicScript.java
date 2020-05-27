package script;

import net.http.HttpRequestWrapper;

/**
 * 需要通过脚本机制实现动态加载的类(包括不限于:handler，TimerEvent...)需要实现本接口, 否则脚本加载机制不识别,拒绝加载
 * 
 * 不需要预先配置脚本id的脚本类
 * 
 * 主要用途方便脚本机制识别需要加载的类文件
 * 
 * 此接口不要和IScript 一起使用
 * 
 */
public interface IDynamicScript {
    String execute(HttpRequestWrapper httpRequest);
}
