package com.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.ExceptionEx;
import com.controller.abs.AbstractHandler;
import com.enity.InvitationCode;
import com.enity.batch.BatchInfo;
import com.service.impl.BatchInfoService;
import com.service.impl.InvitationCodeService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(path = "/count")
public class CountCodeController extends AbstractHandler {

    private static final String queryDataType = "QueryBatch";
    @Autowired
    private BatchInfoService batchInfoService;
    @Autowired
    private InvitationCodeService invitationCodeService;
    private static Logger log = LoggerFactory.getLogger(CountCodeController.class);

    // @ApiOperation(value = "条件查询批次信息", notes = "返回分页信息和内容")
    // @ApiImplicitParam(paramType = "body", name = queryDataType, value = "条件实体QueryBatch",
    // required = true, dataType = queryDataType)
    // @RequestMapping(path = {"/query"}, method = {RequestMethod.POST, RequestMethod.GET})
    // public @ResponseBody String findBatch(HttpServletRequest request) {
    // JSONObject object = new JSONObject();
    // String result = null;
    // try {
    // String text = getRequestJson(request);
    // JSONObject jsonObject = JSONObject.parseObject(text);
    // Object _object = jsonObject.get(queryDataType);
    // JSONObject o = JSONObject.parseObject(_object.toString());
    // QueryBatch queryBatch = JSON.toJavaObject(o, QueryBatch.class);
    // Page<BatchInfo> qyPage = batchInfoService.query(queryBatch, queryBatch.getLimitStart(),
    // queryBatch.getLimitLength());
    //
    // // 当前页内容
    // List<BatchInfo> packList = qyPage.getContent();
    // // 总页数
    // int totalPages = qyPage.getTotalPages();
    // String jsonString = JSON.toJSONString(packList, true);
    // object.put("totalPages", totalPages);
    // object.put("contest", jsonString);
    // object.put(KEY_STATUS, OK);
    // } catch (IOException e) {
    // e.printStackTrace();
    // object.put(KEY_STATUS, ERROR);
    // } finally {
    // result = JSON.toJSONString(object, true);
    // }
    // return result;
    // }

    @ApiOperation(value = "统计批次使用情况", notes = "返回使用情况")
    @ApiImplicitParam(paramType = "body", name = "batchId", value = "批次号", required = true,
            dataType = "String")
    @RequestMapping(path = {"/numOfUses"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String countBatch(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String result = null;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object _object = jsonObject.get("batchId");
            String batchId = String.valueOf(_object);

            InvitationCode code = new InvitationCode();
            code.setBatchId(batchId);
            List<InvitationCode> codeList = invitationCodeService.getInvitationCodeList(code);
            BatchInfo batchInfo = batchInfoService.findByKey(batchId);
            int total = codeList.size();
            int useCount = getUseCount(codeList);
            // String jsonString = JSON.toJSONString(codeList, true);
            object.put(KEY_STATUS, OK);
            // 总计
            object.put("total", total);
            // 已使用个数
            object.put("useCount", useCount);
            // 批次号
            object.put("batchId", batchId);
            // 批次号创建时间
            object.put("createTime", batchInfo.getCreateTime());
            // 礼包码详细列表
            // object.put("contest", jsonString);
        } catch (Exception ex) {
            object.put(KEY_STATUS, ERROR);
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(ex));
        } finally {
            result = JSON.toJSONString(object, true);
        }
        return result;
    }

    public int getUseCount(List<InvitationCode> codeList) {
        int count = 0;
        for (InvitationCode code : codeList) {
            if (code.getRoleId() != 0) {
                count++;
            }
        }
        return count;
    }
}
