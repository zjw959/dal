package logic.equip.bean;


/**
 * 
 * @Description 装备特殊属性封装
 * @author LiuJiang
 * @date 2018年6月5日 下午4:16:12
 *
 */
public class EquipSpecialAttr {
    /** 类型 */
    int type;
    /** 数值 */
    int value;
    /** 模板id */
    int templateId;
    /** 位置下标 */
    int index;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTemplateId() {
        return templateId;
    }

    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public EquipSpecialAttr copy() {
        EquipSpecialAttr item = new EquipSpecialAttr();
        item.setIndex(this.index);
        item.setType(this.type);
        item.setValue(this.value);
        item.setTemplateId(this.templateId);
        return item;
    }
}
