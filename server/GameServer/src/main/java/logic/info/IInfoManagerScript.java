package logic.info;

import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import script.IScript;

public interface IInfoManagerScript extends IScript {
    public void tick(Player player);

    /**
     * 改变角色体力
     * 
     * @param num
     * @param isForce 是否可以强制超过当前等级的体力上限
     * @param isNotify
     * @return
     */
    public boolean changeStrength(InfoManager im, int num, boolean isForce);

    /** 获取当前等级体力上限 */
    public int getLevelMaxStrength(Player player);

    /** 通知客户端体力变化 */
    public void sendStrengthUpdate(Player player);

    public void createEvent(Player player);

    /** 专注变化 **/
    public void changeAbsorbed(InfoManager im, int change);

    /** 魅力变化 **/
    public void changeGlamour(InfoManager im, int change);

    /** 温柔变化 **/
    public void changeTender(InfoManager im, int change);

    /** 知识变化 **/
    public void changeKnowledge(InfoManager im, int change);

    /** 运气变化 **/
    public void changefortune(InfoManager im, int change);

    public int getMaxByItemId(int itemId);

    /** 计算 **/
    public int calculate(InfoManager im, int parm, int change, int max);

    public void acrossDay(Player player, EAcrossDayType type, boolean isNotify);

    public void createPlayerInitialize(Player player);
    
    /** 当前是否是防沉迷状态 */
    public boolean isAnti(InfoManager im);
}
