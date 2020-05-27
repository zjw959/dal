package logic.support;

import gm.db.pay.bean.PayDBBean;
import logic.character.bean.Player;
import logic.log.bean.BattleLog;
import logic.log.bean.CityGameLog;
import logic.log.bean.DatingLog;
import logic.log.bean.HeroAngelLog;
import logic.log.bean.HeroCrystalLog;
import logic.log.bean.HeroLog;
import logic.log.bean.ItemGenLog;
import logic.log.bean.ItemLog;
import logic.log.bean.PayBaseLog;
import logic.log.bean.PaySuccessLog;
import logic.log.bean.PlayLog;
import logic.log.bean.RoleLog;
import logic.log.bean.SummonLog;
import server.ServerConfig;
import db.log.bean.LogBaseBean;
import db.log.bean.LogItemConsumeBean;
import db.log.bean.LogItemGainBean;

public class LogBeanFactory {
    /**
     * 对于日志基础Bean添加数据
     * 
     * @param player
     * @param bean
     * @return
     */
    private static void insertBaseInfo(Player player, LogBaseBean bean) {
        bean.setPlatform("");
        bean.setRoleid(player.getPlayerId());
        bean.setRolename(player.getPlayerName());
        bean.setServerid(ServerConfig.getInstance().getServerId());
        bean.setTime(System.currentTimeMillis());
        bean.setUserName(player.getUserName());
        bean.setViplevel(player.getVipLevel());
        bean.setRolelevel(player.getLevel());
    }

    public static LogItemConsumeBean createItemConsumLog(Player player, int templateId, long uid,
            int usenum, int surplusnum, int reason, long logId) {
        LogItemConsumeBean logBean = new LogItemConsumeBean();
        logBean.setReason(reason);
        logBean.setUid(uid);
        logBean.setTemplateId(templateId);
        logBean.setUsenum(usenum);
        logBean.setSurplusnum(surplusnum);
        logBean.setLogid(logId);
        insertBaseInfo(player, logBean);
        return logBean;
    }

    public static LogItemGainBean createItemGainLog(Player player, long uid, int templateId,
            int gainNum, int totalNum, int reason, long logId) {
        LogItemGainBean logBean = new LogItemGainBean();
        logBean.setGainnum(gainNum);
        logBean.setUid(uid);
        logBean.setTemplateId(templateId);
        logBean.setReason(reason);
        logBean.setLogid(logId);
        logBean.setTotalnum(totalNum);
        insertBaseInfo(player, logBean);
        return logBean;
    }

    public static ItemGenLog createItemGainLog(Player player, long uid, int templateId,
            int gainNum, int totalNum, int reason, boolean fromMail, String ext) {
        ItemGenLog logBean = new ItemGenLog(player);
        logBean.setGainNum(gainNum);
        logBean.setUid(uid);
        logBean.setTemplateId(templateId);
        logBean.setReason(reason);
        logBean.setTotalNum(totalNum);
        logBean.setFromMail(fromMail);
        logBean.setExt(ext);
        return logBean;
    }

    public static ItemLog createItemConsumLog(Player player, int templateId, long uid, int usenum,
            int surplusnum, int reason, String ext) {
        ItemLog logBean = new ItemLog(player);
        logBean.setReason(reason);
        logBean.setUid(uid);
        logBean.setTemplateId(templateId);
        logBean.setUsenum(usenum);
        logBean.setSurplusnum(surplusnum);
        logBean.setExt(ext);
        return logBean;
    }


    /**
     * 约会日志
     * 
     * @param player
     * @param templateId
     * @param heroTmplId
     * @param result
     * @param itemCG
     * @param reason
     * @param ext
     * @return
     */
    public static DatingLog createDatingLog(Player player, int templateId, int heroTmplId,
            int itemCG, int reason, String ext) {
        DatingLog logBean = new DatingLog(player);
        logBean.setReason(reason);
        logBean.setTemplateId(templateId);
        logBean.setHeroTmplId(heroTmplId);
        logBean.setItemCG(itemCG);
        logBean.setExt(ext);
        logBean.setTime(System.currentTimeMillis());
        return logBean;
    }


    /**
     * 看板娘日志
     * 
     * @param player
     * @param heroTmplId
     * @param favorChange
     * @param favor
     * @param reason
     * @param ext
     * @return
     */
    public static RoleLog createRoleLog(Player player, int heroTmplId, int favorChange, int favor,
            int reason, String ext) {
        RoleLog logBean = new RoleLog(player);
        logBean.setReason(reason);
        logBean.setHeroTmplId(heroTmplId);
        logBean.setFavorChange(favorChange);
        logBean.setFavor(favor);
        logBean.setExt(ext);
        return logBean;
    }

    /**
     * 作战日志
     * 
     * @param player
     * @param battleId
     * @param result
     * @param teamInfo
     * @param reason
     * @param ext
     * @return
     */
    public static BattleLog createBattleLog(Player player, int battleId, int result,
            String teamInfo, int reason, String ext) {
        BattleLog logBean = new BattleLog(player);
        logBean.setReason(reason);
        logBean.setExt(ext);
        logBean.setBattleId(battleId);
        logBean.setResult(result);
        logBean.setTeamInfo(teamInfo);
        return logBean;
    }

    /**
     * 小玩法日志 功能-打工、温泉、制作、抽卡、祈愿、洗练
     * 
     * @param player
     * @param reason
     * @param ext
     * @return
     */
    public static PlayLog createPlayLog(Player player, int reason, String ext) {
        PlayLog logBean = new PlayLog(player);
        logBean.setReason(reason);
        logBean.setExt(ext);
        return logBean;
    }

    /**
     * 小玩法日志 功能-打工、温泉
     * 
     * @param player
     * @param reason
     * @param gameId
     * @return
     */
    public static CityGameLog createCityGameLog(Player player, int reason, int gameId) {
        CityGameLog logBean = new CityGameLog(player);
        logBean.setReason(reason);
        logBean.setGameId(gameId);
        logBean.setTime(System.currentTimeMillis());
        return logBean;
    }


    /**
     * 精灵日志
     * 
     * @param player
     * @param uid
     * @param templateId
     * @param expChange
     * @param exp
     * @param levelChange
     * @param level
     * @param starChange
     * @param star
     * @param reason
     * @param ext
     * @return
     */
    public static HeroLog createHeroLog(Player player, int templateId, int expChange,
            long exp, int levelChange, int level, int starChange, int star, int reason, String ext) {
        HeroLog logBean = new HeroLog(player);
        logBean.setTemplateId(templateId);
        logBean.setExpChange(expChange);
        logBean.setExp(exp);
        logBean.setLevelChange(levelChange);
        logBean.setLevel(level);
        logBean.setStarChange(starChange);
        logBean.setStar(star);
        logBean.setReason(reason);
        logBean.setExt(ext);
        return logBean;
    }
    
    public static SummonLog createSummonLog(Player player, int reason, String ext) {
        SummonLog logBean = new SummonLog(player);
        logBean.setReason(reason);
        logBean.setExt(ext);
        return logBean;
    }

    /**
     * 充值日志
     * 
     * @param player
     * @return
     */
    public static PayBaseLog createPayLog(Player player, PayDBBean bean) {
        PaySuccessLog logBean = new PaySuccessLog(player);
        logBean.setChannel_order_id(bean.getChannel_order_id());
        logBean.setOrder_id(bean.getOrder_id());
        logBean.setChannel_id(bean.getChannel_id());
        logBean.setChannel_appid(bean.getChannel_appid());
        logBean.setStatus(bean.getStatus());
        logBean.setUser_name(bean.getUser_name());
        logBean.setPlayer_id(bean.getPlayer_id());
        logBean.setItem_id(bean.getItem_id());
        logBean.setSell_amount(bean.getSell_amount());
        logBean.setPay_amount(bean.getPay_amount());
        logBean.setExtinfo(bean.getExtinfo());
        logBean.setCreate_time(bean.getCreate_time().getTime());
        logBean.setModify_time(bean.getModify_time().getTime());
        logBean.setCallback_time(bean.getCallback_time().getTime());
        logBean.setHtnonce(bean.getHtnonce());
        logBean.setHttoken(bean.getHttoken());
        return logBean;
    }
    
    /**
     * 精灵天使日志
     * @param player
     * @param heroId
     * @param angelAwakeChange
     * @param angelAwake
     * @param angelStrategy
     * @param angelSkill
     * @param reason
     * @param ext
     * @return
     */
    public static HeroAngelLog createHeroAngelLog(Player player, int heroId, int heroLevel, int angelAwakeChange,
            int angelAwake, int angelStrategy, int angelSkill, int reason, String ext) {
        HeroAngelLog logBean = new HeroAngelLog(player);
        logBean.setHeroId(heroId);
        logBean.setHeroLevel(heroLevel);
        logBean.setAngelAwakeChange(angelAwakeChange);
        logBean.setAngelAwake(angelAwake);
        logBean.setAngelStrategy(angelStrategy);
        logBean.setAngelSkill(angelSkill);
        logBean.setReason(reason);
        logBean.setExt(ext);
        return logBean;
    }
    
    /**
     * 精灵结晶日志
     * @param player
     * @param heroId
     * @param rarity
     * @param gridId
     * @param reason
     * @param ext
     * @return
     */
    public static HeroCrystalLog createHeroCrystalLog(Player player, int heroId, int rarity, int gridId, int reason, String ext) {
        HeroCrystalLog logBean = new HeroCrystalLog(player);
        logBean.setHeroId(heroId);
        logBean.setRarity(rarity);
        logBean.setGridId(gridId);
        logBean.setReason(reason);
        logBean.setExt(ext);
        return logBean;
    }

}
