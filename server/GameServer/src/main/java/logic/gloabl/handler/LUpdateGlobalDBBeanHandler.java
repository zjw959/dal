package logic.gloabl.handler;

import gm.utils.GlobalUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description 更新全局bean
 * @author LiuJiang
 * @date 2018年7月27日 下午6:24:58
 *
 */
public class LUpdateGlobalDBBeanHandler extends GameInnerHandler {
    private int id;
    private long longValue;
    private String strValue;

    public LUpdateGlobalDBBeanHandler(int id, long longValue, String strValue) {
        this.id = id;
        this.longValue = longValue;
        this.strValue = strValue;
    }

    @Override
    public void action() throws Exception {
        GlobalUtils.insertOrUpdateBean(id, longValue, strValue);
    }
}
