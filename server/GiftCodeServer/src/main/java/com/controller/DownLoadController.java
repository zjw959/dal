package com.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.ExceptionEx;
import com.constant.QueryTypeConstant;
import com.controller.abs.AbstractHandler;
import com.controller.helper.QueryChineseCode;
import com.controller.helper.QueryChineseUse;
import com.controller.helper.QueryInvitCodeSQL;
import com.controller.helper.QueryPackage;
import com.enity.InvitationCode;
import com.enity.chinese.ChineseCode;
import com.enity.chinese.ChineseUse;
import com.service.impl.ChineseCodeService;
import com.service.impl.ChineseUseService;
import com.service.impl.InvitationCodeService;
import com.service.impl.PackageInfoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import util.CsvUtil;

@Controller
@RequestMapping(path = "/file")
public class DownLoadController extends AbstractHandler {
    private static Logger log = LoggerFactory.getLogger(DownLoadController.class);
    @Autowired
    private ChineseCodeService chineseCodeService;
    @Autowired
    private ChineseUseService chineseUseService;
    @Autowired
    private InvitationCodeService codeService;
    @Autowired
    private PackageInfoService packageInfoService;
    @Value("${service.filePath}")
    private String filePath = "";

    @ApiOperation(value = "生成csv", notes = "生成csv")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", name = "condtion", value = "条件实体condtion",
                    required = true, dataType = "condtion"),
            @ApiImplicitParam(paramType = "query", name = "type", value = "type", required = true,
                    dataType = "String"),})

    @RequestMapping(path = {"/expload"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String expload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        File file = null;
        JSONObject object = new JSONObject();
        String result = null;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object type = jsonObject.get("type");
            Object _object = jsonObject.get("condtion");
            JSONObject o = JSONObject.parseObject(_object.toString());

            QueryTypeConstant queryType = QueryTypeConstant.gerQueryTypeType(String.valueOf(type));
            List packList = getPage(o, String.valueOf(type), queryType);

            Object clazz = queryType.getListener().newInstance();
            Field[] fields = clazz.getClass().getDeclaredFields();
            // 反射获取类属性，用于csv的表头
            String[] csvHeaders = new String[fields.length];
            for (short i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                csvHeaders[i] = fieldName;
            }

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = sf.format(new Date());
            file = CsvUtil.writeCsv(packList, csvHeaders, fileName, filePath);
            String json = sendClient(response, file.getPath());
            object.put(KEY_STATUS, OK);
            object.put("data", json);
        } catch (Exception e) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
            object.put(KEY_STATUS, ERROR);
        } finally {
            result = JSON.toJSONString(object, true);
        }
        return result;
    }

    public List<?> getPage(JSONObject o, String type, QueryTypeConstant queryType) {
        List<?> qyPage = null;
        try {
            Object clazz = queryType.getListener().newInstance();
            if (clazz instanceof ChineseCode) {
                QueryChineseCode queryCCode = JSON.toJavaObject(o, QueryChineseCode.class);
                qyPage = chineseCodeService.queryChineseCode(queryCCode);
            } else if (clazz instanceof ChineseUse) {
                QueryChineseUse queryCCode = JSON.toJavaObject(o, QueryChineseUse.class);
                qyPage = chineseUseService.queryChineseCode(queryCCode);
            } else if (clazz instanceof InvitationCode) {
                QueryInvitCodeSQL queryInvitCodeSQL = JSON.toJavaObject(o, QueryInvitCodeSQL.class);
                InvitationCode code = queryInvitCodeSQL.toInvitationCode();
                qyPage = codeService.getInvitationCodeList(code);
            } else if (clazz instanceof Package) {
                QueryPackage queryPackage = JSON.toJavaObject(o, QueryPackage.class);
                qyPage = packageInfoService.queryPackage(queryPackage.toPackageInfo());
            } else {
                return null;
            }

        } catch (Exception e) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(e));
        }
        return qyPage;
    }
}
