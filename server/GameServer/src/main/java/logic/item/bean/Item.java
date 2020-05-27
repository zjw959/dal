package logic.item.bean;

import org.apache.log4j.Logger;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(ItemSerializer.class)
public abstract class Item {
    private static final Logger LOGGER = Logger.getLogger(Item.class);

    /** 道具id **/
    private long id;
    /** 道具模板id */
    private int cid;
    /** 道具数量 */
    private int num;
    /** 道具失效时间 无时间限制为 0 */
    private long dTime;

    public long getId() {
        return id;
    }

    public Item setId(long id) {
        this.id = id;
        return this;
    }

    public long getDeadTime() {
        return dTime;
    }

    public Item setDeadTime(long dTime) {
        this.dTime = dTime;
        return this;
    }

    public int getTemplateId() {
        return cid;
    }

    public int getNum() {
        return num;
    }

    public Item setTemplateId(int cid) {
        this.cid = cid;
        return this;
    }

    public Item setNum(int num) {
        this.num = num;
        return this;
    }

    // public ItemData getItemData() {
    // ItemData itemData = GameDataManager.getItemData(this.templateId);
    // if (itemData == null) {
    // LOGGER.error(ConstDefine.LOG_ERROR_CONFIG_PREFIX + "itemData error, tempId:" + templateId
    // + ExceptionEx.currentThreadTraces());
    // return null;
    // }
    // return itemData;
    // }

    public void copyBase(Item item) {
        item.setId(this.id);
        item.setDeadTime(this.dTime);
        item.setNum(this.num);
        item.setTemplateId(this.cid);
    }

    public ItemLogBean toLogBean(int gainNum, int useNumber, int totalNum, boolean fromMail) {
        return new ItemLogBean(this, gainNum, useNumber, totalNum, fromMail);
    }

    public static class ItemLogBean {
        final private int templateId;
        final private long uid;
        final private int useNum;
        final private int gainNum;
        final private int totalNum;
        final private boolean fromMail;

        public ItemLogBean(Item item, int gainNum, int useNum, int totalNum, boolean fromMail) {
            this.templateId = item.getTemplateId();
            this.uid = item.getId();
            this.useNum = useNum;
            this.gainNum = gainNum;
            this.totalNum = totalNum;
            this.fromMail = fromMail;
        }

        public int getTemplateId() {
            return templateId;
        }

        public long getUid() {
            return uid;
        }

        public int getTotalNum() {
            return totalNum;
        }

        public int getUseNum() {
            return useNum;
        }

        public int getGainNum() {
            return gainNum;
        }

        public boolean isFromMail() {
            return fromMail;
        }
    }

    public abstract Item copy();

    /**
     * 子类需要初始化对象，需要覆盖此对象实现具体逻辑
     */
    public abstract void initialize();

    /**
     * 道具是否在使用中
     */
    public abstract boolean isInUse();

    // public Message.ItemMsg.ItemData.Builder itemDataBuilder(Player player) {
    // Message.ItemMsg.ItemData.Builder builder = Message.ItemMsg.ItemData.newBuilder();
    // builder.setId(templateId);
    // builder.setUid(id);
    // builder.setCount(num);
    // boolean isPutonAvatar = player.getAvatarManager().isPutOnAvatarPart(id);
    // builder.setAvatarOn(isPutonAvatar);
    // buildItemOtherData(builder);
    // return builder;
    // }

    // public abstract void buildItemOtherData(Message.ItemMsg.ItemData.Builder builder);

    @Override
    public String toString() {
        return "Item{" + "id=" + id + ", templateId=" + cid + ", num=" + num + ", deadTime="
                + dTime + '}';
    }
}
