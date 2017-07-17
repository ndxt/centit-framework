package com.centit.framework.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.centit.framework.components.CodeRepositoryUtil;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.hibernate.dao.SysDaoOptUtils;
import com.centit.framework.model.basedata.IUnitInfo;
import com.centit.framework.model.basedata.IUserInfo;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.po.UserUnit;
import com.centit.framework.system.service.SysUserUnitManager;

/**
 * Created with IntelliJ IDEA.
 * User: sx
 * Date: 14-11-27
 * Time: 上午10:16
 * 用户机构关联操作，此操作是双向操作，用户可在用户管理中新增或更新自身所在机构，机构可在机构管理中新增或更新机构内用户。
 */

@Controller
@RequestMapping("/userunit")
public class UserUnitController extends BaseController {
    @Resource
    @NotNull
    private SysUserUnitManager sysUserUnitManager;

    /**
     * 系统日志中记录
     */
    private String optId = "USERUNIT";//CodeRepositoryUtil.getCode("OPTID", "userUnit");

    /**
     * 机构人员树形信息
     *
     * @param state    A或空，返回所有机构人员信息。T，返回未禁用的机构人员信息
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String state, HttpServletResponse response) {
        List<Map<String, Object>> listObjects = new ArrayList<>();
        if (StringUtils.isBlank(state)) {
            state = "A";
        }
        List<IUserInfo> users = CodeRepositoryUtil.getAllUsers(state);
        List<IUnitInfo> units = CodeRepositoryUtil.getAllUnits(state);

        for (IUnitInfo unit : units) {
            Map<String, Object> object = new HashMap<>();
            object.put("id", unit.getUnitCode());
            object.put("name", unit.getUnitName());
            object.put("pId", unit.getParentUnit());

            listObjects.add(object);
        }

        for (IUserInfo user : users) {
            Map<String, Object> object = new HashMap<>();
            object.put("id", user.getUserCode());
            object.put("name", user.getUserName());
            object.put("pId", user.getPrimaryUnit());

            listObjects.add(object);
        }

        JsonResultUtils.writeSingleDataJson(listObjects, response);
    }

    /**
     * 通过机构代码获取机构下用户
     *
     * @param unitCode 机构代码
     *                 参数 s_isPaimary 是否为主机构，是T F否，为空不限定
     * @param pageDesc PageDesc
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unitusers/{unitCode}", method = RequestMethod.GET)
    public void listUsersByUnit(@PathVariable String unitCode, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = convertSearchColumn(request);
        filterMap.put("unitCode", unitCode);

        listObject(filterMap, pageDesc, response);
    }
    
    

    /**
     * 通过用户代码获取用户所在机构
     *
     * @param userCode 用户代码
     *                 参数 s_isPaimary 是否为主机构，是T F否，为空不限定
     * @param pageDesc PageDesc
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/userunits/{userCode}", method = RequestMethod.GET)
    public void listUnitsByUser(@PathVariable String userCode, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = convertSearchColumn(request);
        filterMap.put("userCode", userCode);
        listObject(filterMap, pageDesc, response);
    }


    protected void listObject(Map<String, Object> filterMap, PageDesc pageDesc, HttpServletResponse response) {
        List<UserUnit> listObjects = sysUserUnitManager.listObjects(filterMap, pageDesc);

        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, SysDaoOptUtils.objectsToJSONArray(listObjects));
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }


    /**
     * 返回一条用户机构关联信息
     *
     * @param unitCode    机构代码
     * @param userCode    用户代码
     * @param userStation 岗位，数据字典 StationType
     * @param userRank    职务，数据字典 RankType
     * @param response    HttpServletResponse
     */
    @RequestMapping(value = "/{userunitid}", method = RequestMethod.GET)
    public void getUserUnitById(@PathVariable String userunitid, HttpServletResponse response) {
        UserUnit userUnit = sysUserUnitManager.getObjectById(userunitid);

        if (null == userUnit) {

            JsonResultUtils.writeErrorMessageJson("当前机构中无此用户", response);
            return;
        }
        JsonResultUtils.writeSingleDataJson(SysDaoOptUtils.objectToJSON(userUnit), response);
    }

    /**
     * 返回一组用户机构关联信息
     *
     * @param unitCode    机构代码
     * @param userCode    用户代码
     * @param userStation 岗位，数据字典 StationType
     * @param userRank    职务，数据字典 RankType
     * @param response    HttpServletResponse
     */
    @RequestMapping(value = "/{unitCode}/{userCode}", method = RequestMethod.GET)
    public void getUserUnit(@PathVariable String unitCode, @PathVariable String userCode, HttpServletResponse response) {
        List<UserUnit> userUnits = sysUserUnitManager.listObjectByUserUnit(userCode, unitCode);

        if (null == userUnits || userUnits.size()==0) {
            JsonResultUtils.writeErrorMessageJson("当前机构中无此用户", response);
            return;
        }
        JsonResultUtils.writeSingleDataJson(SysDaoOptUtils.objectsToJSONArray(userUnits), response);
    }


    /**
     * 创建用户机构关联信息
     *
     * @param userUnit UserUnit
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.POST)
    public void create(@Valid UserUnit userUnit,HttpServletRequest request, HttpServletResponse response) {

        sysUserUnitManager.saveNewUserUnit(userUnit);

        JsonResultUtils.writeBlankJson(response);

        /*********log*********/
        OperationLogCenter.logNewObject(request,optId, OperationLog.P_OPT_LOG_METHOD_C, null, "新增用户机构关联信息" , userUnit);
        /*********log*********/
    }


    /**
     * 更新用户机构关联信息
     *
     * @param unitCode       机构代码
     * @param userCode       用户代码
     * @param userUnit       UserUnit
     * @param oldUserStation 原先的岗位
     * @param oldUserRank    原先的职务
     * @param response       HttpServletResponse
     */
    @RequestMapping(value = "/{userunitid}", method = RequestMethod.PUT)
    public void edit(@PathVariable String userunitid, @Valid UserUnit userUnit,
                     HttpServletRequest request, HttpServletResponse response) {
        if (null == userUnit) {
            JsonResultUtils.writeErrorMessageJson("当前机构中无此用户", response);
        }
        UserUnit dbUserUnit = sysUserUnitManager.getObjectById(userunitid);
         
        /*********log*********/
        UserUnit oldValue = new UserUnit();
        oldValue.copy(dbUserUnit);
        /*********log*********/
        dbUserUnit.copy(userUnit);
        
        sysUserUnitManager.updateUserUnit(dbUserUnit);
        
        JsonResultUtils.writeSingleDataJson(userUnit, response);

        /*********log*********/
        OperationLogCenter.logUpdateObject(request,optId,oldValue.getUserUnitId(), OperationLog.P_OPT_LOG_METHOD_C, "更新用户机构关联信息" , userUnit,oldValue);
        /*********log*********/
    }

    /**
     * 删除用户机构关联信息
     *
     * @param unitCode    机构代码
     * @param userCode    用户代码
     * @param userStation 岗位，数据字典 StationType
     * @param userRank    职务，数据字典 RankType
     * @param response    HttpServletResponse
     */
    @RequestMapping(value = "/{userunitid}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String userunitid,
                       HttpServletRequest request, HttpServletResponse response) {
        UserUnit dbUserUnit = sysUserUnitManager.getObjectById(userunitid);

        sysUserUnitManager.deleteObject(dbUserUnit);

        JsonResultUtils.writeBlankJson(response);


        /*********log*********/
        OperationLogCenter.logDeleteObject(request,optId,dbUserUnit.getUserUnitId(),
        		OperationLog.P_OPT_LOG_METHOD_D,  "已删除",dbUserUnit);
        /*********log*********/
    }

}
