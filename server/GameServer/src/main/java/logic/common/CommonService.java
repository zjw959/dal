package logic.common;

import logic.support.SensitiveWordFilter;
import server.GameServer;

/**
 * 通用接口处理器 对应CommonProcess类
 */
public class CommonService {

    /**
     * 检测角色名
     * 
     * @param characterName
     * @return
     */
    public static int checkRoleName(String characterName) {
        if (characterName == null)
            return -4;

        // 名字长度检查
        if (!GameServer.getInstance().isTestServer()) {
            if (_getRoleNameLen(characterName) > 7) {
                return -3;
            }
        }

        if (SensitiveWordFilter.getInstance().check(characterName))
            return -2;
        return 0;
    }

    /**
     * 获取角色名长度(一个汉字或日韩文长度为1,英文字符长度为0.5)
     * 
     * @param name
     * @return
     */
    private static double _getRoleNameLen(String name) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < name.length(); i++) {
            // 获取一个字符
            String temp = name.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        // 进位取整
        return Math.ceil(valueLength);
    }

//    public static void processorGmCmd(Player player, CharacterMsg.ReqGm msg) {
//        LogicScriptsUtils.getICommonScript().processGMCmd(player, msg);
//    }
}
