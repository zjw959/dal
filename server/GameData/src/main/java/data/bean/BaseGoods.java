/**
 * Auto generated, do not edit it
 *
 * 物品类基类
 */
package data.bean;

import java.util.Map;

public class BaseGoods {

    /** id */
    public int getId() {
        return 0;
    }

    /** 名字 */
    public String getName() {
        return null;
    }

    /** 道具类型 */
    public int getSuperType() {
        return 0;
    }

    /** 子类型 */
    public int getSubType() {
        return 0;
    }

    /** 背包类型 */
    public int getBagType() {
        return 0;
    }

    /** 排序索引 */
    public int getOrder() {
        return 0;
    }

    /** 星级 */
    public int getStar() {
        return 0;
    }

    /** 品质 */
    public int getQuality() {
        return 0;
    }

    /** 出售收益 */
    public Map getSellProfit() {
        return null;
    }

    /** 使用消耗 */
    public Map getUseCast() {
        return null;
    }

    /** 使用收益 */
    public Map getUseProfit() {
        return null;
    }

    /** 总上限 */
    public int getTotalMax() {
        return 0;
    }

    /** 单格上限 */
    public int getGridMax() {
        return 0;
    }

    /** 达总上限转换 */
    public Map getConvertMax() {
        return null;
    }

    public boolean isConvert() {
        return getConvertMax() != null && !getConvertMax().isEmpty();
    }

    /** 单次使用上限 */
    public int getOnceUseLimit() {
        return 0;
    }

    /** 交易收益 */
    public Map getDealProfit() {
        return null;
    }

    /** 叠加 */
    public boolean getPileUp() {
        return false;
    }

    /** 自动使用 */
    public boolean getAutoUse() {
        return false;
    }

}
