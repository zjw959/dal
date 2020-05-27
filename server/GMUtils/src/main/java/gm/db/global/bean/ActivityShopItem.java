package gm.db.global.bean;

public class ActivityShopItem {

    private int id; // id
    private String name; // 商品名称
    private int rank; // 权重
    private int limitType; // 限购（0.不限购。1.每日限购。2.总数限购。3.全服每日限购。5.全服总数限购）
    private int limitVal; // 限购值
    private int serLimit; // 全服时个人限购值
    private String limitDes; // 限购描述
    private String goods; // 道具（id:数量）
    private String price; // 价格类型
    private String tag; // 折扣
    private int batchBuy;// 是否可以批量购买
    private int open;// 商品开关

    public ActivityShopItem() {

    }

    public ActivityShopItem(int id, String name, int rank, int limitType, int limitVal,
            int serLimit, String limitDes, String goods, String price, String tag, int batchBuy,
            int open) {
        super();
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.limitType = limitType;
        this.limitVal = limitVal;
        this.serLimit = serLimit;
        this.limitDes = limitDes;
        this.goods = goods;
        this.price = price;
        this.tag = tag;
        this.batchBuy = batchBuy;
        this.open = open;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getLimitType() {
        return limitType;
    }

    public void setLimitType(int limitType) {
        this.limitType = limitType;
    }

    public int getLimitVal() {
        return limitVal;
    }

    public void setLimitVal(int limitVal) {
        this.limitVal = limitVal;
    }

    public int getSerLimit() {
        return serLimit;
    }

    public void setSerLimit(int serLimit) {
        this.serLimit = serLimit;
    }

    public String getLimitDes() {
        return limitDes;
    }

    public void setLimitDes(String limitDes) {
        this.limitDes = limitDes;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getBatchBuy() {
        return batchBuy;
    }

    public void setBatchBuy(int batchBuy) {
        this.batchBuy = batchBuy;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "ActivityShopItem [id=" + id + ", name=" + name + ", rank=" + rank + ", limitType="
                + limitType + ", limitVal=" + limitVal + ", serLimit=" + serLimit + ", limitDes="
                + limitDes + ", goods=" + goods + ", price=" + price + ", tag=" + tag
                + ", batchBuy=" + batchBuy + ", open=" + open + "]";
    }

}
