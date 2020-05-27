package logic.item.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;

import logic.equip.EquipService;
import logic.equip.bean.EquipSpecialAttr;

/**
 * 装备道具(质点)
 */
public class EquipItem extends BasicItem {
    /**
     * 等级
     */
    int lev = 1;
    /**
     * 经验
     */
    int exp;
    /**
     * 关联英雄id
     */
    int hid;
    /**
     * 装备位置
     */
    int pos;
    /**
     * 特殊属性
     */
    List<EquipSpecialAttr> sAttrs;
    /**
     * 临时特殊属性
     */
    Map<Integer, EquipSpecialAttr> tAttr;
    /**
     * 临时洗练质点信息
     */
    String chEquip;

    /**
     * 是否被锁定
     */
    boolean lock;

    public int getLevel() {
        return lev;
    }

    public void setLevel(int level) {
        this.lev = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHeroId() {
        return hid;
    }

    public void setHeroId(int heroId) {
        this.hid = heroId;
    }

    public int getPosition() {
        return pos;
    }

    public void setPosition(int position) {
        this.pos = position;
    }

    public List<EquipSpecialAttr> getSpecialAttrList() {
        return sAttrs;
    }

    public void setSpecialAttrList(List<EquipSpecialAttr> specialAttrList) {
        this.sAttrs = specialAttrList;
    }

    public Map<Integer, EquipSpecialAttr> getTempSpecialAttr() {
        return tAttr;
    }

    public void setTempSpecialAttr(Map<Integer, EquipSpecialAttr> tempSpecialAttr) {
        this.tAttr = tempSpecialAttr;
    }

    /**
     * 替换特殊属性
     */
    public void changeAttr() {
        if (tAttr == null)
            return;
        for (Entry<Integer, EquipSpecialAttr> entry : tAttr.entrySet()) {
            entry.getValue().setIndex(entry.getKey());
            sAttrs.set(entry.getKey(), entry.getValue());
        }
        removeTempAttr();
    }

    /**
     * 清除临时属性
     */
    public void removeTempAttr() {
        tAttr = null;
        chEquip = null;
    }

    @Override
    public Item copy() {
        EquipItem item = new EquipItem();
        copyBase(item);
        item.setLevel(this.lev);
        item.setExp(this.exp);
        item.setHeroId(this.hid);
        item.setPosition(this.pos);
        if (sAttrs != null) {
            List<EquipSpecialAttr> list = new ArrayList<EquipSpecialAttr>();
            for (EquipSpecialAttr attr : sAttrs) {
                list.add(attr.copy());
            }
            item.setSpecialAttrList(list);
        }
        if (tAttr != null) {
            Map<Integer, EquipSpecialAttr> tAttrMap = new HashMap<>();
            for (EquipSpecialAttr temp : tAttr.values()) {
                tAttrMap.put(temp.getIndex(), temp);
            }
            item.setTempSpecialAttr(tAttrMap);
        }
        item.setLock(this.lock);
        item.setChEquip(this.chEquip);
        return item;
    }

    /**
     * NOTE: 构造器私有化只能通过以下方法构造： {@link logic.item.ItemUtils#createItems(int, int)}
     * {@link logic.item.ItemUtils#createItem(JSONObject)}
     */
    @Override
    public void initialize() {
        // 初始化灵装
        // 随机灵装的特殊属性
        EquipService.getInstance().initEquip(this);
    }

    /**
     * 卸下
     */
    public void takeOff() {
        setPosition(0);
        setHeroId(0);
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    /**
     * 装备
     */
    public void equip(int heroId, int position) {
        setPosition(position);
        setHeroId(heroId);
    }

    public boolean getLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public String getChEquip() {
        return chEquip;
    }

    public void setChEquip(String chEquip) {
        this.chEquip = chEquip;
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.set(2, 2);
        System.out.println("--size=" + list.size());
        System.out.println("--v=" + list.get(2));
    }

    /**
     * 未装备时位置为0
     */
    public static final int NO_EQUIP_POSITION = 0;

    @Override
    public boolean isInUse() {
        return hid != 0;
    }
}
