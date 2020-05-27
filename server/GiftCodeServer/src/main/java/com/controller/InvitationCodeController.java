package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import com.constant.GiftConstant;
import com.controller.abs.AbstractHandler;
import com.controller.helper.CreateInvitCodeStruct;
import com.controller.helper.QueryInvitCodeSQL;
import com.enity.InvitationCode;
import com.enity.batch.BatchInfo;
import com.enity.forever.ForeverCode;
import com.service.impl.BatchInfoService;
import com.service.impl.ForeverCodeService;
import com.service.impl.InvitationCodeService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import util.HTRandomString;
import util.IDUtil;

@Controller
@RequestMapping(path = "package/code")
public class InvitationCodeController extends AbstractHandler {
    private static final String structType = "struct";
    private static final String queryType = "queryInvitCodeSQL";
    private static Logger log = LoggerFactory.getLogger(InvitationCodeController.class);
    @Autowired
    private InvitationCodeService codeService;
    @Autowired
    private ForeverCodeService foreverCodeService;
    @Autowired
    private BatchInfoService batchInfoService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation(value = "创建礼包码", notes = "批量创建")
    @ApiImplicitParam(paramType = "body", name = structType, value = "礼包码参数实体CreateInvitCodeStruct",
            required = true, dataType = structType)
    @RequestMapping(path = {"/create"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String createInvitation(HttpServletRequest request) {
        int status = ERROR;
        JSONObject jsonObj = new JSONObject();
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object object = jsonObject.get(structType);
            JSONObject o = JSONObject.parseObject(object.toString());
            CreateInvitCodeStruct struct = JSON.toJavaObject(o, CreateInvitCodeStruct.class);
            @SuppressWarnings("unused")
            List<InvitationCode> resultList = batchCreate(struct);
            status = OK;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            jsonObj.put(KEY_STATUS, status);
        }
        String text = jsonObj.toJSONString();
        return text;
    }

    @ApiOperation(value = "条件查询礼包码信息", notes = "返回分页信息和内容")
    @ApiImplicitParam(paramType = "body", name = queryType, value = "条件实体queryInvitCodeSQL",
            required = true, dataType = queryType)
    @RequestMapping(path = {"/query"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String findInvitation(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        String result = null;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object _object = jsonObject.get(queryType);
            JSONObject o = JSONObject.parseObject(_object.toString());
            QueryInvitCodeSQL queryInvitCodeSQL = JSON.toJavaObject(o, QueryInvitCodeSQL.class);
            InvitationCode code = queryInvitCodeSQL.toInvitationCode();
            Page<InvitationCode> qyPage = codeService.getInvitationCodeList(code,
                    queryInvitCodeSQL.getLimitStart(), queryInvitCodeSQL.getLimitLen());
            // 当前页内容
            List<InvitationCode> inviList = qyPage.getContent();
            // 总页数
            int totalPages = qyPage.getTotalPages();

            String jsonString = JSON.toJSONString(inviList, true);
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

    /**
     * 批量创建礼包码
     * 
     * @param struct
     */
    public List<InvitationCode> batchCreate(CreateInvitCodeStruct struct) throws Throwable {
        List<InvitationCode> result = new ArrayList<InvitationCode>(struct.getCount());
        InvitationCode newCode = null;
        String batchId = IDUtil.getCreateId();
        boolean isExist = batchInfoService.isExist(batchId);
        if (isExist) {
            batchId = IDUtil.getCreateId();
        }
        for (int i = 0; i < struct.getCount();) {
            newCode = create(struct);
            if (result.contains(newCode)) {
                continue;
            }

            if (struct.getType() == GiftConstant.TYPE_FOREVER) {
                result.add(newCode);
                ForeverCode foreverCode = newCode.toForeverCode();
                foreverCodeService.save(foreverCode);
            } else {
                try {
                    newCode.setBatchId(batchId);
                    codeService.save(newCode);;
                } catch (Throwable ex) {
                    logger.error("dump key {}", newCode);
                }
                result.add(newCode);
            }
            i++;
        }
        BatchInfo batchInfo = new BatchInfo(batchId, new Date());
        batchInfoService.save(batchInfo);
        return result;
    }

    /**
     * @param struct
     * @return
     */
    public InvitationCode create(CreateInvitCodeStruct struct) throws Throwable {
        InvitationCode code = new InvitationCode();
        code.setId(HTRandomString.generateString8());
        code.setPackageId(struct.getPackageId());
        code.setCreateTime(new Date());
        code.setGot(0);
        code.setRoleId(0);
        return code;
    }

}
