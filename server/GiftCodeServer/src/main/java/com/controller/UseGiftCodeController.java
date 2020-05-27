package com.controller;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.constant.ExceptionEx;
import com.constant.GiftConstant;
import com.controller.abs.AbstractHandler;
import com.enity.InvitationCode;
import com.enity.PackageInfo;
import com.enity.StatusCode;
import com.enity.UsedList;
import com.enity.chinese.ChineseCode;
import com.enity.chinese.ChineseUse;
import com.enity.forever.ForeverCode;
import com.enity.forever.ForeverUse;
import com.service.impl.ChineseCodeService;
import com.service.impl.ChineseUseService;
import com.service.impl.ForeverCodeService;
import com.service.impl.ForeverUseService;
import com.service.impl.InvitationCodeService;
import com.service.impl.PackageInfoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller // 表示这是一个Controller
@RequestMapping(path = "/package")
public class UseGiftCodeController extends AbstractHandler {
    ReentrantLock lock = new ReentrantLock(false);
    Object object = new Object();

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PackageInfoService packageInfoService;
    @Autowired
    private InvitationCodeService codeService;
    @Autowired
    private ForeverCodeService foreverCodeService;
    @Autowired
    private ForeverUseService foreverUseService;
    @Autowired
    private ChineseCodeService chineseCodeService;
    @Autowired
    private ChineseUseService chineseUseService;

    @ApiOperation(value = "使用礼包码", notes = "使用后返回礼包码有效/无效信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", value = "礼包码ID", required = true,
                    dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "roleId", value = "玩家ID", required = true,
                    dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "channelId", value = "有效渠道",
                    required = true, dataType = "String"),})
    @RequestMapping(path = {"/use"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String useGiftCode(HttpServletRequest request) throws Throwable {
        StatusResult result = null;
        String text = null;
        long time = System.currentTimeMillis();
        try {
            String encodeId = request.getParameter("id");
            String code = URLDecoder.decode(encodeId, "utf-8");
            Integer roleId = Integer.parseInt(request.getParameter("roleId"));
            String channelId = request.getParameter("channelId");
            result = use(code, roleId, channelId);
            logger.info("{} request use code :{} , result:{} ", roleId, code, result);
        } catch (Exception e) {
            logger.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
        } finally {
            JSONObject jsonObj = new JSONObject();
            if (result == null) {
                jsonObj.put(KEY_STATUS, StatusCode.ERROR);
            } else {
                String msg = toDetailsText(result.code);
                if (result.code != StatusCode.OK) {
                    jsonObj.put(KEY_STATUS, msg);
                } else {
                    jsonObj.put(KEY_STATUS, msg);
                    jsonObj.put("goodsInfo", result.packageInfo.getExpression());
                }
            }
            text = jsonObj.toJSONString();
        }
        logger.info("{} request use times:{} , result:{} ",
                (System.currentTimeMillis() - time) + "ms");
        return text;
    }

    public StatusResult use(String code, Integer roleId, String channelId) throws Throwable {
        boolean isForever = foreverCodeService.isExist(code);
        if (isForever) { // 永久礼包码
            return useForeverCode(foreverCodeService.findByKey(code), roleId, channelId);
        } else if (isSpecialCode(code)) { // 特殊礼包码，中文礼包
            return useSpecialCode(code, roleId, channelId);
        } else {
            return useNormalCode(code, roleId, channelId);
        }
    }

    /**
     * 判定是否为特殊邀请码，如：中文邀请码等
     * 
     * @param code
     * @return
     */
    public boolean isSpecialCode(String code) {
        if (chineseCodeService.findByKey(code) != null) {
            return true;
        }
        return false;
    }

    public StatusResult useForeverCode(ForeverCode foreverCode, Integer roleId, String channelId)
            throws Throwable {
        ForeverUse existCode = foreverUseService.findByKey(foreverCode.getId() + ":" + roleId);

        PackageInfo packInfo = packageInfoService.findByKey(foreverCode.getPackageId());
        if (existCode != null) {
            if (!StringUtils.isEmpty(packInfo.getChannel()) && !StringUtils.isEmpty(channelId)) {
                String[] channelIds = packInfo.getChannel().split(",");
                if (!isValidChannel(channelIds, channelId)) {
                    logger.error("channelId is invalid.{},{},{}", foreverCode, roleId, packInfo);
                    return StatusResult.INVALID_CHANNEL;
                }
            }
            if (packInfo.getCount() > 0 && existCode.getGot() > 0) {
                logger.error("forever code use time out of bound.{},{},{}", foreverCode, roleId,
                        existCode);
                return StatusResult.ALREADY_USE;
            }
            existCode.addGot();
            foreverUseService.save(existCode);
        } else {
            existCode = new ForeverUse();
            existCode.setId(foreverCode.getId());
            existCode.setPackageId(packInfo.getId());
            existCode.setRoleId(roleId);
            existCode.setGot(0);
            existCode.addGot();
            foreverUseService.save(existCode);
        }
        foreverCode.addGot();
        foreverCodeService.save(foreverCode);
        return new StatusResult(StatusCode.OK, existCode, packInfo);
    }

    public StatusResult useSpecialCode(String code, Integer roleId, String channelId)
            throws Throwable {
        ChineseCode specialCode = chineseCodeService.findByKey(code);
        if (specialCode == null) {
            logger.error("special code not found.{},{}", code, roleId);
            return StatusResult.CODE_NOT_FOUND;
        }

        PackageInfo packInfo = packageInfoService.findByKey(specialCode.getPackageId());
        if (packInfo == null) {
            logger.error("package not found.{},{}", code, roleId);
            return StatusResult.PACK_NOT_FOUND;
        }

        Date now = new Date();
        if (packInfo.getBeginTime() != null && now.before(packInfo.getBeginTime())) {
            logger.error("package have not start.{},{},{}", code, roleId, packInfo);
            return StatusResult.PACK_NOT_START;
        } else {
            if (packInfo.getEndTime() != null && now.after(packInfo.getEndTime())) {
                logger.error("package is timeout.{},{},{}", code, roleId, packInfo);
                return StatusResult.PACK_TIME_OUT;
            }
        }

        ChineseUse existCode = chineseUseService.findByKey(code + ":" + roleId);
        if (existCode != null) {
            logger.error("special code already used.{},{},{}", code, roleId, existCode);
            return StatusResult.ALREADY_USE;
        } else {
            if (!StringUtils.isEmpty(packInfo.getChannel()) && !StringUtils.isEmpty(channelId)) {
                String[] channelIds = packInfo.getChannel().split(",");
                if (!isValidChannel(channelIds, channelId)) {
                    logger.error("channelId is invalid.{},{},{}", code, roleId, packInfo);
                    return StatusResult.INVALID_CHANNEL;
                }
            }
            existCode = new ChineseUse();
            existCode.setId(code);
            existCode.setPackageId(packInfo.getId());
            existCode.setRoleId(roleId);
            existCode.setGotTime(new Date());
        }
        synchronized (object) {
            List<ChineseUse> count = chineseUseService.findByList(packInfo.getId());
            if (count.size() >= packInfo.getCount()) {
                logger.error("package is use.{},{},{}", code, roleId, packInfo);
                return StatusResult.TIME_LESS_THAN;
            }
            count.add(existCode);
        }

        chineseUseService.save(existCode);
        return new StatusResult(StatusCode.OK, existCode, packInfo);
    }

    /**
     * 使用普通礼包码（随机礼包码）,一个普通礼包码只能使用一次,使用后会被标记已使用
     */
    public StatusResult useNormalCode(String code, Integer roleId, String channelId)
            throws Throwable {
        UsedList usedList = codeService.findByKey(roleId);
        InvitationCode invitationCode = usedList.findById(code);
        if (invitationCode != null) {
            logger.error("invit code already used.{},{}", code, roleId);
            return StatusResult.ALREADY_USE;
        }
        invitationCode = codeService.find(code);
        if (invitationCode == null) {// 改礼包码为空
            logger.error("invit code not found.{},{}", code, roleId);
            return StatusResult.CODE_NOT_FOUND;
        }
        PackageInfo packInfo = packageInfoService.findByKey(invitationCode.getPackageId());
        if (packInfo == null) {
            logger.error("package not found.{},{}", code, roleId);
            return StatusResult.PACK_NOT_FOUND;
        }
        Date now = new Date();
        if (packInfo.getBeginTime() != null && now.before(packInfo.getBeginTime())) {
            logger.error("package have not start.{},{},{}", code, roleId, packInfo);
            return StatusResult.PACK_NOT_START;
        } else {
            if (packInfo.getEndTime() != null && now.after(packInfo.getEndTime())) {
                logger.error("package is timeout.{},{},{}", code, roleId, packInfo);
                return StatusResult.PACK_TIME_OUT;
            }
        }
        int samePackCount = usedList.count(packInfo.getId());
        if (samePackCount >= packInfo.getCount()) {
            logger.error("already use same package.{},{},{}", code, roleId, packInfo);
            return StatusResult.ALREADY_USE_SAME_PACK;
        }

        boolean enable = isEnable(roleId, invitationCode.getPackageId());
        if (!enable) {
            logger.error("invit code disable.{},{},{}", invitationCode, roleId, packInfo);
            return StatusResult.TIME_LESS_THAN;
        }
        if (!StringUtils.isEmpty(packInfo.getChannel()) && !StringUtils.isEmpty(channelId)) {
            String[] channelIds = packInfo.getChannel().split(",");
            if (!isValidChannel(channelIds, channelId)) {
                logger.error("channelId is invalid.{},{},{}", code, roleId, packInfo);
                return StatusResult.INVALID_CHANNEL;
            }
        }

        synchronized (invitationCode) {
            if (invitationCode.getGot() > 0) {
                logger.error("invit code already used.{},{}", code, roleId);
                return StatusResult.ALREADY_USE;
            }
            invitationCode.addGot();
            invitationCode.setRoleId(roleId);
            invitationCode.setGotTime(new Date());
        }
        codeService.save(invitationCode);
        usedList.add(invitationCode);
        return new StatusResult(StatusCode.OK, invitationCode, packInfo);

    }

    /**
     * TODO 暂时不考虑同一个礼包(不是礼包码)的限制次数
     */
    public boolean isEnable(int roleId, int packageId) {
        // TODO
        // PackageInfo packInfo = packageInfoService.findByKey(packageId);
        // int dayinterval = packInfo.getDayInterval();
        // if (dayinterval < 1) {
        // return true;
        // }
        // // TODO
        // // InvitationCode code = queryByLastCode(roleId, packageId);
        // InvitationCode code = null;
        // if (code == null)
        // return true;
        //
        boolean enable = true;
        // Date lasttime = code.getGotTime();
        // Calendar calendar = new GregorianCalendar();
        // calendar.setTime(lasttime);
        // calendar.add(Calendar.DATE, dayinterval);
        // Date timeget = calendar.getTime();
        // enable = timeget.before(new Date());
        return enable;
    }

    private boolean isValidChannel(String[] channels, String channel) {
        for (String _channelId : channels) {
            if (_channelId.equals(channel))
                return true;
        }
        return false;
    }

    private String toDetailsText(int code) {
        switch (code) {
            case StatusCode.OK:
                return GiftConstant.OK;
            case StatusCode.ERROR:
                return GiftConstant.ERROR;
            case StatusCode.ALREADY_USE_SAME_PACK:
                return GiftConstant.ALREADY_USE_SAME_PACK;
            case StatusCode.ALREADY_USE:
                return GiftConstant.ALREADY_USE;
            case StatusCode.CODE_NOT_FOUND:
                return GiftConstant.CODE_NOT_FOUND;
            case StatusCode.PACK_NOT_FOUND:
                return GiftConstant.PACK_NOT_FOUND;
            case StatusCode.PACK_NOT_START:
                return GiftConstant.PACK_NOT_START;
            case StatusCode.PACK_TIME_OUT:
                return GiftConstant.PACK_TIME_OUT;
            case StatusCode.TIME_LESS_THAN:
                return GiftConstant.TIME_LESS_THAN;
            case StatusCode.INVALID_CHANNEL:
                return GiftConstant.INVALID_CHANNEL;
        }
        return "";
    }

    public static class StatusResult implements Serializable {
        private static final long serialVersionUID = -8164361750040952078L;
        public final int code;
        public final Object object;
        public final PackageInfo packageInfo;

        /**
         * 未知错误
         */
        public static final StatusResult ERROR = new StatusResult(StatusCode.ERROR);;
        /**
         * 礼包码已经使用过
         */
        public static final StatusResult ALREADY_USE = new StatusResult(StatusCode.ALREADY_USE);;
        /**
         * 已经使用过同一个礼包
         */
        public static final StatusResult ALREADY_USE_SAME_PACK =
                new StatusResult(StatusCode.ALREADY_USE_SAME_PACK);;
        /**
         * 使用次数超过了上限
         */
        public static final StatusResult TIME_LESS_THAN =
                new StatusResult(StatusCode.TIME_LESS_THAN);;
        /**
         * 找不到邀请码
         */
        public static final StatusResult CODE_NOT_FOUND =
                new StatusResult(StatusCode.CODE_NOT_FOUND);;
        /**
         * 找不到礼包
         */
        public static final StatusResult PACK_NOT_FOUND =
                new StatusResult(StatusCode.PACK_NOT_FOUND);;
        /**
         * 礼包仍未开放兑换
         */
        public static final StatusResult PACK_NOT_START =
                new StatusResult(StatusCode.PACK_NOT_START);;
        /**
         * 礼包过期
         */
        public static final StatusResult PACK_TIME_OUT =
                new StatusResult(StatusCode.PACK_TIME_OUT);;

        /**
         * 无效的渠道号
         */
        public static final StatusResult INVALID_CHANNEL =
                new StatusResult(StatusCode.INVALID_CHANNEL);;

        /**
         * @param code
         * @param object
         * @param packageInfo
         */
        public StatusResult(int code, Object object, PackageInfo packageInfo) {
            super();
            this.code = code;
            this.object = object;
            this.packageInfo = packageInfo;
        }

        /**
         * @param code
         */
        public StatusResult(int code) {
            super();
            this.code = code;
            this.object = null;
            this.packageInfo = null;
        }

        @Override
        public String toString() {
            return "StatusResult [code=" + code + ", "
                    + (object != null ? "object=" + object + ", " : "")
                    + (packageInfo != null ? "packageInfo=" + packageInfo : "") + "]";
        }

    }
}
