package logic.gloabl.handler;

import logic.gloabl.GlobalService;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 系统获取全局表数据(定时从数据库拉取)
 * @author LiuJiang
 * @date 2018年8月23日 下午9:24:52
 *
 */
public class LGetGlobalDBBeanHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        GlobalService.getInstance().checkUpdate();
    }
}
