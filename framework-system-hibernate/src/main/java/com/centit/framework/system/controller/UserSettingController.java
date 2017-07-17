package com.centit.framework.system.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserSetting;
import com.centit.framework.system.po.UserSettingId;
import com.centit.framework.system.service.UserSettingManager;

/**
 * 用户设置
 *
 * @author sx
 * @create 2014年10月14日
 */
@Controller
@RequestMapping("/usersetting")
public class UserSettingController extends BaseController {
    @Resource
    private UserSettingManager userSettingManager;
    
    /**
     * 系统日志中记录
     */
    private String optId = "userSetting";//CodeRepositoryUtil.getCode("OPTID", "userSetting");

    /**
     * 查询当前用户所有的用户参数设置信息
     *
     * @param field
     * @param pageDesc
     * @param request
     * @param response
     */
    @RequestMapping
    public void list(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        UserInfo userInfo = (UserInfo) getLoginUser(request);
        searchColumn.put(CodeRepositoryUtil.USER_CODE, userInfo.getUserCode());

        List<UserSetting> listObjects = userSettingManager.listObjects(searchColumn, pageDesc);

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(UserSetting.class, field);
        }

        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response, simplePropertyPreFilter);
    }


    @RequestMapping(value = "/listall", method = RequestMethod.GET)
    public void listAll(String[] field, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = new HashMap<>();
        UserInfo userInfo = (UserInfo) getLoginUser(request);
        searchColumn.put(CodeRepositoryUtil.USER_CODE, userInfo.getUserCode());

        List<UserSetting> listObjects = userSettingManager.listObjects(searchColumn);

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(UserSetting.class, field);
        }

        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        JsonResultUtils.writeResponseDataAsJson(resData, response, simplePropertyPreFilter);
    }
    /**
     * 获取当前用户设置的参数
     *
     * @param paramCode
     * @param response
     */
    @RequestMapping(value = "/{paramCode}", method = RequestMethod.GET)
    public void getUserSetting(@PathVariable String paramCode, HttpServletRequest request, HttpServletResponse response) {
        UserSettingId id = new UserSettingId(this.getLoginUser(request).getUserCode(), paramCode);

        UserSetting userSetting = userSettingManager.getObjectById(id);
        if (null != userSetting) {
            userSetting.setParamValue(HtmlUtils.htmlUnescape(userSetting.getParamValue()));
        }

        JsonResultUtils.writeSingleDataJson(userSetting, response);
    }

    /**
     * 新增或更新当前用户设置参数
     *
     * @param paramCode     参数代码
     * @param userSetting   UserSetting
     * @param bindingResult BindingResult
     * @param response      HttpServletResponse
     */
    @RequestMapping(value = "/{paramCode}", method = {RequestMethod.POST, RequestMethod.PUT})
    public void editUserSetting(@PathVariable String paramCode, @Valid UserSetting userSetting, 
                 HttpServletRequest request, BindingResult bindingResult,
                 HttpServletResponse response) {

        UserSettingId id = new UserSettingId(
                this.getLoginUser(request).getUserCode(), paramCode);
        UserSetting dbUserSetting = userSettingManager.getObjectById(id);

        if(dbUserSetting!=null){

            BeanUtils.copyProperties(userSetting, dbUserSetting, new String[]{"cid"});

            userSettingManager.saveObject(dbUserSetting);
        }else {

            userSetting.setCid(id);
            userSetting.setCreateDate(new Date());
            userSettingManager.saveObject(userSetting);
        } 
        JsonResultUtils.writeBlankJson(response);
        
        OperationLogCenter.logNewObject(request,optId,userSetting.getUserCode(),
        		OperationLog.P_OPT_LOG_METHOD_U,
                "更新当前用户设置参数",userSetting);

    }

    /**
     * 删除当前用户设置参数
     *
     * @param paramCode
     * @param response
     */
    @RequestMapping(value = "/{paramCode}", method = {RequestMethod.DELETE})
    public void deleteUserSetting(@PathVariable String paramCode, HttpServletRequest request,
            HttpServletResponse response) {
        UserSetting dbUserSetting=userSettingManager.getObjectById(new UserSettingId(this.getLoginUser(request).getUserCode(), paramCode));
        userSettingManager.deleteObject(dbUserSetting);
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        OperationLogCenter.logDeleteObject(request,optId,dbUserSetting.getUserCode(),
        		OperationLog.P_OPT_LOG_METHOD_D,  "已删除",dbUserSetting);
        /*********log*********/
    }


    /*
     * 导出当前用户下的所有参数设置
     *
     * @param request
     * @param response
     */
    /*@RequestMapping(value = "/export", method = RequestMethod.GET)
    public void export(HttpServletRequest request, HttpServletResponse response) {
        UserInfo userInfo = (UserInfo) getLoginUser(request);
        Map<String, Object> searchColumn = new HashMap<>();
        searchColumn.put(CodeRepositoryUtil.USER_CODE, userInfo.getUserCode());

        List<UserSetting> listObjects = userSettingManager.listObjects(searchColumn);

        String[] header = new String[]{"参数中文名称", "参数代码", "参数值", "创建时间"};
        String[] property = new String[]{"paramName", "paramCode", "paramValue", "createDate"};


        InputStream generateExcel = ExportExcelUtil.generateExcel(listObjects, header, property);

        try {
            WebOptUtils.download(generateExcel, "用户参数信息.xls", response);
        } catch (IOException e) {
            throw new ObjectException(e.getMessage());
        }

    }*/
}
