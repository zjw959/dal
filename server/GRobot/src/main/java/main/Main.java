package main;

import UI.window.frame.FunctionWindow;
import UI.window.frame.MessageWindow;
import conf.RunConf;
import core.Log4jManager;
import core.event.EventScanner;
import data.GameDataManager;
import logic.item.GoodsCfgCache;
import utils.FileEx;
import utils.MiscUtils;

/**
 * @function 主函数
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // 初始化配置文件
        RunConf.initConf();

        // 加载GameData
        if (MiscUtils.isIDEEnvironment()) {
            GameDataManager.Ainit("../GameServer/target/csv/");
        } else {
            GameDataManager.Ainit("./csv/");
        }
        // 加载道具缓存
        GoodsCfgCache.getInstance().init();

        // 初始化事件模板
        EventScanner.initEventClazzs();

        try {
            // 初始化协议
            EventScanner.initMessageClazzs();
            EventScanner.initRespMessageClazzs();
        } catch (Exception e) {
            Log4jManager.getInstance().error(e);
            System.exit(1);
        }

        boolean openMessage = false;
        if (FileEx.isExists("./config/windowIndex.txt")) {
            String select = FileEx.readAll("./config/windowIndex.txt");
            select = select.trim();
            if (select.equals("2")) {
                openMessage = true;
            }
        }
        // TEST
        // GiftCodeHelp.init();

        MessageWindow.getInstance().setVisible(openMessage);
        FunctionWindow.getInstance().setVisible(!openMessage);


    }
}
