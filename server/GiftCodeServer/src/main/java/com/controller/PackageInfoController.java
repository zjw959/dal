package com.controller;

import java.io.IOException;
import java.util.Collection;
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
import com.controller.helper.QueryPackage;
import com.enity.PackageInfo;
import com.service.impl.PackageInfoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Controller // 表示这是一个Controller
@RequestMapping(path = "/package")
public class PackageInfoController extends AbstractHandler {
    private static Logger log = LoggerFactory.getLogger(PackageInfoController.class);
    private static final String dataType = "packageInfo";
    private static final String queryDataType = "queryPackage";
    @Autowired
    private PackageInfoService packageInfoService;


    @ApiOperation(value = "添加礼包", notes = "添加礼包")
    @ApiImplicitParam(paramType = "body", name = dataType, value = "礼包实体packageInfo",
            required = true, dataType = dataType)
    @RequestMapping(path = {"/create"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String createPackage(HttpServletRequest request) {
        int result = ERROR;
        JSONObject jsonObj = new JSONObject();
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object object = jsonObject.get(dataType);
            JSONObject o = JSONObject.parseObject(object.toString());
            PackageInfo packageInfo = JSON.toJavaObject(o, PackageInfo.class);
            packageInfoService.save(packageInfo);
            result = OK;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jsonObj.put("status", result);
        }
        String text = jsonObj.toJSONString();
        return text;
    }

    @ApiOperation(value = "查询全部礼包", notes = "此接口查询全部礼包信息/慎用")
    @RequestMapping(path = {"/find_all"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String findAllPackage(HttpServletRequest request) {
        JSONObject object = new JSONObject();
        try {
            Collection<PackageInfo> coll = packageInfoService.findAll();
            String jsonString = JSON.toJSONString(coll, true);
            object.put("contest", jsonString);
            object.put("status", OK);
            String result = JSON.toJSONString(object, true);
            return result;
        } catch (Exception ex) {
            object.put("status", ERROR);
            String result = JSON.toJSONString(object, true);
            return result;
        }
    }

    @ApiOperation(value = "修改礼包", notes = "修改礼包信息")
    @ApiImplicitParam(paramType = "body", name = dataType, value = "礼包实体packageInfo",
            required = true, dataType = dataType)
    @RequestMapping(path = {"/update"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String modifyPackage(HttpServletRequest request) {
        JSONObject jsonObj = new JSONObject();
        int result = ERROR;
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object object = jsonObject.get(dataType);
            JSONObject o = JSONObject.parseObject(object.toString());
            PackageInfo packageInfo = JSON.toJavaObject(o, PackageInfo.class);
            packageInfoService.save(packageInfo);
            result = OK;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            jsonObj.put(KEY_STATUS, result);
        }
        String text = jsonObj.toJSONString();
        return text;
    }

    @ApiOperation(value = "删除礼包", notes = "删除礼包,传入礼包id")
    @ApiImplicitParam(paramType = "query", name = "id", value = "礼包ID", required = true,
            dataType = "String")
    @RequestMapping(path = {"/delete"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String deletePackage(HttpServletRequest request) {
        int result = ERROR;
        JSONObject jsonObj = new JSONObject();
        try {
            String text = getRequestJson(request);
            JSONObject jsonObject = JSONObject.parseObject(text);
            Object object = jsonObject.get("id");
            int id = Integer.parseInt(object.toString());
            packageInfoService.remove(id);
            result = OK;
        } catch (Exception ex) {
            log.error(LOG_ERROR_PROGRAMMER_PREFIX + ExceptionEx.e2s(ex));
        } finally {
            jsonObj.put(KEY_STATUS, result);
        }
        String text = jsonObj.toJSONString();
        return text;
    }

    @ApiOperation(value = "条件查询礼包信息", notes = "返回分页信息和内容")
    @ApiImplicitParam(paramType = "body", name = queryDataType, value = "条件实体queryPackage",
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
            QueryPackage queryPackage = JSON.toJavaObject(o, QueryPackage.class);
            Page<PackageInfo> qyPage = packageInfoService.queryPackage(queryPackage.toPackageInfo(),
                    queryPackage.getLimitStart(), queryPackage.getLimitLen());

            // 当前页内容
            List<PackageInfo> packList = qyPage.getContent();
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

}
