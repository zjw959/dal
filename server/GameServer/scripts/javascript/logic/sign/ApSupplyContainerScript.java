package javascript.logic.sign;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import data.GameDataManager;
import data.bean.EnergySupplementCfgBean;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.sign.ApSupplyContainer;
import logic.sign.IApSupplyContainerScript;
import thread.base.GameInnerHandler;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import utils.TimeUtil;

/***
 * 
 * 体力补给容器脚本
 * @author lihongji
 *
 */
public class ApSupplyContainerScript extends IApSupplyContainerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ENERGY_CONTAINER_SCRIPT.Value();
    }

    
    //初始化
    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        List<EnergySupplementCfgBean> list = GameDataManager.getEnergySupplementCfgBeans();
        long[] energyTime = new long[list.size() * 3];
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            EnergySupplementCfgBean bean = list.get(i);
            Map<Integer, Integer> time = bean.getTime();
            for (Entry<Integer, Integer> entery : time.entrySet()) {
                energyTime[index] = entery.getKey() * TimeUtil.SECOND;
                energyTime[index + 1] = entery.getValue() * TimeUtil.SECOND;
            }
            energyTime[index + 2] = bean.getId();
            index += 3;
        }
        ApSupplyContainer.getInstance().setEnergyTime(energyTime);
    }

    //检测是否能够刷新 
    @Override
    public boolean checkSendEnergyTime(long[] energyTime, long nextTime) {
        if (energyTime == null)
            return false;
        long timeNow = System.currentTimeMillis();
        long todayStart = TimeUtil.getTheZeroClock(new Date());
        if (timeNow > (todayStart + TimeUtil.SECOND + energyTime[energyTime.length - 2]))
            return false;
        if (timeNow >= nextTime) {
            long[] sendEnergyTime = new long[2];
            int giftId = 0;
            for (int i = energyTime.length - 1; i >= 0; i -= 3) {
                if (timeNow < (todayStart + energyTime[i - 1])) {
                    nextTime = todayStart + energyTime[i - 1];
                    sendEnergyTime[0] = todayStart + energyTime[i - 2];
                    sendEnergyTime[1] = todayStart + energyTime[i - 1];
                    giftId = (int) energyTime[i];
                }
                if (timeNow < (todayStart + energyTime[i - 2])) {
                    nextTime = todayStart + energyTime[i - 2];
                    sendEnergyTime[0] = todayStart + energyTime[i - 2];
                    sendEnergyTime[1] = todayStart + energyTime[i - 1];
                    giftId = (int) energyTime[i];
                }
            }
            ApSupplyContainer.getInstance().initData(sendEnergyTime, nextTime, giftId);
            return true;
        }
        return false;

    }

    /**
     * 验证是否刷新体力活动
     */
    @Override
    public void push(long energyTime[], long nextTime) {
        if (energyTime == null)
            init();
        if (checkSendEnergyTime(energyTime, nextTime)) {
            List<Player> onlinePlayers = PlayerManager.getAllPlayers();
            for (Player player : onlinePlayers) {
                PlayerProcessor processor = PlayerProcessorManager.getInstance()
                        .getProcessorByUserName(player.getUserName());
                if (processor != null) {
                    processor.executeInnerHandler(new LApSupplyPush(player));
                }
            }
        }
    }

    

    /**玩家线程内部刷新体力活动**/
    class LApSupplyPush extends GameInnerHandler {
        Player player;

        public LApSupplyPush(Player player) {
            this.player = player;
        }

        @Override
        public void action() throws Exception {
            player.getApSupplyManager().refreshEnery();
        }
    }

    
}
