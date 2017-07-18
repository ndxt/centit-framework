package com.centit.framework.system.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.BeanUtils;
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
import com.centit.framework.model.basedata.IUserUnit;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.security.model.CentitUserDetails;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.po.RoleInfo;
import com.centit.framework.system.po.RolePower;
import com.centit.framework.system.po.RolePowerId;
import com.centit.framework.system.service.OptInfoManager;
import com.centit.framework.system.service.OptMethodManager;
import com.centit.framework.system.service.SysRoleManager;
import com.centit.framework.system.service.SysUserRoleManager;
import com.centit.support.json.JsonPropertyUtils;

@Controller
@RequestMapping("/roleinfo")
public class RoleInfoController extends BaseController {
    @Resource
    @NotNull
    private SysRoleManager sysRoleManager;
    @Resource
    @NotNull
    private SysUserRoleManager sysUserRoleManager;

    @Resource
    @NotNull
    private OptInfoManager functionManager;    
    
    @Resource
    @NotNull
    private OptMethodManager optDefManager;    
    /**
     * 系统日志中记录
     */
    private static String optId = "ROLEMAG";//CodeRepositoryUtil.getCode("OPTID", "roleInfo");

    /**
     * 查询所有系统角色
     * @param field field[]
     * @param pageDesc PageDesc
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.GET)
    public void listGlobalAndPublicRole(String[] field,PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = convertSearchColumn(request);
        filterMap.put("NP_GLOBAL", "true");
        List<RoleInfo> roleInfos = sysRoleManager.listObjects(filterMap, pageDesc);

        ResponseData respData = new ResponseData();
        respData.addResponseData(OBJLIST, roleInfos);
        respData.addResponseData(PAGE_DESC, pageDesc);

        if (ArrayUtils.isNotEmpty(field)) {
            JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getIncludePropPreFilter(RoleInfo.class,field));
        }
        else{
        JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getExcludePropPreFilter(RoleInfo.class, "rolePowers","userRoles"));}
    }

    /**
     * 查询所有某部门部门角色
     * @param field field[]
     * @param unitCode unitCode
     * @param pageDesc PageDesc
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unit/{unitCode}", method = RequestMethod.GET)
    public void listUnitAndPublicRole(String[] field,@PathVariable String unitCode,PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = convertSearchColumn(request);
        filterMap.put("UNITROLE", unitCode + "-%");
        List<RoleInfo> roleInfos = sysRoleManager.listObjects(filterMap, pageDesc);

        ResponseData respData = new ResponseData();
        respData.addResponseData(OBJLIST, roleInfos);
        respData.addResponseData(PAGE_DESC, pageDesc);

        if (ArrayUtils.isNotEmpty(field)) {
            JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getIncludePropPreFilter(RoleInfo.class,field));
        }
        else{
        JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getExcludePropPreFilter(RoleInfo.class, "rolePowers","userRoles"));}
    }
    
    /**
     * 查询所有某部门部门角色
     *
     * @param field field[]
     * @param pageDesc PageDesc
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/item", method = RequestMethod.GET)
    public void listItemRole(String[] field,PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> filterMap = convertSearchColumn(request);
        filterMap.put("ROLECODE",  "I-%");
        List<RoleInfo> roleInfos = sysRoleManager.listObjects(filterMap, pageDesc);

        ResponseData respData = new ResponseData();
        respData.addResponseData(OBJLIST, roleInfos);
        respData.addResponseData(PAGE_DESC, pageDesc);

        if (ArrayUtils.isNotEmpty(field)) {
            JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getIncludePropPreFilter(RoleInfo.class,field));
        }
        else{
        JsonResultUtils.writeResponseDataAsJson(respData, response, JsonPropertyUtils.getExcludePropPreFilter(RoleInfo.class, "rolePowers","userRoles"));}
    }
    
    /**
     * 根据角色代码获取角色操作定义信息
     *
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/power/role/{roleCode}", method = RequestMethod.GET)
    public void getRolePowerByRoleCode(@PathVariable String roleCode, HttpServletResponse response) {
        List<RolePower> rolePowers = sysRoleManager.getRolePowers(roleCode);

        JsonResultUtils.writeSingleDataJson(rolePowers, response/*,
                JsonPropertyUtils.getExcludePropPreFilter(RoleInfo.class, "rolePowers")*/);
    }

    /**
     * 根据操作定义代码获取角色信息
     *
     * @param defCode  操作定义代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/power/defCode/{defCode}", method = RequestMethod.GET)
    public void getRolePowerByOptCode(@PathVariable String defCode, HttpServletResponse response) {

        List<RolePower> rolePowers = sysRoleManager.getRolePowersByDefCode(defCode);

        JsonResultUtils.writeSingleDataJson(rolePowers, response);
    }

    /**
     * 根据业务代码获取角色信息
     *
     * @param optId  操作定义代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/power/optCode/{optId}", method = RequestMethod.GET)
    public void getRolePowerByOptId(@PathVariable String optId, HttpServletResponse response) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        List<OptMethod> optDefs = optDefManager.listOptMethodByOptID(optId);
        

        for (OptMethod def : optDefs) {
            Map<String, Object> temp = new HashMap<>();

            List<RolePower> rolePowers = sysRoleManager.getRolePowersByDefCode(def.getOptCode());

            temp.put("optDef", def);
            temp.put("rolePowers", rolePowers);
            result.add(temp);
        }

      /*  Map<Class<?>, String[]> excludes = new HashMap<>();
        excludes.put(RoleInfo.class, new String[]{"rolePowers"});
        excludes.put(OptMethod.class, new String[]{"optInfo"});*/
        JsonResultUtils.writeSingleDataJson(result, response);
    }

    /**
     * 新增系统角色
     *
     * @param request HttpServletRequest
     * @param roleInfo RoleInfo
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/global", method = RequestMethod.POST)
    public void createGlobalRole(@Valid RoleInfo roleInfo,HttpServletRequest request, HttpServletResponse response) {
        if(! roleInfo.getRoleCode().startsWith("G-")){
            roleInfo.setRoleCode("G-"+ roleInfo.getRoleCode());
        }
        roleInfo.setRoleType("S");
        //roleInfo.setUnitCode("G");
        roleInfo.setCreateDate(new Date());
        sysRoleManager.saveNewRoleInfo(roleInfo);
      //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        
        /*********log*********/
        OperationLogCenter.logNewObject(request,optId,roleInfo.getRoleCode(),
        		OperationLog.P_OPT_LOG_METHOD_C, "新增角色" ,roleInfo);
        /*********log*********/
    }
   
    @RequestMapping(value = "/public",method = RequestMethod.POST)
    public void createPublicRole(@Valid RoleInfo roleInfo,HttpServletRequest request, HttpServletResponse response) {
        if(! roleInfo.getRoleCode().startsWith("P-")){
            roleInfo.setRoleCode("P-"+ roleInfo.getRoleCode());
        }
        //roleInfo.setUnitCode("P");
        roleInfo.setRoleType("S");
        roleInfo.setCreateDate(new Date());
        sysRoleManager.saveNewRoleInfo(roleInfo);
      //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        
        /*********log*********/
        OperationLogCenter.logNewObject(request, optId,roleInfo.getRoleCode(),
        		OperationLog.P_OPT_LOG_METHOD_C, "新增角色" , roleInfo);
        /*********log*********/
    }
    
    @RequestMapping(value = "/item",method = RequestMethod.POST)
    public void createItemRole(@Valid RoleInfo roleInfo,HttpServletRequest request, HttpServletResponse response) {
        if(! roleInfo.getRoleCode().startsWith("I-")){
            roleInfo.setRoleCode("I-"+ roleInfo.getRoleCode());
        }
        roleInfo.setRoleType("I");
        //roleInfo.setUnitCode("I");
        
        roleInfo.setCreateDate(new Date());
        sysRoleManager.saveNewRoleInfo(roleInfo);
      //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        
        /*********log*********/
        OperationLogCenter.logNewObject(request, optId,roleInfo.getRoleCode(),
        		OperationLog.P_OPT_LOG_METHOD_C, "新增角色" , roleInfo);
        /*********log*********/
    }
    
    @RequestMapping(value = "/dept/{unitcode}",method = RequestMethod.POST)
    public void createDeptRole(@PathVariable String unitcode,@Valid RoleInfo roleInfo,
            HttpServletRequest request,HttpServletResponse response) {
        if(! roleInfo.getRoleCode().startsWith(unitcode+"-")){
            roleInfo.setRoleCode(unitcode+"-"+ roleInfo.getRoleCode());
        }
        roleInfo.setRoleType("S");
        roleInfo.setUnitCode(unitcode);
        roleInfo.setCreateDate(new Date());
        sysRoleManager.saveNewRoleInfo(roleInfo);
        //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        
        //*********log*********//*
        OperationLogCenter.logNewObject(request,optId,roleInfo.getRoleCode(), 
        		OperationLog.P_OPT_LOG_METHOD_C,  "新增角色" , roleInfo);
        //*********log*********//*
    }
    
    /**
     * 将权限付给部门
     * @param unitcode unitcode
     * @param optCodes optCodes
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unit/saveopts/{unitcode}",method = RequestMethod.POST)
    public void setUnitPowers(@PathVariable String unitcode,
    		String optCodes,
            HttpServletRequest request,HttpServletResponse response) {
    	String optCodesArray[]=optCodes.split(",");
    	RoleInfo roleInfo = sysRoleManager.getObjectById("G$"+ unitcode);
    	if(roleInfo==null){
	    	roleInfo = new RoleInfo();
	    	roleInfo.setIsValid("T");
	    	roleInfo.setRoleCode("G$"+ unitcode);
	    	roleInfo.setRoleName("赋给部门"+unitcode+"的权限");
	    	roleInfo.setRoleDesc(roleInfo.getRoleName());
	        roleInfo.setRoleType("D");
	        roleInfo.setCreateDate(new Date());
	        sysRoleManager.saveNewRoleInfo(roleInfo);
	        //刷新缓存
	        sysRoleManager.loadRoleSecurityMetadata();
    	}

		List<RolePower> rolePowers = new ArrayList<>();
		//为空时更新RoleInfo中字段数据
	   if (ArrayUtils.isNotEmpty(optCodesArray)) {
	       for (String optCode : optCodesArray) {
	    	   if(StringUtils.isNotBlank(optCode))
	    		   rolePowers.add(new RolePower(new RolePowerId(roleInfo.getRoleCode(), optCode)));
	       }
	   }

	   roleInfo.addAllRolePowers(rolePowers);
	   sysRoleManager.updateRoleInfo(roleInfo);
	   //sysRoleManager.loadRoleSecurityMetadata();   
	   JsonResultUtils.writeBlankJson(response);	   
	   /*********log*********/
	   OperationLogCenter.logNewObject(request,optId, roleInfo.getRoleCode(),
			  "setUnitPowers", "更新机构权限",roleInfo);
	   /*********log*********/
    }


    /**
     * 从操作定义反向添加角色代码
     * @param roleCode 角色代码
     * @param optCode 操作定义
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/addopt/{roleCode}/{optCode}", method = RequestMethod.PUT)
    public void addOptToRole(@PathVariable String roleCode, @PathVariable String optCode, 
            HttpServletRequest request,HttpServletResponse response) {
        
        RoleInfo dbRoleInfo = sysRoleManager.getObjectById(roleCode);

        if (null == dbRoleInfo) {
            JsonResultUtils.writeErrorMessageJson("角色信息不存在", response);
            return;
        }

        RolePower rolePower = new RolePower(new RolePowerId(roleCode, optCode));

        if (dbRoleInfo.getRolePowers().contains(rolePower)) {
            JsonResultUtils.writeBlankJson(response);
            return;
        }

        dbRoleInfo.getRolePowers().add(rolePower);
        
        sysRoleManager.updateRoleInfo(dbRoleInfo);
      //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        OperationLogCenter.logNewObject(request,optId, rolePower.getOptCode(),
        		OperationLog.P_OPT_LOG_METHOD_C,  "角色"+dbRoleInfo.getRoleName()+"添加权限:" , rolePower);
        /*********log*********/
    }

    /**
     * 从操作定义反向删除角色代码
     * @param roleCode 角色代码
     * @param optCode 操作定义
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/delopt/{roleCode}/{optCode}", method = RequestMethod.DELETE)
    public void deleteOptFormRole(@PathVariable String roleCode, @PathVariable String optCode,
            HttpServletRequest request, HttpServletResponse response) {
        RoleInfo dbRoleInfo = sysRoleManager.getObjectById(roleCode);

        if (null == dbRoleInfo) {
            JsonResultUtils.writeErrorMessageJson("角色信息不存在", response);
            return;
        }
        
        RolePower rolePower = new RolePower(new RolePowerId(roleCode, optCode));
        if (!dbRoleInfo.getRolePowers().contains(rolePower)) {
            JsonResultUtils.writeBlankJson(response);
            return;
        }

        dbRoleInfo.getRolePowers().remove(rolePower);
        sysRoleManager.updateRoleInfo(dbRoleInfo);
        //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        OperationLogCenter.logDeleteObject(request,optId,rolePower.getOptCode(),
        		OperationLog.P_OPT_LOG_METHOD_D, "删除角色"+dbRoleInfo.getRoleName()+"的权限" , rolePower);
        /*********log*********/
    }

    /**
     * 更新系统角色
     *
     * @param roleCode 角色代码
     * @param roleInfo RoleInfo
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{roleCode}", method = RequestMethod.PUT)
    public void edit(@PathVariable String roleCode, @Valid RoleInfo roleInfo,
                     //@RequestParam(value = "optCodes", required = false) String[] optCodes,
                     HttpServletRequest request, HttpServletResponse response) {

        RoleInfo dbRoleInfo = sysRoleManager.getObjectById(roleCode);
        if (null == dbRoleInfo) {
            JsonResultUtils.writeErrorMessageJson("角色信息不存在", response);
            return;
        }

        /*********log*********/
        RoleInfo oldValue = new RoleInfo();
        oldValue.copy(dbRoleInfo);
        /*********log*********/

        BeanUtils.copyProperties(roleInfo, dbRoleInfo, new String[]{"roleCode", "rolePowers"});

        sysRoleManager.updateRoleInfo(dbRoleInfo);

        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        OperationLogCenter.logUpdateObject(request, optId, roleInfo.getRoleCode(),
                OperationLog.P_OPT_LOG_METHOD_U, "更新系统角色", roleInfo, oldValue);
        /*********log*********/
    }

    /**
     * 更新系统角色
     *
     * @param roleCode 角色代码
     * @param roleInfo rolePower roleCode dataScopes
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/power/{roleCode}", method = RequestMethod.PUT)
    public void updateRolePower(@PathVariable String roleCode,RoleInfo roleInfo,
                     HttpServletRequest request, HttpServletResponse response) {

        RoleInfo dbRoleInfo = sysRoleManager.getObjectById(roleCode);
        if (null == dbRoleInfo) {
            JsonResultUtils.writeErrorMessageJson("角色信息不存在", response);
            return;
        }
//        String  userCode = super.getLoginUserCode(request);


        //List<RolePower> oldPowers = dbRoleInfo.getRolePowers();

       // List<RolePower> rolePowers = new ArrayList<>();
        /*for( Map.Entry<String, Object> ent : rolePower.entrySet()){
            RolePower po = new RolePower(
                    new RolePowerId( roleCode,ent.getKey()),
                    StringBaseOpt.objectToString(ent.getValue()) );
            po.setCreator(userCode);
            po.setUpdator(userCode);
            rolePowers.add(po);
        }*/

        //为空时更新RoleInfo中字段数据
        dbRoleInfo.setRolePowers(roleInfo.getRolePowers());
        sysRoleManager.updateRoleInfo(dbRoleInfo);
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        
        /*********log*********/
        OperationLogCenter.logUpdateObject(request,optId, roleCode,
        		OperationLog.P_OPT_LOG_METHOD_U, "更新系统角色权限",roleInfo.getRolePowers(),null);
        /*********log*********/
    }

    /**
     * 角色代码是否存在
     *
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     * @throws IOException IOException
     */
    @RequestMapping(value = "/notexists/{roleCode}", method = RequestMethod.GET)
    public void isNotExists(@PathVariable String roleCode, HttpServletResponse response) throws IOException {
    	if(roleCode.indexOf('-')<1){
    		boolean notExist = sysRoleManager.getObjectById("G-"+roleCode)==null;
    		if(notExist)
    			notExist = sysRoleManager.getObjectById("P-"+roleCode)==null;
    		JsonResultUtils.writeOriginalObject(notExist, response);	
    	}else
    		JsonResultUtils.writeOriginalObject(null == sysRoleManager.getObjectById(roleCode), response);
    }

    /**
     * 从操作定义反向删除角色代码
     * @param roleCode 角色代码
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{roleCode}", method = RequestMethod.DELETE)
    public void deleteRole(@PathVariable String roleCode,
                           HttpServletRequest request, HttpServletResponse response) {
        if("G-SYSADMIN".equalsIgnoreCase(roleCode)||
                "G-anonymous".equalsIgnoreCase(roleCode)||
                "G-DEPLOY".equalsIgnoreCase(roleCode)){
            JsonResultUtils.writeErrorMessageJson("系统内置角色不能删除。", response);
            return;
        }
        int n = sysRoleManager.countRoleUserSum(roleCode);

        if(n>0){
            JsonResultUtils.writeErrorMessageJson("有用户引用这个角色，不能删除。", response);
            return ;
        }
        RoleInfo dbRoleInfo = sysRoleManager.getObjectById(roleCode);
        if(dbRoleInfo!=null) {
            OperationLogCenter.logDeleteObject(request, optId, roleCode,
                    OperationLog.P_OPT_LOG_METHOD_D, "删除角色" + dbRoleInfo.getRoleName(), dbRoleInfo);
        }
        sysRoleManager.deleteRoleInfo(roleCode);
        JsonResultUtils.writeSuccessJson(response);
    }

    /**
     * 单个角色信息
     *
     * @param roleCode 角色代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{roleCode}", method = RequestMethod.GET)
    public void findRoleInfo(@PathVariable String roleCode, HttpServletResponse response) {
        RoleInfo roleInfo = sysRoleManager.getRoleInfo(roleCode);
        if (null == roleInfo) {
            JsonResultUtils.writeErrorMessageJson("角色信息不存在", response);
            return;
        }

        JsonResultUtils.writeSingleDataJson(roleInfo, response);
    }


    /**
     * 机构权限
     *
     * @param unitCode 机构代码
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/power/unit/{unitCode}", method = RequestMethod.GET)
    public void getUnitInfoPower(@PathVariable String unitCode, HttpServletResponse response) {
        
    	List<RolePower> rolePowers = sysRoleManager.getRolePowers("G$" + unitCode);

        JsonResultUtils.writeSingleDataJson(rolePowers, response);
    }
    
    /**
     * 对角色信息进行模糊搜索，适用于带搜索条件的下拉框。
     *
     * @param key      搜索条件
     * @param field    需要搜索的字段，如为空，默认，roleCode,roleName
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/search/{key}", method = RequestMethod.GET)
    public void search(@PathVariable String key, String[] field, HttpServletResponse response) {
        if (ArrayUtils.isEmpty(field)) {
            field = new String[]{"roleCode", "roleName"};
        }

        List<RoleInfo> listObjects = sysRoleManager.search(key, field);

        JsonResultUtils.writeSingleDataJson(listObjects, response, 
                JsonPropertyUtils.getExcludePropPreFilter(RoleInfo.class, "rolePowers"));
    }
    

    /**
     * 对角色信息进行模糊搜索，适用于带搜索条件的下拉框。
     *
     * @param type      搜索条件
     * @param field    需要搜索的字段，如为空，默认，roleCode,roleName
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/listRoles/{type}", method = RequestMethod.GET)
    public void listRoles(@PathVariable String type, String[] field, HttpServletRequest request,HttpServletResponse response) {
        if (ArrayUtils.isEmpty(field)) {
            field = new String[]{"roleCode", "roleName"};
        }
        Map<String,Object> filterMap = convertSearchColumn(request);
        filterMap.put("roleType", "S");
        if("S".equals(type)){
        	filterMap.put("NP_unitCode", true);
        	
        }else if("D".equals(type)){
        	CentitUserDetails user = getLoginUser(request);
        	IUserUnit unit = CodeRepositoryUtil.getUserPrimaryUnit(user.getUserCode());
        	if(unit!=null)
        		filterMap.put("unitCode",unit.getUnitCode());
        	else return;
        }
        List<RoleInfo> listObjects = sysRoleManager.listObjects(filterMap);

        JsonResultUtils.writeSingleDataJson(listObjects, response, 
                JsonPropertyUtils.getIncludePropPreFilter(RoleInfo.class, field));
    }
  
}
