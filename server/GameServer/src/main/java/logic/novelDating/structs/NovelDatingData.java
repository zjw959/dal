package logic.novelDating.structs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NovelDatingData {
    /** 已经完成的结局 */
    private Set<Integer> completeEndings = new HashSet<Integer>();
    /**
     * 背包
     */
    private Map<Integer, Integer> bag = new HashMap<>();

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

    private Map<Integer, Integer> qualityMap = new HashMap<Integer, Integer>();
    private List<Integer> doneDatings = new ArrayList<Integer>();
    Map<Integer, Integer> tempBag = new HashMap<Integer, Integer>();
    Set<Integer> tempFlag = new HashSet<Integer>();
    private Set<Integer> completeScript = new HashSet<Integer>();
    private Set<Integer> guideScript = new HashSet<Integer>();
    /**
     * 获取当前阶段id
     * 
     * @return
     */
    public int getCurrentParagraphId() {
        return this.getParagraghs().get(this.getParagraghs().size() - 1);
    }

    public void addDoneNovelDating(int favorDatingId) {
        if (!this.doneDatings.contains(favorDatingId)) {
            this.doneDatings.add(favorDatingId);
        }
    }

    public void addParagraghId(int paragraphId) {
        this.getParagraghs().add((Integer) paragraphId);
    }

    public NovelDatingData(List<Integer> paragraghs, Map<Integer, Integer> entrances,
            int beginTime) {
        this.paragraghs = paragraghs;
        this.entrances = entrances;
        this.currentTime = beginTime;
    }

    public NovelDatingData() {}

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

    public List<Integer> getDoneDatings() {
        return doneDatings;
    }

    public void setDoneDatings(List<Integer> doneDatings) {
        this.doneDatings = doneDatings;
    }

    public List<Integer> getMessages() {
        return messages;
    }

    public void setMessages(List<Integer> messages) {
        this.messages = messages;
    }

    public Set<Integer> getCompleteScript() {
        return completeScript;
    }

    public void setCompleteScript(Set<Integer> completeScript) {
        this.completeScript = completeScript;
    }

    public Set<Integer> getGuideScript() {
        return guideScript;
    }

    public void setGuideScript(Set<Integer> guideScript) {
        this.guideScript = guideScript;
    }
}
