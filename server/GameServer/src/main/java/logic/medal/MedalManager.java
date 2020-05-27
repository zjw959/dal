package logic.medal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CMedalMsg;
import org.game.protobuf.s2c.S2CMedalMsg.MedalInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import data.GameDataManager;
import data.bean.MedalCfgBean;
import event.Event;
import event.IEventListener;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EEventType;
import logic.constant.GameErrorCode;
import logic.medal.bean.Medal;
import logic.msgBuilder.MedalMsgBuilder;
import logic.support.MessageUtils;
import utils.EventUtil;
import utils.ExceptionEx;
import utils.TimeUtil;

public class MedalManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, ICreatePlayerInitialize, IEventListener, ITick {
    private static final Logger LOGGER = Logger.getLogger(MedalManager.class);
    /** 勋章 */
    private Map<Integer, Medal> medals = new HashMap<>();
    /** 当前装备的勋章id */
    private int medalId;

    public void reqActivateMedals() {
        if (!isOpen()) {
            MessageUtils.returnEmptyBody();
            return;
        }
        MessageUtils.send(player, MedalMsgBuilder.getMedalInfos(medals, ChangeType.DEFAULT));
    }

    public void reqEquipMedal(int cid) {
        if (!isOpen()) {
            MessageUtils.throwCondtionError(GameErrorCode.OUTSIDE_DATING_OUT_OF_TIME, "功能未开放");
            return;
        }
        Medal medal = medals.get(cid);
        if (medal == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "不存在此勋章" + cid);
            return;
        }
        Medal oldMedal = null;
        if (getNowMedal() != null) {
            oldMedal = getNowMedal();
            oldMedal.takeOff();
        }
        medal.equip();
        medalId = medal.getCid();
        calAtt();
        MedalMsgBuilder.notifyMedals(player, ChangeType.UPDATE, medal, oldMedal);
        S2CMedalMsg.RespEquipMedal.Builder builder = S2CMedalMsg.RespEquipMedal.newBuilder();
        builder.setSuccess(true);
        MessageUtils.send(player, builder);
    }

    public void reqTakeOffMedal(int cid) {
        Medal medal = medals.get(cid);
        if (medal == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "不存在此勋章" + cid);
            return;
        }
        if (getNowMedal() == null || medalId != cid) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "要求所卸下的勋章和当前装备的勋章不符合" + cid);
            return;
        }
        medal.takeOff();
        calAtt();
        MedalMsgBuilder.notifyMedals(player, ChangeType.UPDATE, medal);
        S2CMedalMsg.RespTakeOffMedal.Builder builder = S2CMedalMsg.RespTakeOffMedal.newBuilder();
        builder.setSuccess(true);
        MessageUtils.send(player, builder);
    }

    /**
     * 计算属性
     */
    public void calAtt() {

    }

    @Override
    public void createPlayerInitialize() {

    }

    @Override
    public void registerPerformed(Player player) {
        player.registerEventListener(EEventType.MEDIA_EVENT.value(), this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void eventPerformed(Event event) {
        if (!isOpen()) {
            return;
        }
        List<MedalCfgBean> medalCfgList = GameDataManager.getMedalCfgBeans();
        List<Medal> actiList = new ArrayList<>();
        for (MedalCfgBean medalcfg : medalCfgList) {
            // 配置完成参数
            Map params = medalcfg.getAccessway();
            if (params == null || params.isEmpty()) {
                continue;
            }
            // 可存在多个条件
            Set<String> keySet = params.keySet();
            boolean bool = true;
            for (String condition : keySet) {
                bool &= trigger(params, event, condition, bool);
            }
            if (bool) {
                Date effectTime = TimeUtil.addMinute(new Date(), medalcfg.getEffectivetime());
                Medal medal = new Medal(medalcfg.getId(), medalcfg.getStar(), medalcfg.getQuality(),
                        effectTime.getTime(), false);
                actiList.add(medal);
            }
        }
        List<MedalInfo> infoBuilder = new ArrayList<>();
        // 真正获取
        for (Medal medal : actiList) {
            Medal _medal = medals.get(medal.getCid());
            MedalInfo info = null;
            if (_medal == null) {
                medals.put(medal.getCid(), medal);
                info = MedalMsgBuilder.createMedalInfo(medal, ChangeType.ADD);
            } else {
                // 直接重置有效时间
                _medal.setEffectTime(medal.getEffectTime());
                info = MedalMsgBuilder.createMedalInfo(_medal, ChangeType.UPDATE);
            }
            infoBuilder.add(info);
        }
        // 通知
        MedalMsgBuilder.notifyMedals(player, infoBuilder);
    }

    /**
     * 添加勋章
     */
    public void addMedal(MedalCfgBean medalcfg) {
        if (!isOpen()) {
            return;
        }
        Date effectTime = TimeUtil.addMinute(new Date(), medalcfg.getEffectivetime());
        Medal medal = new Medal(medalcfg.getId(), medalcfg.getStar(), medalcfg.getQuality(),
                effectTime.getTime(), false);
        Medal _medal = medals.get(medal.getCid());
        MedalInfo info = null;
        List<MedalInfo> infoBuilder = new ArrayList<>();
        if (_medal == null) {
            medals.put(medal.getCid(), medal);
            info = MedalMsgBuilder.createMedalInfo(medal, ChangeType.ADD);
        } else {
            // 直接重置有效时间
            _medal.setEffectTime(medal.getEffectTime());
            info = MedalMsgBuilder.createMedalInfo(_medal, ChangeType.UPDATE);
        }
        infoBuilder.add(info);
        // 通知
        MedalMsgBuilder.notifyMedals(player, infoBuilder);
    }

    @SuppressWarnings("rawtypes")
    public boolean trigger(Map params, Event event, String contidion, boolean bool) {
        return EventUtil.checkParams(event, params, contidion, bool);
    }

    /**
     * 开放条件
     */
    public boolean isOpen() {
        return true;
    }

    public Map<Integer, Medal> getMedals() {
        return medals;
    }

    public void setMedals(Map<Integer, Medal> medals) {
        this.medals = medals;
    }

    public int getMedalId() {
        return medalId;
    }

    public void setMedalId(int medalId) {
        this.medalId = medalId;
    }

    public Medal getNowMedal() {
        return medals.get(medalId);
    }

    @Override
    public void tick() {
        try {
            checkTimeOut();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }

    }

    public void checkTimeOut() {
        if (!isOpen()) {
            return;
        }
        if (medals == null || medals.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        List<Medal> delMedal = new ArrayList<>();
        for (Medal medal : medals.values()) {
            if (medal.getEffectTime() > now) {
                continue;
            }
            delMedal.add(medal);
        }
        for (Medal _medal : delMedal) {
            medals.remove(_medal.getCid());
        }
        MedalMsgBuilder.notifyMedals(player, delMedal, ChangeType.DELETE);
    }
}
