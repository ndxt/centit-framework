package com.centit.framework.system.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.UserInfo;
import com.centit.framework.system.po.UserRole;
import com.centit.framework.system.po.UserUnit;
import com.centit.framework.system.service.SysUserManager;
import com.centit.framework.system.service.SysUserUnitManager;
import com.centit.framework.system.service.UserSettingManager;
import com.centit.support.json.JsonPropertyUtils;

@Controller
@RequestMapping("/userinfo")
public class UserInfoController extends BaseController {
    @Resource
    @NotNull
    private SysUserManager sysUserManager;

    @Resource
    @NotNull
    private SysUserUnitManager sysUserUnitManager;
    
    @Resource
    @NotNull
    private UserSettingManager userSettingManager;

    /**
     * 系统日志中记录
     */
    private String optId = "USERMAG";//CodeRepositoryUtil.getCode("OPTID", "userInfo");

    /**
     * 查询所有用户信息
     *
     * @param field    显示结果中只需要显示的字段
     * @param pageDesc PageDesc
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc, String _search, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        searchColumn.put("sort", "userOrder");
        searchColumn.put("order", "");
        List<UserInfo> listObjects = null;
        if (null == _search) {
            listObjects = sysUserManager.listObjects(searchColumn);
        } else {
            listObjects = sysUserManager.listObjects(searchColumn, pageDesc);
        }

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(UserInfo.class, field);
        }
        if (null == pageDesc) {
            JsonResultUtils.writeSingleDataJson(listObjects, response, simplePropertyPreFilter);
            return;
        }

        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response, simplePropertyPreFilter);
    }
            

    /**
     * 新增用户
     *
     * @param userInfo UserInfo
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.POST)
    public void create(@Valid UserInfo userInfo,HttpServletRequest request, HttpServletResponse response) {
        
    	UserInfo dbuserinfo=sysUserManager.loadUserByLoginname(userInfo.getLoginName());
    	if(null!=dbuserinfo)
    	{
    		 JsonResultUtils.writeErrorMessageJson(
    				 ResponseData.ERROR_FIELD_INPUT_CONFLICT,
    				 "登录名"+userInfo.getLoginName()+"已存在，请更换！", response);
    		 return;
    	}
        userInfo.setUserCode(sysUserManager.getNextUserCode());
        
        if(null!=userInfo.getUserUnits()){
            for(UserUnit uu:userInfo.getUserUnits()){
                uu.setUserCode(userInfo.getUserCode()); 
            }
        }
        if(null!=userInfo.listUserRoles()){
            for(UserRole ur:userInfo.listUserRoles()){
                ur.setUserCode(userInfo.getUserCode());
            }
        }
        sysUserManager.saveNewUserInfo(userInfo);
        
        
        JsonResultUtils.writeSingleDataJson(userInfo, response);


        /*********log*********/
        OperationLogCenter.logNewObject(request,optId,userInfo.getUserCode(),
        		OperationLog.P_OPT_LOG_METHOD_C,  "新增用户", userInfo);
    }

    /**
     * 更新用户状态信息
     *
     * @param userCode 用户代码
     * @param userInfo UserInfo
     */
    @RequestMapping(value = "/state/{userCode}", method = RequestMethod.PUT)
    public void changeState(@PathVariable String userCode, UserInfo userInfo, 
            HttpServletRequest request, HttpServletResponse response) {
        UserInfo userDetails = sysUserManager.getObjectById(userCode);
        if (null == userDetails) {
            JsonResultUtils.writeErrorMessageJson("当前用户不存在", response);

            return;
        }

        String isValid = userInfo.getIsValid();
        // 更新时不更新用户代码，登录名称，用户密码
        // 更新用户密码有专门的方法操作
        //BeanUtils.copyProperties(userInfo, userDetails, new String[]{"userCode", "loginName", "userPin"});
        userDetails.setIsValid(isValid);

        sysUserManager.updateUserProperities(userDetails);

        JsonResultUtils.writeBlankJson(response);

        /*********log*********/
        StringBuilder optContent = new StringBuilder();
        optContent.append("更新用户状态,用户代码:" + userCode + ",")
                .append("是否启用:" + ("T".equals(userDetails.getIsValid()) ? "是" : "否"));


        OperationLogCenter.log(request,optId,userCode,"changeStatus",  optContent.toString());
    }
    
    /**
     * 更新用户信息
     * @param userCode
     * @param userInfo
     * @param request
     * @param response
     */
    @RequestMapping(value = "/{userCode}", method = RequestMethod.PUT)
    public void edit(@PathVariable String userCode, UserInfo userInfo,
            HttpServletRequest request, HttpServletResponse response) {
        
        UserInfo userDetails = sysUserManager.getObjectById(userCode);
        if (null == userDetails) {
            JsonResultUtils.writeErrorMessageJson("当前用户不存在", response);

            return;
        }
       
        //userInfo.setUserCode(userCode);
        userDetails.copyNotNullProperty(userInfo );
        
        sysUserManager.updateUserInfo(userDetails);
        
        JsonResultUtils.writeBlankJson(response);

        /*********log*********/

        StringBuilder optContent = new StringBuilder();
        optContent.append("更新用户状态,用户代码:" + userCode + ",")
                .append("是否启用:" + ("T".equals(userDetails.getIsValid()) ? "是" : "否"));

       OperationLogCenter.log(request,optId, userCode, "changeStatus",
        		 optContent.toString());
    }
    
    
    
    
    /**
     * 当前登录用户信息
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public void getCurrentUserInfo(HttpServletRequest request, HttpServletResponse response) {
        CentitUserDetails userDetails = super.getLoginUser(request);
        UserInfo userinfo = sysUserManager.getObjectById(userDetails.getUserCode());
        JsonResultUtils.writeSingleDataJson(userinfo, response);
    }

    /**
     * 获取单个用户信息
     *
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{userCode}", method = RequestMethod.GET)
    public void getUserInfo(@PathVariable String userCode, HttpServletResponse response) {
        UserInfo userDetails = sysUserManager.getObjectById(userCode);

        Map<Class<?>, String[]> excludes  =new HashMap<Class<?>, String[]>();
        excludes.put(UserUnit.class,new String[]{"userInfo"});
        excludes.put(UserRole.class,new String[]{"userInfo"});
        JsonResultUtils.writeSingleDataJson(userDetails,response, JsonPropertyUtils.getExcludePropPreFilter(excludes));
    }
    
    /**
     * 通过用户代码获取角色
     *
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/allroles/{userCode}", method = RequestMethod.GET)
    public void listRolesByUser(@PathVariable String userCode, 
             HttpServletResponse response) {
        
        List<RoleInfo> roles = sysUserManager.listUserValidRoles(userCode);
        JsonResultUtils.writeSingleDataJson(roles, response);
    }


    /**
     * 当前登录名是否已存在
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/exists", method = RequestMethod.GET)
    public void isAnyExist(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String userCode = request.getParameter("userCode");
        String loginName = request.getParameter("loginName");
        String regPhone = request.getParameter("regPhone");
        String regEmail = request.getParameter("regEmail");

        JsonResultUtils.writeOriginalObject(
                sysUserManager.isAnyOneExist(
                        userCode,  loginName, regPhone, regEmail), response);
    }

     /**
     * 当前登录名是否已存在
     *
     * @param loginName 登录名
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/exists/{loginName}", method = RequestMethod.GET)
    public void isExists(@PathVariable String loginName, 
            HttpServletRequest request,HttpServletResponse response) throws IOException {
        UserInfo userInfo = sysUserManager.loadUserByLoginname(loginName);

        JsonResultUtils.writeOriginalObject(null != userInfo, response);
    }

    /**
     * 更新用户密码
     *
     * @param userCode    用户代码
     * @param password    旧密码
     * @param newPassword 新密码
     * @param response    HttpServletResponse
     */
    @RequestMapping(value = "/change/{userCode}", method = RequestMethod.PUT)
    public void changePwd(@PathVariable String userCode, String password, String newPassword,
            HttpServletRequest request,HttpServletResponse response) {
        
        sysUserManager.setNewPassword(userCode, password, newPassword);

        JsonResultUtils.writeBlankJson(response);

        /*********log*********/
        OperationLogCenter.log(request,optId,userCode, "changePassword", "更新用户密码,用户代码:" + userCode);
        /*********log*********/
    }

    @RequestMapping(value = "/changePwd/{userCode}", method = RequestMethod.PUT)
    public void forceChangePwd(@PathVariable String userCode,
                               HttpServletRequest request,HttpServletResponse response) {
        String newPassword = request.getParameter("newPassword");
        if(StringUtils.isBlank(newPassword))
            sysUserManager.resetPwd(userCode);
        else
            sysUserManager.forceSetPassword(userCode,newPassword);

        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        OperationLogCenter.log(request,optId,userCode, "forceChangePwd", "更新用户密码,用户代码:" + userCode);
        /*********log*********/
    }

    @RequestMapping(value = "/canchange/{userCode}/{oldPassword}", method = RequestMethod.GET)
    public void canChangePwd(@PathVariable String userCode,@PathVariable String oldPassword,HttpServletRequest request,HttpServletResponse response) {
        boolean bo=true;
        bo=sysUserManager.checkUserPassword(userCode,oldPassword);

        JsonResultUtils.writeSingleDataJson(bo,response);
    }

    /**
     * 批量重置密码
     *
     * @param userCodes 用户代码集合
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/reset", method = RequestMethod.PUT)
    public void resetBatchPwd(String[] userCodes,HttpServletRequest request, HttpServletResponse response) {
        if (ArrayUtils.isEmpty(userCodes)) {
            JsonResultUtils.writeErrorMessageJson("用户代码集合为空", response);

            return;
        }
        
        sysUserManager.resetPwd(userCodes);

        JsonResultUtils.writeBlankJson(response);

        /*********log*********/
        OperationLogCenter.logNewObject(request,optId,null, "resetPassword",  "批量重置密码",userCodes);
    }

    /**
     * 重置用户密码
     *
     * @param userCode 用户代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/reset/{userCode}", method = RequestMethod.PUT)
    public void resetPwd(@PathVariable String userCode, HttpServletRequest request,HttpServletResponse response) {
        sysUserManager.resetPwd(userCode);

        JsonResultUtils.writeBlankJson(response);

        /*********log*********/
        OperationLogCenter.log(request,optId,userCode, "resetPassword", "重置用户密码,用户代码:" + userCode);
    }

    @RequestMapping(value="/{userCode}",method=RequestMethod.DELETE)
    public  void deleteUser(@PathVariable String userCode,HttpServletResponse response){
        UserInfo userInfo = sysUserManager.getObjectById(userCode);
        if(null!=userInfo){
            
            sysUserManager.deleteUserInfo(userCode);
            
            JsonResultUtils.writeSuccessJson(response);
        }
        else{
            JsonResultUtils.writeErrorMessageJson("该用户不存在", response);
        }
    }
    
}
