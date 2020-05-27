package logic.basecore;

import com.google.gson.JsonElement;

/**
 * 数据延迟发送
 */
public interface IView {
    public abstract IView toView();

    /** 从完整的jsonData转换为ViewJson */
    public abstract JsonElement toViewJson(String fullJsonData);
}

