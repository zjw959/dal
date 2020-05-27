package gm.db.global.bean;

/****
 * 
 * 活动任务条目
 * 
 * @author lihongji
 *
 */
public class ActivityTaskItem {

    private int id;// 任务条目id
    private String icon; // 任务图标
    private int rank; // 客户端显示顺序
    private String des;// 任务的文本描述，配置内容为string表中的id
    private String des2;// 任务的文本描述，配置内容为string表中的id
    private int resetType;// 重置类型
    private int finishCondid;// 完成条件id
    private String finishParams;// 完成参数
    private int progress;// 当前进度
    private String reward;// 完成奖励
    private int jumpInterface;// 跳转
    private int open;// 是否开启

    public ActivityTaskItem() {

    }

    public ActivityTaskItem(int id, String icon, int rank, String des, String des2, int resetType,
            int finishCondid, String finishParams, int progress, String reward, int jumpInterface,
            int open) {
        super();
        this.id = id;
        this.icon = icon;
        this.rank = rank;
        this.des = des;
        this.des2 = des2;
        this.resetType = resetType;
        this.finishCondid = finishCondid;
        this.finishParams = finishParams;
        this.progress = progress;
        this.reward = reward;
        this.jumpInterface = jumpInterface;
        this.open = open;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getResetType() {
        return resetType;
    }

    public void setResetType(int resetType) {
        this.resetType = resetType;
    }

    public int getFinishCondid() {
        return finishCondid;
    }

    public void setFinishCondid(int finishCondid) {
        this.finishCondid = finishCondid;
    }

    public String getFinishParams() {
        return finishParams;
    }

    public void setFinishParams(String finishParams) {
        this.finishParams = finishParams;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getJumpInterface() {
        return jumpInterface;
    }

    public void setJumpInterface(int jumpInterface) {
        this.jumpInterface = jumpInterface;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getDes2() {
        return des2;
    }

    public void setDes2(String des2) {
        this.des2 = des2;
    }

    @Override
    public String toString() {
        return "ActivityTaskItem [id=" + id + ", icon=" + icon + ", rank=" + rank + ", des=" + des
                + ", resetType=" + resetType + ", finishCondid=" + finishCondid + ", finishParams="
                + finishParams + ", progress=" + progress + ", reward=" + reward
                + ", jumpInterface=" + jumpInterface + ", des2=" + des2 + ", open=" + open + "]";
    }


}
