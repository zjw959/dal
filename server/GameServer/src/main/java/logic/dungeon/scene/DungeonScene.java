package logic.dungeon.scene;

import java.util.Date;

import logic.constant.DungeonSceneStatus;

import org.apache.log4j.Logger;

/**
 * 副本场景
 * 
 * @author Alan
 *
 */
public abstract class DungeonScene {


    static final Logger log = Logger.getLogger(DungeonScene.class);

    /** 场景ID */
    private String sceneId;
    private int sceneCfgId;
    /** 随机种子 */
    private int randomSeed;
    /** 场景状态 */
    private DungeonSceneStatus status = DungeonSceneStatus.AWAIT_START;
    /** 开始场景时间 */
    private Date startSceneDate;

    public DungeonScene init() {
        sceneInit();
        putScene();
        return this;
    }

    /** 场景开始 */
    public void start() {
        // 应由外部进行判定,内部返回执行状态
        // ToolError.isAndTrue(GameErrorCode.FIGHT_NON_WAITING, status !=
        // DungeonSceneStatus.AWAIT_START);
        init();
        if (sceneStart()) {
            setStartSceneDate(new Date());
            status = DungeonSceneStatus.OPERATING;
        }
    }

    /** 放置场景至相应容器 */
    protected abstract void putScene();

    /** 销毁当前场景 */
    protected abstract void destroyScene();

    /** 初始化当前场景 */
    protected abstract void sceneInit();

    /** 场景开始运作 */
    protected abstract boolean sceneStart();

    // 停止场景
    protected void sceneStop() {
        status = DungeonSceneStatus.STOPPING;
    }

    protected abstract boolean sceneOver(boolean normal);

    /**
     * 场景结束
     * 
     * @param normal true:正常结束 false:异常结束
     */
    public void over(boolean normal) {
        if (normal) {
            if (status == DungeonSceneStatus.OPERATING) {
                sceneStop();
            }
        } else {
            status = DungeonSceneStatus.STOPPING;
        }

        if (status == DungeonSceneStatus.STOPPING) {
            if (sceneOver(normal)) {
                status = DungeonSceneStatus.STOPPED;
                destroyScene();
            }
        }
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public int getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(int randomSeed) {
        this.randomSeed = randomSeed;
    }

    public DungeonSceneStatus getStatus() {
        return status;
    }

    public void setStatus(DungeonSceneStatus status) {
        this.status = status;
    }

    public Date getStartSceneDate() {
        return startSceneDate;
    }

    public void setStartSceneDate(Date startSceneDate) {
        this.startSceneDate = startSceneDate;
    }

    public int getSceneCfgId() {
        return sceneCfgId;
    }

    public void setSceneCfgId(int sceneCfgId) {
        this.sceneCfgId = sceneCfgId;
    }


}
