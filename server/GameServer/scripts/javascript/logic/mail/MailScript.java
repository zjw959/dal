package javascript.logic.mail;

import gm.constant.MailConstant;
import gm.db.mail.bean.BaseMailDBBean;
import gm.db.mail.bean.PlayerMailDBBean;
import gm.db.mail.bean.ServerMailDBBean;
import gm.db.mail.dao.MailDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import logic.bag.BagManager;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.mail.IMailScript;
import logic.mail.MailMgr;
import logic.mail.MailService;
import logic.mail.handler.LLoginGetMailsCBHandler;
import logic.mail.handler.LReceiveNewMailsHandler;
import logic.msgBuilder.MailMsgBuilder;
import logic.support.MessageUtils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SMailMsg.MailHandleMsg;
import org.game.protobuf.c2s.C2SMailMsg.MailOperationType;
import org.game.protobuf.s2c.S2CMailMsg.MailInfoList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import thread.base.GameBaseProcessor;
import thread.player.PlayerProcessorManager;
import utils.CommonUtil;
import utils.DateEx;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.ToolMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;

public class MailScript extends IMailScript {
    private static final Logger LOGGER = Logger.getLogger(MailScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.MAIL_SCRIPT.Value();
    }

    @Override
    public void systemGetMails() {
        // 从邮件DB获取大于指定时间段的新邮件
        boolean isFirst = false;
        long nowTime = System.currentTimeMillis();
        long lastEndTime = MailService.getInstance().getLastEndTime();
        long startTime = lastEndTime;
        if (lastEndTime == -1) {// 首次获取
            isFirst = true;
            startTime = 0;
        }
        long endTime = nowTime;
        // 获取指定时间段内的邮件
        int distime = 5 * 1000;// 误差处理
        Date startDate = new Date(startTime - distime);
        Date endDate = new Date(endTime + distime);

        // 缓存全服邮件
        List<ServerMailDBBean> serverMails;
        Map<Integer, List<PlayerMailDBBean>> playerMailsMap =
                new HashMap<Integer, List<PlayerMailDBBean>>();
        if (isFirst) {
            DiscreteDataCfgBean cfg = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAIL);
            int limitDay = ToolMap.getInt("overdueDay", cfg.getData(), 7);// 邮件过期时间（天）
            // 第一次取全服邮件时，取所有邮件
            serverMails = MailDao.selectAllServerMails();
            for (int i = serverMails.size() - 1; i >= 0; i--) {
                ServerMailDBBean mail = serverMails.get(i);
                if (mail.getCreate_date().getTime() > endTime) {
                    endTime = mail.getCreate_date().getTime();
                }
                if (DateEx.isTimeout(mail.getCreate_date().getTime(), limitDay
                        * DateUtils.MILLIS_PER_DAY)) {
                    serverMails.remove(i);// 过滤过期邮件
                }
            }
        } else {

            // 获取系统新邮件
            serverMails = MailDao.selectServerMailsByTime(startDate, endDate);
            // 获取玩家新邮件
            List<PlayerMailDBBean> playerMails =
                    MailDao.selectPlayerMailsByTime(startDate, endDate);
            for (PlayerMailDBBean mail : playerMails) {
                int playerId = (int) mail.getReceiver_id();
                List<PlayerMailDBBean> list = playerMailsMap.get(playerId);
                if (list == null) {
                    list = Lists.newArrayList();
                    playerMailsMap.put(playerId, list);
                }
                list.add(mail);
            }
        }
        MailService.getInstance().setLastEndTime(endTime);
        // 缓存全服邮件
        for (ServerMailDBBean mail : serverMails) {
            ((MailService) (MailService.getInstance())).putServerMail(mail);
        }
        // 处理在线玩家邮件
        if (serverMails.size() > 0) {// 有全服邮件
            List<Player> players = PlayerManager.getAllPlayers();
            for (Player player : players) {
                if (player == null || !player.isOnline()
                        || player.getMailManager().getAllMails() == null) {
                    continue;
                }
                // 通知在线玩家有新邮件到了
                List<BaseMailDBBean> newMails = Lists.newArrayList();
                // 是否同时有单人邮件
                List<PlayerMailDBBean> list = playerMailsMap.get(player.getPlayerId());
                if (list != null && list.size() > 0) {
                    newMails.addAll(list);
                }
                for (ServerMailDBBean mail : serverMails) {
                    newMails.add(mail.copy());
                }
                // 丢给玩家线程处理新邮件
                GameBaseProcessor proc =
                        PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex());
                proc.executeInnerHandler(new LReceiveNewMailsHandler(player, newMails));
            }

        } else {// 只有单人邮件
            for (Entry<Integer, List<PlayerMailDBBean>> entry : playerMailsMap.entrySet()) {
                int playerId = entry.getKey();
                List<PlayerMailDBBean> list = entry.getValue();
                Player player = PlayerManager.getPlayerByPlayerId(playerId);
                if (player == null || !player.isOnline()
                        || player.getMailManager().getAllMails() == null) {
                    continue;
                }
                // 通知在线玩家有新邮件到了
                List<BaseMailDBBean> newMails = Lists.newArrayList();
                newMails.addAll(list);
                // 丢给玩家线程处理新邮件
                GameBaseProcessor proc =
                        PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex());
                proc.executeInnerHandler(new LReceiveNewMailsHandler(player, newMails));
            }
        }
    }

    @Override
    public boolean loginGetMailsProce(int playerId, LLoginGetMailsCBHandler handler) {
        boolean isSuccess = true;
        List<BaseMailDBBean> mails = Lists.newArrayList();
        List<PlayerMailDBBean> playerMails = MailDao.selectPlayerMailsByReceiverId(playerId);
        mails.addAll(playerMails);
        Map<Long, ServerMailDBBean> serverMails =
                ((MailService) (MailService.getInstance())).getServerMails();
        for (ServerMailDBBean bean : serverMails.values()) {
            ServerMailDBBean newMail = bean.copy();
            newMail.setStatus(MailConstant.STATUS_DEFAULT.getCode());
            mails.add(newMail);
        }
        handler.setMails(mails);
        return isSuccess;
    }

    @Override
    public void loginGetMails(Player player, List<BaseMailDBBean> mails) {
        if (player == null || !player.isOnline()) {
            return;
        }
        MailMgr mailManager = player.getMailManager();
        Map<Long, BaseMailDBBean> allMails = new ConcurrentHashMap<Long, BaseMailDBBean>();// 登录获取的邮件成功返回后再设置缓存
        mailManager.setAllMails(allMails);
        MailInfoList.Builder msg = null;
        if (mails == null) {
            msg = MailMsgBuilder.createMailInfoList(ChangeType.DEFAULT, null);
        } else {
            DiscreteDataCfgBean cfg = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAIL);
            int limitDay = ToolMap.getInt("overdueDay", cfg.getData(), 7);// 邮件过期时间（天）
            Map<Long, Integer> serverMailsStatus = mailManager.getServerMailsStatus();
            List<Long> deletePlayerMails = Lists.newArrayList();
            List<PlayerMailDBBean> autoMails = Lists.newArrayList();// 自动提取的邮件
            for (BaseMailDBBean mail : mails) {
                if (mail instanceof ServerMailDBBean) {
                    if (mail.getCreate_date().getTime() < player.getCreateTime()) {
                        continue;// 跳过创建角色之前的全服邮件
                    }
                    Integer status = serverMailsStatus.get(mail.getId());
                    if (status != null) {
                        if (status == MailConstant.STATUS_DELETE.getCode()) {
                            continue;// 跳过已删除的全服邮件
                        }
                        mail.setStatus(status);
                    } else {
                        mail.setStatus(MailConstant.STATUS_DEFAULT.getCode());
                    }
                    // 是否过期
                    boolean timeOut =
                            DateEx.isTimeout(mail.getCreate_date().getTime(), limitDay
                                    * DateUtils.MILLIS_PER_DAY);
                    if (timeOut) {
                        continue;
                    }
                } else if (mail instanceof PlayerMailDBBean) {
                    if (mail.getStatus() == MailConstant.STATUS_DELETE.getCode()) {
                        deletePlayerMails.add(mail.getId());
                        continue;
                    }
                    // 是否过期
                    boolean timeOut =
                            DateEx.isTimeout(mail.getCreate_date().getTime(), limitDay
                                    * DateUtils.MILLIS_PER_DAY);
                    if (timeOut) {
                        deletePlayerMails.add(mail.getId());
                        continue;
                    }
                    // 是否是自动提取的邮件
                    if (mail.getStatus() == MailConstant.STATUS_AUTO.getCode()) {
                        autoMails.add((PlayerMailDBBean) mail);
                        continue;
                    }
                }
                allMails.put(mail.getId(), mail);
            }
            // 处理自动提取邮件
            autoRewardMails(player, autoMails);
            // 批量删除玩家过期邮件
            ((MailService) (MailService.getInstance())).deletePlayerMails(deletePlayerMails);
            // 检查并删除超出上限的邮件
            checkMailMaxLimit(mailManager);
            msg = MailMsgBuilder.createMailInfoList(ChangeType.DEFAULT, allMails.values());
        }
        MessageUtils.send(player, msg);
    }

    @Override
    public void receiveNewMails(Player player, List<BaseMailDBBean> newMails) {
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        MailMgr mailManager = player.getMailManager();
        Map<Long, BaseMailDBBean> allMails = mailManager.getAllMails();
        if (allMails == null) {
            return;// allMails为空，说明玩家登录时请求的邮件还没有回来，所以跳过不处理
        }
        List<PlayerMailDBBean> autoMails = Lists.newArrayList();// 自动提取的邮件
        List<BaseMailDBBean> newList = Lists.newArrayList();
        for (BaseMailDBBean mail : newMails) {
            if (!allMails.containsKey(mail.getId())) {
                if (mail instanceof PlayerMailDBBean
                        && mail.getStatus() == MailConstant.STATUS_AUTO.getCode()) {
                    autoMails.add((PlayerMailDBBean) mail);
                    continue;
                }
                newList.add(mail);
                allMails.put(mail.getId(), mail);
            }
        }
        // 处理自动提取邮件
        autoRewardMails(player, autoMails);
        if (newList.size() > 0) {
            MailInfoList.Builder msg = MailMsgBuilder.createMailInfoList(ChangeType.ADD, newList);
            // 检查并删除超出上限的邮件
            List<BaseMailDBBean> limitMails = checkMailMaxLimit(mailManager);
            if (limitMails != null) {
                MailInfoList.Builder msg1 =
                        MailMsgBuilder.createMailInfoList(ChangeType.DELETE, limitMails);
                msg.addAllMails(msg1.getMailsList());
            }
            MessageUtils.send(player, msg);
        }
    }

    /** 操作邮件 */
    @Override
    public void handleMails(Player player, MailHandleMsg msg) {
        MailOperationType type = msg.getType();
        List<String> ids = msg.getIdsList();
        List<Long> mailIds = new ArrayList<>(ids.size());
        for (String idStr : ids) {
            mailIds.add(Long.valueOf(idStr));
        }
        switch (type) {
            case READ:
                readMail(player, mailIds);
                break;
            case RECEIVE:
                rewardMail(player, mailIds);
                break;
            case DELETE:
                deleteMail(player, mailIds);
                break;
            default:
                break;
        }
    }

    /**
     * 读取邮件
     * 
     * @param mailIds
     */
    private void readMail(Player player, List<Long> mailIds) {
        MailMgr mailManager = player.getMailManager();
        Map<Long, BaseMailDBBean> allMails = mailManager.getAllMails();
        Map<Long, Integer> serverMailsStatus = mailManager.getServerMailsStatus();
        List<BaseMailDBBean> updateMails = Lists.newArrayList();
        List<PlayerMailDBBean> updatePlayerMails = Lists.newArrayList();
        for (Long id : mailIds) {
            BaseMailDBBean mail = allMails.get(id);
            if (mail == null || mail.getStatus() != MailConstant.STATUS_DEFAULT.getCode()) {
                continue;
            }
            mail.setStatus(MailConstant.STATUS_READ.getCode());
            mail.setModify_date(new Date());
            if (mail instanceof ServerMailDBBean) {
                // 全服邮件
                serverMailsStatus.put(id, MailConstant.STATUS_READ.getCode());
            } else if (mail instanceof PlayerMailDBBean) {
                // 玩家单人邮件
                updatePlayerMails.add((PlayerMailDBBean) mail);
            }
            updateMails.add(mail);
        }
        // 交给邮件线程更新邮件
        ((MailService) (MailService.getInstance())).updatePlayerMails(updatePlayerMails);
        MailInfoList.Builder mailInfoList =
                MailMsgBuilder.createMailInfoList(ChangeType.UPDATE, updateMails);
        MessageUtils.send(player, mailInfoList);
        MessageUtils.returnEmptyBody();
    }

    /**
     * 领取邮件奖励
     * 
     * @param mailIds
     */
    private void rewardMail(Player player, List<Long> mailIds) {
        List<BaseMailDBBean> updateMails = Lists.newArrayList();
        List<PlayerMailDBBean> updatePlayerMails = Lists.newArrayList();
        // 先做检查
        MailMgr mailManager = player.getMailManager();
        Map<Long, BaseMailDBBean> allMails = mailManager.getAllMails();
        Map<Long, Integer> serverMailsStatus = mailManager.getServerMailsStatus();
        BagManager bagManager = player.getBagManager();
        Map<Integer, Integer> items = Maps.newHashMap();
        for (Long id : mailIds) {
            BaseMailDBBean mail = allMails.get(id);
            if (mail == null || mail.getStatus() == MailConstant.STATUS_RECEIVE.getCode()
                    || mail.getStatus() == MailConstant.STATUS_DELETE.getCode()) {
                MessageUtils.throwCondtionError(GameErrorCode.MAIL_STATUS_ERROR,
                        "mail status error.id=" + id);
                return;
            }
            if (mail.getItems() == null) {
                continue;// 无奖励可领
            }
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            JsonObject json = GsonUtils.toJsonObject(mail.getItems());
            for (Entry<String, JsonElement> entry : json.entrySet()) {
                map.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsInt());
            }
            CommonUtil.changeMap(items, map);
            if (!bagManager.checkBagSpace(items)) {
                MessageUtils.throwCondtionError(GameErrorCode.BAG_SPACE_IS_NOT_ENOUGH);
                return;
            }
            updateMails.add(mail);
        }

        for (BaseMailDBBean mail : updateMails) {
            mail.setStatus(MailConstant.STATUS_RECEIVE.getCode());
            mail.setModify_date(new Date());
            if (mail instanceof ServerMailDBBean) {
                // 全服邮件
                serverMailsStatus.put(mail.getId(), MailConstant.STATUS_RECEIVE.getCode());
            } else if (mail instanceof PlayerMailDBBean) {
                // 玩家单人邮件
                updatePlayerMails.add((PlayerMailDBBean) mail);
            }
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            JsonObject json = GsonUtils.toJsonObject(mail.getItems());
            for (Entry<String, JsonElement> entry : json.entrySet()) {
                map.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsInt());
            }
            // 发放奖励
            if (map.size() > 0) {
                EReason reason = EReason.ITEM_REWARD_MAIL;
                try{
                    int reasonValue = Integer.parseInt(mail.getInfo());
                    EReason rea = EReason.getReason(reasonValue);
                    if (rea != null) {
                        reason = rea;// 转换成原始的reason
                    }
                }catch(Exception e){
                    LOGGER.error(ExceptionEx.e2s(e));  
                }
                bagManager.addItems(map, true, reason, EReason.ITEM_REWARD_MAIL.name(),
                        String.valueOf(mail.getId()));
            }
        }
        // 交给邮件线程更新邮件
        ((MailService) (MailService.getInstance())).updatePlayerMails(updatePlayerMails);
        MailInfoList.Builder mailInfoList =
                MailMsgBuilder.createMailInfoList(ChangeType.UPDATE, updateMails);
        MessageUtils.send(player, mailInfoList);
        MessageUtils.returnEmptyBody();
    }

    /**
     * 删除邮件
     * 
     * @param mailIds
     */
    private void deleteMail(Player player, List<Long> mailIds) {
        MailMgr mailManager = player.getMailManager();
        Map<Long, BaseMailDBBean> allMails = mailManager.getAllMails();
        Map<Long, Integer> serverMailsStatus = mailManager.getServerMailsStatus();
        List<BaseMailDBBean> deleteMails = Lists.newArrayList();
        List<Long> deletePlayerMails = Lists.newArrayList();
        for (Long id : mailIds) {
            BaseMailDBBean mail = allMails.get(id);
            // 状态检查
            if (mail == null || mail.getStatus() == MailConstant.STATUS_DEFAULT.getCode()
                    || mail.getStatus() == MailConstant.STATUS_DELETE.getCode()) {
                MessageUtils.throwCondtionError(GameErrorCode.MAIL_STATUS_ERROR,
                        "mail status error.id=" + id);
                return;
            }
            // 有附件未领取
            if (mail.getItems() != null
                    && mail.getStatus() != MailConstant.STATUS_RECEIVE.getCode()) {
                MessageUtils.throwCondtionError(GameErrorCode.NOT_RECEIVE_ITEMS,
                        "mail items not reward error.id=" + id);
                return;
            }
            deleteMails.add(mail);
        }
        for (BaseMailDBBean mail : deleteMails) {
            if (mail instanceof ServerMailDBBean) {
                // 全服邮件
                serverMailsStatus.put(mail.getId(), MailConstant.STATUS_DELETE.getCode());
            } else if (mail instanceof PlayerMailDBBean) {
                // 玩家单人邮件
                deletePlayerMails.add(mail.getId());
            }
            allMails.remove(mail.getId());
        }

        // 交给邮件线程删除邮件
        ((MailService) (MailService.getInstance())).deletePlayerMails(deletePlayerMails);
        MailInfoList.Builder mailInfoList =
                MailMsgBuilder.createMailInfoList(ChangeType.DELETE, deleteMails);
        MessageUtils.send(player, mailInfoList);
        MessageUtils.returnEmptyBody();
    }

    /**
     * 检查并删除超出上限的邮件
     * 
     * @return
     */
    public List<BaseMailDBBean> checkMailMaxLimit(MailMgr mailManager) {
        Map<Long, BaseMailDBBean> allMails = mailManager.getAllMails();
        if (allMails == null || allMails.isEmpty()) {
            return null;
        }
        DiscreteDataCfgBean cfg = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAIL);
        int max = ToolMap.getInt("maxEmail", cfg.getData(), 100);// 邮件最大保存数量
        int overflowMail = allMails.size() - max;
        List<BaseMailDBBean> overflowMails = new ArrayList<>(Math.max(0, overflowMail));
        if (overflowMail > 0) {
            List<BaseMailDBBean> mails = Lists.newArrayList(allMails.values());
            List<Long> deletePlayerMails = Lists.newArrayList();
            Collections.sort(mails, (o1, o2) -> o1.getCreate_date().compareTo(o2.getCreate_date()));
            overflowMails.addAll(mails.subList(0, overflowMail));
            mails.removeAll(overflowMails);

            for (BaseMailDBBean mail : overflowMails) {
                if (mail instanceof ServerMailDBBean) {
                    // 全服邮件
                    mailManager.getServerMailsStatus().put(mail.getId(),
                            MailConstant.STATUS_DELETE.getCode());
                } else if (mail instanceof PlayerMailDBBean) {
                    // 玩家单人邮件
                    deletePlayerMails.add(mail.getId());
                }
            }
            // 交给邮件线程删除邮件
            ((MailService) (MailService.getInstance())).deletePlayerMails(deletePlayerMails);
            return overflowMails;
        }
        return null;
    }


    /**
     * 自动提取邮件附件奖励
     */
    private void autoRewardMails(Player player, List<PlayerMailDBBean> autoMails) {
        // 处理自动提取邮件
        if (autoMails != null && autoMails.size() > 0) {
            List<Long> deletePlayerMails = Lists.newArrayList();
            // 先整合所有附件
            for (BaseMailDBBean mail : autoMails) {
                deletePlayerMails.add(mail.getId());
                if (mail.getItems() == null) {
                    continue;// 无奖励可领
                }
                JsonObject json = GsonUtils.toJsonObject(mail.getItems());
                Map<Integer, Integer> items = Maps.newHashMap();
                for (Entry<String, JsonElement> entry : json.entrySet()) {
                    int itemId = Integer.parseInt(entry.getKey());
                    int itemNum = entry.getValue().getAsInt();
                    CommonUtil.changeMap(items, itemId, itemNum);
                }
                player.getBagManager().addItems(items, true, EReason.ITEM_AUTO_REWARD_MAIL, false,
                        mail.getInfo());
            }
            // 交给邮件线程删除邮件
            ((MailService) (MailService.getInstance())).deletePlayerMails(deletePlayerMails);
        }
    }
}
