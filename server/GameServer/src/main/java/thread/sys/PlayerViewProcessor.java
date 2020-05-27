package thread.sys;

import logic.character.PlayerViewService;
import thread.sys.base.SysFunctionProcessor;

/**
 * 
 * @Description 获取好友View数据线程
 * @author LiuJiang
 * @date 2018年6月30日 下午4:48:23
 *
 */
public class PlayerViewProcessor extends SysFunctionProcessor {

    static int coreSize = Runtime.getRuntime().availableProcessors() < 2 ? 1 : 2;

    static {
        coreSize = Runtime.getRuntime().availableProcessors() / 3;
        if (coreSize < 2) {
            coreSize = 1;
        }
    }

    private PlayerViewProcessor() {
        super(PlayerViewProcessor.class.getSimpleName(), coreSize, coreSize);
    }

    @Override
    protected void registerService() {
        services.add(PlayerViewService.getInstance());
    }
}
