package logic.favor.structs;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavorDatingData {
    /** 已经完成的结局 */
    private Set<Integer> completeEndings = new HashSet<Integer>();
    /**
     * 背包
     */
    private Map<Integer, Integer> bag = new HashMap<>();
    /**
     * 外传约会时间进度
     */
    private int dateSchedule;
    /**
     * 外传约会入口进度
     */
    private int entranceSchedule;

    /**
     * 最近日期进度的改变时间
     */
    private java.util.Date lastDateSchedule;

    /**
     * 阶段id记录列表 末尾为当前阶段(step)
     */
    private List<Integer> paragraghs = new ArrayList<Integer>();

    /**
     * 事件标记
     */
    private List<Integer> eventFlag = new ArrayList<Integer>();
    /**
     * 事件入口
     */
    private Map<Integer, Integer> entrances = new HashMap<Integer, Integer>();
    /**
     * 结局列表
     */
    private List<Integer> endings = new ArrayList<Integer>();
    /**
     * 消息列表
     */
    private List<Integer> messages = new ArrayList<Integer>();
    /**
     * 本阶段被移除的入口
     */
    private List<Integer> rmEntrance = new ArrayList<Integer>();;
    /**
     * 当天的时间，根据事件时间会进行相应的推动
     */
    private int currentTime;
    private int repeat = 1;
    private Map<Integer, Boolean> repeatTimeMap = new HashMap<Integer, Boolean>();
    private Map<Integer, Integer> qualityMap = new HashMap<Integer, Integer>();
    private boolean rePlay = false;
    private Set<Integer> completeDatingScript = new HashSet<Integer>();

    private List<Integer> rewardFavorId = new ArrayList<>();
    private Set<Integer> choosed = new HashSet<Integer>();
    private Map<Integer, Map<Integer, Integer>> tempEndItemMap =
            new HashMap<Integer, Map<Integer, Integer>>();
    Map<Integer, Integer> tempBag = new HashMap<Integer, Integer>();
    Map<Integer, Integer> tempQuality = new HashMap<Integer, Integer>();
    Set<Integer> tempFlag = new HashSet<Integer>();
    Set<Integer> tempChoose = new HashSet<Integer>();

    /**
     * 获取当前阶段id
     * 
     * @return
     */
    public int getCurrentParagraphId() {
        return this.getParagraghs().get(this.getParagraghs().size() - 1);
    }
    public void addParagraghId(int paragraphId) {
        this.getParagraghs().add((Integer) paragraphId);
    }
    public FavorDatingData(List<Integer> paragraghs, int entranceSchedule,
            Map<Integer, Integer> entrances, int defaultSchedule, int beginTime) {
        this.paragraghs = paragraghs;
        this.entranceSchedule = entranceSchedule;
        this.entrances = entrances;
        this.lastDateSchedule = new Date();
        this.dateSchedule = defaultSchedule;
        this.currentTime = beginTime;
    }
    public FavorDatingData() {}
    public Set<Integer> getCompleteEndings() {
        return completeEndings;
    }

    public void setCompleteEndings(Set<Integer> completeEndings) {
        this.completeEndings = completeEndings;
    }

    public Map<Integer, Integer> getBag() {
        return bag;
    }

    public void setBag(Map<Integer, Integer> bag) {
        this.bag = bag;
    }

    public int getDateSchedule() {
        return dateSchedule;
    }

    public void setDateSchedule(int dateSchedule) {
        this.dateSchedule = dateSchedule;
    }

    public int getEntranceSchedule() {
        return entranceSchedule;
    }

    public void setEntranceSchedule(int entranceSchedule) {
        this.entranceSchedule = entranceSchedule;
    }

    public java.util.Date getLastDateSchedule() {
        return lastDateSchedule;
    }

    public void setLastDateSchedule(java.util.Date lastDateSchedule) {
        this.lastDateSchedule = lastDateSchedule;
    }

    public List<Integer> getParagraghs() {
        return paragraghs;
    }

    public void setParagraghs(List<Integer> paragraghs) {
        this.paragraghs = paragraghs;
    }

    public List<Integer> getEventFlag() {
        return eventFlag;
    }

    public void setEventFlag(List<Integer> eventFlag) {
        this.eventFlag = eventFlag;
    }

    public Map<Integer, Integer> getEntrances() {
        return entrances;
    }

    public void setEntrances(Map<Integer, Integer> entrances) {
        this.entrances = entrances;
    }

    public List<Integer> getEndings() {
        return endings;
    }

    public void setEndings(List<Integer> endings) {
        this.endings = endings;
    }

    public List<Integer> getRmEntrance() {
        return rmEntrance;
    }

    public void setRmEntrance(List<Integer> rmEntrance) {
        this.rmEntrance = rmEntrance;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public Map<Integer, Integer> getTempBag() {
        return tempBag;
    }

    public void setTempBag(Map<Integer, Integer> tempBag) {
        this.tempBag = tempBag;
    }

    public Set<Integer> getTempFlag() {
        return tempFlag;
    }

    public void setTempFlag(Set<Integer> tempFlag) {
        this.tempFlag = tempFlag;
    }

    public Map<Integer, Integer> getQualityMap() {
        return qualityMap;
    }

    public void setQualityMap(Map<Integer, Integer> qualityMap) {
        this.qualityMap = qualityMap;
    }

    public Map<Integer, Integer> getTempQuality() {
        return tempQuality;
    }

    public List<Integer> getMessages() {
        return messages;
    }

    public void setMessages(List<Integer> messages) {
        this.messages = messages;
    }

    public List<Integer> getRewardFavorId() {
        return rewardFavorId;
    }

    public void setRewardFavorId(List<Integer> rewardFavorId) {
        this.rewardFavorId = rewardFavorId;
    }

    public Set<Integer> getTempChoose() {
        return tempChoose;
    }

    public Set<Integer> getChoosed() {
        return choosed;
    }

    public void setChoosed(Set<Integer> choosed) {
        this.choosed = choosed;
    }

    public boolean isRePlay() {
        return rePlay;
    }

    public void setRePlay(boolean rePlay) {
        this.rePlay = rePlay;
    }

    public Set<Integer> getCompleteDatingScript() {
        return completeDatingScript;
    }

    public void setCompleteDatingScript(Set<Integer> completeDatingScript) {
        this.completeDatingScript = completeDatingScript;
    }

    public Map<Integer, Boolean> getRepeatTimeMap() {
        return repeatTimeMap;
    }

    public void setRepeatTimeMap(Map<Integer, Boolean> repeatTimeMap) {
        this.repeatTimeMap = repeatTimeMap;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }
    public Map<Integer, Map<Integer, Integer>> getTempEndItemMap() {
        return tempEndItemMap;
    }
    public void setTempEndItemMap(Map<Integer, Map<Integer, Integer>> tempEndItemMap) {
        this.tempEndItemMap = tempEndItemMap;
    }
}
