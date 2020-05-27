package logic.dating.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.annotations.JsonAdapter;

/**
 * 当前约会对象
 * 
 * @author Alan
 *
 */
@JsonAdapter(DatingSerializer.class)
public abstract class CurrentDatingBean {

    private long id;

    /**
     * 本次剧本已经获得的奖励
     */
    private Map<Integer, Integer> reward = new HashMap<Integer, Integer>();

    /**
     * 对话次数记录
     */
    private Map<Integer, Integer> dialogueCount = new HashMap<Integer, Integer>();

    /**
     * 约会类型
     */
    private int datingType;

    /**
     * 积分
     */
    private int score;

    /**
     * 好感度奖励
     */
    private int favorReward;

    /**
     * 当前节点cid
     */
    private int currentCid;

    /**
     * 剧本分支节点
     */
    private Map<Integer, List<Integer>> currentScript=new HashMap<Integer, List<Integer>>();

    /**
     * 剧本id
     */
    private int scriptId;

    /**
     * 被选择的节点
     */
    private List<Integer> selectedNode = new ArrayList<Integer>();

    /**
     * 看板娘id
     */
    private List<Integer> roleIds;

    /**
     * id
     */
    public long getId() {
        return id;
    }

    public CurrentDatingBean(long id, int datingType, int score, int favorReward, int currentCid,
            Map<Integer, List<Integer>> currentScript, int scriptId, List<Integer> roleIds) {
        super();
        this.id = id;
        this.datingType = datingType;
        this.score = score;
        this.favorReward = favorReward;
        this.currentCid = currentCid;
        this.currentScript = currentScript;
        this.scriptId = scriptId;
        this.roleIds = roleIds;
    }

    /**
     * id
     */
    public CurrentDatingBean setId(long id) {
        this.id = id;
        return this;
    }

    /**
     * 本次剧本已经获得的奖励
     */
    public Map<Integer, Integer> getReward() {
        return reward;
    }

    /**
     * 本次剧本已经获得的奖励
     */
    public CurrentDatingBean setReward(Map<Integer, Integer> reward) {
        this.reward = reward;
        return this;
    }

    /**
     * 对话次数记录
     */
    public Map<Integer, Integer> getDialogueCount() {
        return dialogueCount;
    }

    /**
     * 对话次数记录
     */
    public CurrentDatingBean setDialogueCount(Map<Integer, Integer> dialogueCount) {
        this.dialogueCount = dialogueCount;
        return this;
    }

    /**
     * 约会类型
     */
    public int getDatingType() {
        return datingType;
    }

    /**
     * 约会类型
     */
    public CurrentDatingBean setDatingType(int datingType) {
        this.datingType = datingType;
        return this;
    }

    /**
     * 积分
     */
    public int getScore() {
        return score;
    }

    /**
     * 积分
     */
    public CurrentDatingBean setScore(int score) {
        this.score = score;
        return this;
    }

    /**
     * 好感度奖励
     */
    public int getFavorReward() {
        return favorReward;
    }

    /**
     * 好感度奖励
     */
    public CurrentDatingBean setFavorReward(int favorReward) {
        this.favorReward = favorReward;
        return this;
    }

    /**
     * 当前节点cid
     */
    public int getCurrentCid() {
        return currentCid;
    }

    /**
     * 当前节点cid
     */
    public CurrentDatingBean setCurrentCid(int currentCid) {
        this.currentCid = currentCid;
        return this;
    }

    /**
     * 剧本分支节点
     */
    public Map<Integer, List<Integer>> getCurrentScript() {
        return currentScript;
    }

    /**
     * 剧本分支节点
     */
    public CurrentDatingBean setCurrentScript(Map<Integer, List<Integer>> currentScript) {
        this.currentScript = currentScript;
        return this;
    }

    /**
     * 剧本id
     */
    public int getScriptId() {
        return scriptId;
    }

    /**
     * 剧本id
     */
    public CurrentDatingBean setScriptId(int scriptId) {
        this.scriptId = scriptId;
        return this;
    }

    /**
     * 被选择的节点
     */
    public List<Integer> getSelectedNode() {
        return selectedNode;
    }

    /**
     * 被选择的节点
     */
    public CurrentDatingBean setSelectedNode(List<Integer> selectedNode) {
        this.selectedNode = selectedNode;
        return this;
    }

    /**
     * 看板娘id
     */
    public List<Integer> getRoleIds() {
        return roleIds;
    }

    /**
     * 看板娘id
     */
    public CurrentDatingBean setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
        return this;
    }
}
