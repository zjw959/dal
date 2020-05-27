package thread.sys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import logic.activity.ActivityDateProvide;
import logic.character.PlayerManager;
import logic.chasm.TeamService;
import logic.gloabl.GlobalService;
import logic.gloabl.handler.LGetGlobalDBBeanHandler;
import logic.marquee.MarqueeService;
import logic.marquee.handler.LGetMarqueesHandler;
import logic.marquee.handler.LNoticeMarqueesHandler;

import org.apache.log4j.Logger;

import server.GameServer;
import server.ServerConfig;
import thread.sys.base.SysFunctionProcessor;
import utils.DateEx;
import cn.hutool.http.HttpUtil;

/**
 * 全局线程
 * 
 * 功能都尽量集中在全局功能线程中,避免不必要的上下文切换,耗时功能需要独立线程
 */
public class GlobalProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(GlobalProcessor.class);

    private GlobalProcessor() {
        super(GlobalProcessor.class.getSimpleName());
        _initTimer(1000, 1000, true);
    }

    @Override
    protected void registerService() {
        services.add(GlobalService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        // 跑马灯tick
        tickGlobal();
        // 跑马灯tick
        tickMarquee();
        // // 全服功能开关tick
        // tickFunctionSwitch();
        
        Date date = new Date();
        if (!DateEx.isSameDay(GameServer.getInstance().getDayDate(), date)) {
            LOGGER.info("server day arcorss day, date:"
                    + DateEx.format(date, DateEx.fmt_yyyy_MM_dd_HH_mm));
            GameServer.getInstance().setDayTime(date.getTime());
        }
        
        TeamService.getDefault().checkChasmStatus();
        ActivityDateProvide.getDefault().checkActivityChange();
        // 登录服信息同步
        String url = ServerConfig.getInstance().getLoginSyncUrl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("serverId", ServerConfig.getInstance().getServerId());
        map.put("onlineNum", PlayerManager.getOnlineNum());
        HttpUtil.get(url, map);
    }


    /**
     * 全服功能开关tick
     */
    private void tickGlobal() {
        GlobalService service = GlobalService.getInstance();
        long now = System.currentTimeMillis();
        if (now >= service.getNextTickGetTime()) {
            this.executeInnerHandler(new LGetGlobalDBBeanHandler());
        }
    }

    /**
     * 跑马灯tick
     */
    private void tickMarquee() {
        MarqueeService service = MarqueeService.getInstance();
        long now = System.currentTimeMillis();
        if (now >= service.getNextTickGetTime()) {
            this.executeInnerHandler(new LGetMarqueesHandler());
        }
        if (service.getMarquees().size() > 0) {
            this.executeInnerHandler(new LNoticeMarqueesHandler());
        }
    }
    
    // /**
    // * 全服功能开关tick
    // */
    // private void tickFunctionSwitch() {
    // FunctionSwitchService service = FunctionSwitchService.getInstance();
    // long now = System.currentTimeMillis();
    // if (now >= service.getNextTickGetTime()) {
    // this.executeInnerHandler(new LGetFunctionSwitchHandler());
    // }
    // }

}
