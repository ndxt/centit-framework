package com.centit.framework.system.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.OptLog;
import com.centit.framework.system.service.OptLogManager;

@Controller
@RequestMapping("/optlog")
public class OptLogController extends BaseController {

    @Resource
    @NotNull 
    private OptLogManager optLogManager;


    /**
     * 查询系统日志
     *
     * @param field    需要显示的字段
     * @param response HttpServletResponse
     */
    @RequestMapping
    public void list(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        /*List<OptLog> listObjects = null;
        if (null == pageDesc) {
        	
            listObjects = optLogManager.listObjects(searchColumn);
        } else
            listObjects = optLogManager.listObjects(searchColumn, pageDesc);

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(OptLog.class, field);
        }

        if (null == pageDesc) {
            JsonResultUtils.writeSingleDataJson(listObjects, response, simplePropertyPreFilter);
            return;
        }*/
        
        JSONArray jsonArray = optLogManager.listObjectsAsJson(field, searchColumn, pageDesc);
        
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, jsonArray);
        resData.addResponseData(PAGE_DESC, pageDesc);
        resData.addResponseData(CodeBook.SELF_ORDER_BY, searchColumn.get(CodeBook.SELF_ORDER_BY));
        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }

    /**
     * 查询单条日志
     *
     * @param logId
     * @param response
     */
    @RequestMapping(value = "/{logId}", method = {RequestMethod.GET})
    public void getOptLogById(@PathVariable Long logId, HttpServletResponse response) {
        OptLog dbOptLog = optLogManager.getObjectById(logId);
        if (null == dbOptLog) {
            JsonResultUtils.writeErrorMessageJson("日志信息不存在", response);
        }
        JsonResultUtils.writeSingleDataJson(dbOptLog, response);
    }

    /**
     * 删除单条系统日志
     *
     * @param logId
     * @param response
     */
    @RequestMapping(value = "/{logId}", method = {RequestMethod.DELETE})
    public void deleteOne(@PathVariable Long logId, HttpServletResponse response) {
        optLogManager.deleteObjectById(logId);
        JsonResultUtils.writeBlankJson(response);
    }

    /**
     * 删除多条系统日志
     * @param logIds
     * @param response
     */
    @RequestMapping(value = "/deleteMany", method = RequestMethod.DELETE)
    public void deleteMany(Long[] logIds, HttpServletResponse response) {
        optLogManager.deleteMany(logIds);

        JsonResultUtils.writeBlankJson(response);
    }
    /**
     * 删除某时段之前的系统日志
     *
     * @param begin
     * @param end
     * @param response
     */
    @RequestMapping(value = "/delete", method = {RequestMethod.DELETE})
    public void deleteByTime(Date begin, Date end, HttpServletResponse response) {
        optLogManager.delete(begin,end);

        JsonResultUtils.writeBlankJson(response);
    }
}
