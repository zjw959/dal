package javascript.logic.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import logic.basecore.IAcrossDay;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.IPlayerScript;
import logic.character.bean.Player;
import logic.character.handler.LCloseSessionHandler;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.gloabl.GlobalService;
import server.GameServer;
import server.ServerConfig;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessor.PlayerDirtyTimeComparator;
import thread.player.PlayerProcessorManager;
import utils.ChannelUtils;
import utils.DateEx;
import utils.ExceptionEx;
import utils.TimeUtil;


/**
 * 
 * @Description 检查清理sessionClosing状态的卡死player
 * @author LiuJiang
 * @date 2018年8月13日 下午2:43:03
 *
 */
public class PlayerScript extends IPlayerScript {
    private static final Logger LOGGER = Logger.getLogger(PlayerScript.class);

    @Override
    public void clearClosedSession(Player player) {
        try {
            // 清理sessionClosing状态的卡死player
            if (!GameServer.getInstance().isShutDown() && player.isOnline()
                    && ChannelUtils.isDisconnectChannel(player.getCtx())) {
                if (player.isClosingTime == 0l) {
                    player.isClosingTime = System.currentTimeMillis();
                } else if (player.isClosingTime != -1l) {
                    if ((System.currentTimeMillis() - player.isClosingTime) > ServerConfig
                            .getInstance().getClientChannelIdleTime() * TimeUtil.SECOND * 1.5) {
                        player.isClosingTime = -1l;
                        LOGGER.error("ClearClosingSession playerId:" + player.getPlayerId()
                                + ", lineId:" + player.getLineIndex());
                        LCloseSessionHandler handler = new LCloseSessionHandler(player);
                        PlayerProcessorManager.getInstance().getProcessor(player.getLineIndex())
                                .executeInnerHandler(handler);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("SessionClosing Exception:" + ExceptionEx.e2s(e));
        }
        // 此段代码为修复线上全服邮件bug（玩家无法领取全服邮件，缓存邮件共享）已修复
        // try {
        // if (player.isOnline()) {
        // MailMgr manager = player.getMailManager();
        // Map<Long, BaseMailDBBean> mails = manager.getAllMails();
        // Map<Long, Integer> serverMailsStatus = manager.getServerMailsStatus();
        //
        // if (mails != null) {
        // boolean isNotify = false;
        // List<BaseMailDBBean> fixMails = Lists.newArrayList();
        // for (BaseMailDBBean bean : mails.values()) {
        // if (bean instanceof ServerMailDBBean
        // && bean.getStatus() != MailConstant.STATUS_DEFAULT.getCode()
        // && !serverMailsStatus.containsKey(bean.getId())) {
        // isNotify = true;
        // ServerMailDBBean newMail = ((ServerMailDBBean) bean).copy();
        // newMail.setStatus(MailConstant.STATUS_DEFAULT.getCode());
        // mails.put(newMail.getId(), newMail);
        // fixMails.add(newMail);
        // serverMailsStatus.put(newMail.getId(),
        // MailConstant.STATUS_DEFAULT.getCode());
        // break;
        // }
        // }
        // if (isNotify) {
        // LOGGER.info("----fixServerMailSuc------> pid:" + player.getPlayerId());
        // MailInfoList.Builder msg =
        // MailMsgBuilder.createMailInfoList(ChangeType.UPDATE, fixMails);
        // MessageUtils.send(player, msg);
        // } else {
        // // // 恢复删除的指定id的全服邮件
        // // long mailId = 6290291827453952L;
        // // if (!mails.containsKey(mailId)
        // // && serverMailsStatus.containsKey(mailId)
        // // && serverMailsStatus.get(mailId) == MailConstant.STATUS_DELETE
        // // .getCode()) {
        // // ServerMailDBBean mail =
        // // MailService.getInstance().getServerMails().get(mailId);
        // // // LOGGER.info("---00------- > pid:" + player.getPlayerId());
        // // if (mail != null
        // // && player.getCreateTime() <= mail.getCreate_date().getTime()) {
        // // ServerMailDBBean newMail = mail.copy();
        // // newMail.setStatus(MailConstant.STATUS_DEFAULT.getCode());
        // // mails.put(newMail.getId(), newMail);
        // // serverMailsStatus.put(newMail.getId(),
        // // MailConstant.STATUS_DEFAULT.getCode());
        // // List<BaseMailDBBean> newMails = Lists.newArrayList();
        // // newMails.add(newMail);
        // // MailInfoList.Builder msg =
        // // MailMsgBuilder.createMailInfoList(ChangeType.ADD, newMails);
        // // MessageUtils.send(player, msg);
        // // LOGGER.info("----fixDelServerMailSuc------> pid:"
        // // + player.getPlayerId());
        // // }
        // // }
        // }
        // }
        // }
        // } catch (Exception e) {
        // LOGGER.error("FixServerMails Exception:" + ExceptionEx.e2s(e));
        // }
    }

    @Override
    protected void tick(Player player, long currentTime) {
        Iterator<ITick> iterator = player.getTickManagers();
        while (iterator.hasNext()) {
            ITick manager = iterator.next();
            try {
                long now = System.currentTimeMillis();
                manager.tick();
                long time = (System.currentTimeMillis() - now);
                if (time > 50) {
                    LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " managerTick,time:" + time
                            + " name: " + manager.getClass().getSimpleName() + ",playerId:"
                            + player.getPlayerId());
                }

                if (manager instanceof IAcrossDay) {
                    PlayerBaseFunctionManager baseFunctionManager =
                            (PlayerBaseFunctionManager) manager;
                    ((IAcrossDay) manager).tickAcrossDay(baseFunctionManager, currentTime, false);
                }
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e) + manager.getClass().getSimpleName()
                        + player.logInfo());
            }
        }
    }

    @Override
    protected void fixTickBug(Player player, long currentTime) {

    }

    @Override
    protected void initFixBug(Player player) {

    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.PlAYER.Value();
    }

    @Override
    public void saveBackTick(PlayerProcessor ps) {
        long begin = System.currentTimeMillis();

        PlayerProcessorManager manager = PlayerProcessorManager.getInstance();
        if (begin - ps.getLastSaveTime() < GlobalService.getInstance().getSavebackCheckInterval()) {
            return;
        }
        ps.setLastSaveTime(begin);

        // 离线玩家在内存中保留的最长时间
        final int maxOfflineTime = GlobalService.getInstance().getMaxOffLineTimes();
        // 每个逻辑线程离线玩家最大缓存
        final int maxNumOffline = GlobalService.getOffLineMax();
        // 每次离线回存最大数量
        int maxSaveNumOffline = manager.getMaxSaveNumOffline();

        TreeSet<Player> offlinePlayers = ps.getTreeSetByOfflineTime();
        TreeSet<Player> onlinePlayers = ps.getTreeSetByLastSaveTime();

        Iterator<Map.Entry<Integer, Player>> it = ps.getPlayerRegistry().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Player> entry = it.next();
            if (entry == null || entry.getValue() == null) {
                LOGGER.error(ConstDefine.LOG_ERROR_PROGRAMMER_PREFIX
                        + "tick to save playerProcessor exits null entry.");
                it.remove();
                continue;
            }
            // 分离离线玩家和在线玩家
            Player player = entry.getValue();
            if (player.getState() == Player.PlayerState.OFFLINE) {
                offlinePlayers.add(player);
            } else if (player.getState() == Player.PlayerState.ONLINE) {
                onlinePlayers.add(player);
            }
        }
        // 离线玩家回存和清理
        // 溢出量
        int offlineOverflow = offlinePlayers.size() - maxNumOffline;
        // 如果溢出量翻倍,则加大回存数量,避免内存数据过大溢出.
        int multiple = (int) Math.ceil(offlinePlayers.size() / (double) maxNumOffline);
        if (multiple <= 0) {
            multiple = 1;
        }
        if (multiple >= 3) {
            multiple = 3;
        }

        long offDirtyLimtTime = 5;
        long onlineLimitTime = 10;
        // 动态时间
        long offLimitTime = 5 * multiple;
        long totalMaxTime = offLimitTime + offDirtyLimtTime + onlineLimitTime;

        maxSaveNumOffline = multiple * maxSaveNumOffline;
        if (multiple > 1) {
            maxSaveNumOffline = maxSaveNumOffline * 2;
        }
        long offBeingTime = System.currentTimeMillis();
        long offlineCount = 0l;
        Iterator<Player> dIt = offlinePlayers.iterator();
        while (dIt.hasNext()) {
            Player player = dIt.next();
            try {
                long _time = System.currentTimeMillis() - offBeingTime;
                if (_time >= offLimitTime) {
                    break;
                }

                // 清除最大N小时离线玩家
                long offlineTimes = begin - player.getOfflineTime();
                if (offlineTimes >= maxOfflineTime) {
                    offlineCount++;
                    boolean isSave = ps.flushSingleOneOfflinePlayer(player, begin);
                    if (isSave) {
                        --maxSaveNumOffline;
                    }
                    --offlineOverflow;
                    dIt.remove();
                    // 已经到了本次离线玩家回存数量的上限,不再处理
                    if (maxSaveNumOffline <= 0) {
                        break;
                    }
                    continue;
                }

                // 如果当前离线玩家数量溢出了
                if (offlineOverflow > 0) {
                    offlineCount++;
                    if (begin - player.getOfflineTime() < DateEx.TIME_MINUTE * 3) {
                        continue;
                    }
                    boolean isSave = ps.flushSingleOneOfflinePlayer(player, begin);
                    if (isSave) {
                        --maxSaveNumOffline;
                    }
                    --offlineOverflow;
                    dIt.remove();
                    // 已经到了本次回存的上限，不再处理
                    if (maxSaveNumOffline <= 0) {
                        break;
                    }
                    continue;
                }

                // 在线时间不满足并且没有溢出
                break;
            } catch (Exception e) {
                LOGGER.error("回存离线玩家数据异常,id:" + player.getPlayerId() + ",name:"
                        + player.getPlayerName() + "\n" + ExceptionEx.e2s(e));
            }
        }
        long offLineTime = System.currentTimeMillis() - offBeingTime;


        // 离线玩家回存
        Collections.sort(new ArrayList<Player>(offlinePlayers), new PlayerDirtyTimeComparator());
        long dirtyTimeBeing = System.currentTimeMillis();
        long dirtyCount = 0;
        long dirtySaveCount = 0;
        Iterator<Player> _dirtydIt = offlinePlayers.iterator();
        while (_dirtydIt.hasNext()) {
            Player player = _dirtydIt.next();
            long offlineTimes = begin - player.getOfflineTime();

            try {
                // 还没有达到本次离线玩家回存的上限
                if (maxSaveNumOffline <= 0) {
                    break;
                }

                if (System.currentTimeMillis() - dirtyTimeBeing >= offDirtyLimtTime) {
                    break;
                }

                if ((begin - player.getDirtyCalculateTime()) < 1000 * 60) {
                    break;
                }

                dirtyCount++;
                if (!ps.isDirty(player)) {
                    continue;
                }

                dirtySaveCount++;

                // 如果离线时间超过10分钟,则主动日志报错
                // 理论上只会有刚离线的时候,可能有hanlder尚未执行完成,会有零碎的数据修改发生.
                if (offlineTimes > DateEx.TIME_MINUTE * 10) {
                    LOGGER.warn("save offline player over offlinetime => "
                            + player.logInfo()
                            + " offlineTime="
                            + DateEx.format(new Date(player.getOfflineTime()),
                                    DateEx.fmt_yyyy_MM_dd_HH_mm_ss_sss));
                }

                player.setLastSavebackTime(begin);
                player.save(false);
                --maxSaveNumOffline;
            } catch (Exception e) {
                LOGGER.error("回存离线玩家dirty数据异常,id:" + player.getPlayerId() + ",name:"
                        + player.getPlayerName() + "\n" + ExceptionEx.e2s(e));
            }
        }
        long dirtyTime = System.currentTimeMillis() - dirtyTimeBeing;


        // 在线玩家的单次回存数量
        int onlineSavebackMaxNum = manager.getMaxSaveNumOnline();
        // 定时回存的间隔
        int savebackInterval = GlobalService.getInstance().getSaveBackInterval();

        // 控制在xxms内完成
        long _onlineLimitTime = totalMaxTime - (System.currentTimeMillis() - offBeingTime);
        if (onlineLimitTime < _onlineLimitTime) {
            onlineLimitTime = _onlineLimitTime;
        }
        Iterator<Player> oIt = onlinePlayers.iterator();
        long onlineTimeBegin = System.currentTimeMillis();
        long onlineCount = 0;
        while (oIt.hasNext()) {
            // if ((System.currentTimeMillis() - onlineTimeBegin) >= onlineLimitTime) {
            // break;
            // }

            onlineCount++;
            // if (onlineSavebackMaxNum <= 0) {
            // break;
            // }

            Player player = oIt.next();
            try {
                long interval = (begin - player.getLastSavebackTime());
                if (interval >= savebackInterval) {
                    player.setLastSavebackTime(begin);
                    player.save();
                    --onlineSavebackMaxNum;
                    if (onlineSavebackMaxNum <= 0) {
                        break;
                    }
                    continue;
                }
                // 已经按时间排过序,接下来都不可能超过了
                break;
            } catch (Exception e) {
                LOGGER.error("回存在线玩家数据异常" + player.getPlayerId() + "\n" + ExceptionEx.e2s(e));
            }
        }
        long onlineTime = System.currentTimeMillis() - onlineTimeBegin;

        long time = System.currentTimeMillis() - begin;
        long saveTime = onlineTime + offLineTime + dirtyTime;
        if (time > totalMaxTime + 10) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + "threadName:"
                    + Thread.currentThread().getName() + " 回存tick检查执行时间超长. time:" + time
                    + ",saveTime:" + saveTime + ",totalMaxTime:" + totalMaxTime + ",offLineTime:"
                    + offLineTime + ",offlineCount:" + offlineCount + ",onlineTime:" + onlineTime
                    + ",onlineLimitTime:" + onlineLimitTime + ",onlineCount:" + onlineCount
                    + ",dirtyTime:" + dirtyTime + ",dirtyCount:" + dirtyCount + ",dirtySaveCount:"
                    + dirtySaveCount + ",lineSize:" + ps.getPlayerRegistry().size()
                    + ", onlineSize:" + onlinePlayers.size() + ",offlinesize:"
                    + offlinePlayers.size() + ",maxSaveNumOffline:["
                    + (multiple > 1 ? manager.getMaxSaveNumOffline() * 2 : maxSaveNumOffline) + ","
                    + maxSaveNumOffline + "],onlineSavebackMaxNum:["
                    + manager.getMaxSaveNumOnline() + "," + onlineSavebackMaxNum + "]");
        }
    }
}
