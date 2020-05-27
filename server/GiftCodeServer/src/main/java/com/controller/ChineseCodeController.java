package com.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.ExceptionEx;
import com.controller.abs.AbstractHandler;
import com.controller.helper.QueryChineseCode;
import com.controller.helper.QueryChineseUse;
import com.enity.PackageInfo;
import com.enity.chinese.ChineseCode;
import com.enity.chinese.ChineseUse;
import com.service.impl.ChineseCodeService;
import com.service.impl.ChineseUseService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(path = "/create/chinese")
public class ChineseCodeController extends AbstractHandler {
    private static Logger log = LoggerFactory.getLogger(ChineseCodeController.class);

    private static final String dataType = "chineseCode";
    private static final String queryDataType = "queryCCode";
    private static final String queryUseType = "queryUseCCode";
    @Autowired
    ChineseCodeService chineseCodeService;
    @Autowired
    private ChineseUseService chineseUseService;

    @ApiOperation(value = "创建中文礼包码", notes = "返会成功/失败")
    @ApiImplicitParam(paramType = "body", name = dataType, value = "中文礼包码chineseCode",
            required = true, dataType = dataType)
    @RequestMapping(path = {"/code"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String createInvitation(HttpServletRequest request) {
        int result = ERROR;
        JSONObject jsonObj = new JSONObject();
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object object = jsonObject.get(dataType);
            JSONObject o = JSONObject.parseObject(object.toString());
            ChineseCode chineseCode = JSON.toJavaObject(o, ChineseCode.class);
            chineseCodeService.save(chineseCode);
            result = OK;
        } catch (Exception e) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
        } finally {
            jsonObj.put(KEY_STATUS, result);
        }
        String text = jsonObj.toJSONString();
        return text;
    }

    @ApiOperation(value = "条件查询礼包信息", notes = "返回分页信息和内容")
    @ApiImplicitParam(paramType = "body", name = queryDataType, value = "条件实体QueryChineseCode",
            required = true, dataType = queryDataType)
    @RequestMapping(path = {"/query"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String findPackage(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String result = null;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object _object = jsonObject.get(queryDataType);
            JSONObject o = JSONObject.parseObject(_object.toString());
            QueryChineseCode queryCCode = JSON.toJavaObject(o, QueryChineseCode.class);
            Page<ChineseCode> qyPage = chineseCodeService.queryChineseCode(queryCCode,
                    queryCCode.getLimitStart(), queryCCode.getLimitLength());

            // 当前页内容
            List<ChineseCode> packList = qyPage.getContent();
            // 总页数
            int totalPages = qyPage.getTotalPages();
            String jsonString = JSON.toJSONString(packList, true);
            object.put("totalPages", totalPages);
            object.put("contest", jsonString);
            object.put(KEY_STATUS, OK);
        } catch (IOException e) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
            object.put(KEY_STATUS, ERROR);
        } finally {
            result = JSON.toJSONString(object, true);
        }
        return result;
    }

    @ApiOperation(value = "条件查询中文礼包使用记录", notes = "返回记录信息")
    @ApiImplicitParam(paramType = "body", name = queryUseType, value = "条件实体QueryChineseUse",
            required = true, dataType = queryUseType)
    @RequestMapping(path = {"/use/query"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String findChineseCodeUse(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String result = null;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object _object = jsonObject.get(queryUseType);
            JSONObject o = JSONObject.parseObject(_object.toString());
            QueryChineseUse queryCCode = JSON.toJavaObject(o, QueryChineseUse.class);
            Page<ChineseUse> qyPage = chineseUseService.queryChineseCode(queryCCode,
                    queryCCode.getLimitStart(), queryCCode.getLimitLength());

            // 当前页内容
            List<ChineseUse> packList = qyPage.getContent();
            // 总页数
            int totalPages = qyPage.getTotalPages();
            String jsonString = JSON.toJSONString(packList, true);
            object.put("totalPages", totalPages);
            object.put("contest", jsonString);
            object.put(KEY_STATUS, OK);
        } catch (IOException e) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
            object.put(KEY_STATUS, ERROR);
        } finally {
            result = JSON.toJSONString(object, true);
        }
        return result;
    }

    public static void main(String[] args) {
        System.err.println(PackageInfo.class.getSimpleName());
    }
}
