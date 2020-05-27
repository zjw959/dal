package gm.db.global.bean;

import java.util.ArrayList;
import java.util.List;

public class ActivityConfigure{

    /** 活动id **/
    protected int id;
    /**活动类型  
     * @see ActivityType*/
    protected int type;
    /**活动状态 
     * @see ActivityStatus*/
    protected int status;
    /**活动标题*/
    protected String title;
    /**活动副标题*/
    protected String subtitle;
    /** 开启时间 **/
    protected long beginTime;
    /** 活动结束时间 --活动在这个时候已经结束了 活动进度不会变化  但是活动不消失  玩家可以领取没领取的奖励 **/
    protected long endTime;
    /** 活动展示开启时间 --活动这个时候可以发送给客户端了  但是不能完成没有进度**/
    protected long showBeginTime;
    /** 活动展示结束时间 **/
    protected long showEndTime;
    /** 活动创建时间 **/
    protected long createTime;
    /** 活动修改时间 **/
    protected long changeTime;
    /**活动详情*/
    protected String details;
    /**活动条目*/
    protected String items;
    /**活动权重-影响活动显示排序*/
    protected int weight;
    /**编辑状态--如果状态为1表示后台正在对该活动进行编辑  游戏服读取时忽略该活动
     * 状态为2表示已经编辑完   游戏服务器可以去读取最新的数据*/
    protected int updateStatus;
    /**广告图*/
    protected String showIcon;
    
	private List<Integer> itemsList = new ArrayList<>(); 
    
    public ActivityConfigure() {
        super();
    }
    
    public ActivityConfigure(int id, int type, int status, String title, String subtitle, long beginTime, long endTime,
			long showBeginTime, long showEndTime, long createTime, long changeTime, String details, String items,
			int weight, int updateStatus, List<Integer> itemsList) {
		super();
		this.id = id;
		this.type = type;
		this.status = status;
		this.title = title;
		this.subtitle = subtitle;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.showBeginTime = showBeginTime;
		this.showEndTime = showEndTime;
		this.createTime = createTime;
		this.changeTime = changeTime;
		this.details = details;
		this.items = items;
		this.weight = weight;
		this.updateStatus = updateStatus;
		this.itemsList = itemsList;
	}

	public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public long getCreateTime() {
        return createTime;
    }
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    public long getChangeTime() {
        return changeTime;
    }
    public void setChangeTime(long changeTime) {
        this.changeTime = changeTime;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public String getItems() {
        return items;
    }
    public void setItems(String items) {
        this.items = items;
        if(items!=null&&!items.isEmpty()){
            setItemsList(stringToList(items));
        }
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public List<Integer> getItemsList() {
        return itemsList;
    }
    public void setItemsList(List<Integer> itemsList) {
        this.itemsList = itemsList;
    }
    public int getUpdateStatus() {
        return updateStatus;
    }
    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }
    public long getShowBeginTime() {
		return showBeginTime;
	}
	public void setShowBeginTime(long showBeginTime) {
		this.showBeginTime = showBeginTime;
	}
	public long getShowEndTime() {
		return showEndTime;
	}
	public void setShowEndTime(long showEndTime) {
		this.showEndTime = showEndTime;
	}
	public String getShowIcon() {
		return showIcon;
	}
	public void setShowIcon(String showIcon) {
		this.showIcon = showIcon;
	}
    /**
     * 将逗号分隔开的字符串转换成集合
     * @param str 字符串
     * @return 集合
     */
    public static List<Integer> stringToList(String str) {
        str=str.replaceAll("，", ",");
        List<Integer> list = new ArrayList<Integer>();
        if(!str.equals("")){
            String[] strs = str.split(",");
            for(String s : strs){
                int x = Integer.parseInt(s);
                    list.add(x);
            }
        }
        return list;
    }

	@Override
	public String toString() {
		return "ActivityConfigure [id=" + id + ", type=" + type + ", status=" + status + ", title=" + title
				+ ", subtitle=" + subtitle + ", beginTime=" + beginTime + ", endTime=" + endTime + ", showBeginTime="
				+ showBeginTime + ", showEndTime=" + showEndTime + ", createTime=" + createTime + ", changeTime="
				+ changeTime + ", details=" + details + ", items=" + items + ", weight=" + weight + ", updateStatus="
				+ updateStatus + ", showIcon=" + showIcon + ", itemsList=" + itemsList + "]";
	}

  }
