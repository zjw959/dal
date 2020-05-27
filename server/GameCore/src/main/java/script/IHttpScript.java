package script;

import script.IScript;

import net.http.HttpRequestWrapper;

/**
 * 通过Http调用的脚本
 * 
 * 通常用于bug修复
 */
public interface IHttpScript extends IScript {
    String execute(HttpRequestWrapper httpRequest);
}
