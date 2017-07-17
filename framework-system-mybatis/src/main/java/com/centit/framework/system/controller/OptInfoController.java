package com.centit.framework.system.controller;

import java.io.IOException;
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

import com.centit.framework.components.OperationLogCenter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.basedata.OperationLog;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethod;
import com.centit.framework.system.service.OptInfoManager;
import com.centit.framework.system.service.OptMethodManager;
import com.centit.framework.system.service.SysRoleManager;
import com.centit.support.json.JsonPropertyUtils;

@Controller
@RequestMapping("/optinfo")
public class OptInfoController extends BaseController {
    @Resource
    private OptInfoManager optInfoManager;

    @Resource
    private OptMethodManager optMethodManager;

    @Resource
    @NotNull
    private SysRoleManager sysRoleManager;
    /**
     * 系统日志中记录
     */
    private String optId = "OPTINFO";//CodeRepositoryUtil.getCode("OPTID", "optInfo");
    
    /**
     * 查询所有系统业务
     *
     * @param field    需要显示的字段
     * @param id       父id 
     * @param struct   True根据父子节点排序的树形结构，False，排序的列表结构
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/sub", method = RequestMethod.GET)
    public void listFromParent(String[] field, String id, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);
        
        if (StringUtils.isNotBlank(id)) {        
            searchColumn.put("preOptId", id);
        }else{
        	searchColumn.put("NP_TOPOPT", "true");
        }
        
        List<OptInfo> listObjects = optInfoManager.listObjects(searchColumn);
        
        for (OptInfo opt : listObjects) {
        	//if("...".equals(opt.getOptRoute()))
        	 opt.setState(optInfoManager.hasChildren(opt.getOptId())?
               "closed":"open");
        }
        JsonResultUtils.writeSingleDataJson(listObjects, 
        		response, JsonPropertyUtils.getIncludePropPreFilter(OptInfo.class, field));
    }

    /**
     * 查询所有系统业务
     *
     * @param field    需要显示的字段
     * @param struct   True根据父子节点排序的树形结构，False，排序的列表结构
     * @param response HttpServletResponse
     */
    @RequestMapping(method = RequestMethod.GET)
    public void listAll(String[] field, boolean struct, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request); 
        List<OptInfo> listObjects = optInfoManager.listObjects(searchColumn);

        if (struct) {        	
            listObjects = optInfoManager.listObjectFormatTree(listObjects,false);
        }
        if(ArrayUtils.isNotEmpty(field))
        	JsonResultUtils.writeSingleDataJson(listObjects, response,
        		JsonPropertyUtils.getIncludePropPreFilter(OptInfo.class, field));
        else
        	JsonResultUtils.writeSingleDataJson(listObjects, response);
    }


    /**
     * 查询所有需要通过权限管理的业务
     *
     * @param field    需要显示的字段
     * @param struct   True根据父子节点排序的树形结构，False，排序的列表结构
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/poweropts",method = RequestMethod.GET)
    public void listPowerOpts( String[] field,  HttpServletResponse response) {         
        List<OptInfo> listObjects = optInfoManager.listSysAndOptPowerOpts();
        listObjects = optInfoManager.listObjectFormatTree(listObjects,true);
        if(ArrayUtils.isNotEmpty(field))
        	JsonResultUtils.writeSingleDataJson(listObjects, response,
        		JsonPropertyUtils.getIncludePropPreFilter(OptInfo.class, field));
        else
        	JsonResultUtils.writeSingleDataJson(listObjects, response);
    }
    
       
    /**
     * 查询所有项目权限管理的业务
     * @param field    需要显示的字段
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/itempoweropts",method = RequestMethod.GET)
    public void listItemPowerOpts( String[] field, HttpServletResponse response) {         
        List<OptInfo> listObjects = optInfoManager.listItemPowerOpts();
        listObjects = optInfoManager.listObjectFormatTree(listObjects,true);
        
        if(ArrayUtils.isNotEmpty(field))
        	JsonResultUtils.writeSingleDataJson(listObjects, response,
        		JsonPropertyUtils.getIncludePropPreFilter(OptInfo.class, field));
        else
        	JsonResultUtils.writeSingleDataJson(listObjects, response);        
    }
    
    
    /**
     * 查询某个部门权限的业务
     *
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/unitpoweropts/{unitCode}",method = RequestMethod.GET)
    public void listUnitPowerOpts(@PathVariable String unitCode, String[] field,
    		HttpServletResponse response) {         
        List<OptInfo> listObjects = optInfoManager.listOptWithPowerUnderUnit(unitCode);
        listObjects = optInfoManager.listObjectFormatTree(listObjects,false);
        
        if(ArrayUtils.isNotEmpty(field))
        	JsonResultUtils.writeSingleDataJson(listObjects, response,
        		JsonPropertyUtils.getIncludePropPreFilter(OptInfo.class, field));
        else
        	JsonResultUtils.writeSingleDataJson(listObjects, response);  
    }
    /**
     * 新增业务
     *
     * @param optInfo OptInfo
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST})
    public void createOptInfo(@Valid OptInfo optInfo, HttpServletRequest request, HttpServletResponse response) {
        
    	if (StringUtils.isBlank(optInfo.getOptRoute()) ) {
            optInfo.setOptRoute("...");
        }    	
        // 解决问题新增菜单没有url
        if (StringUtils.isBlank(optInfo.getOptUrl()) || "...".equals(optInfo.getOptUrl())) {
            optInfo.setOptUrl(optInfo.getOptRoute());
        }
        OptInfo parentOpt = optInfoManager.getOptInfoById(optInfo.getPreOptId());
        if(parentOpt==null)
        	optInfo.setPreOptId("0");
        optInfoManager.saveNewOptInfo(optInfo);      
       
        //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        String optUser = this.getLoginUser(request).getUserCode();
        OperationLogCenter.logNewObject( optUser, optId,optInfo.getId(), OperationLog.P_OPT_LOG_METHOD_C, 
        		"新增业务:",optInfo);
        /*********log*********/
    }

    /**
     * optId是否已存在
     *
     * @param optId
     * @param response
     */
    @RequestMapping(value = "/notexists/{optId}", method = {RequestMethod.GET})
    public void isNotExists(@PathVariable String optId, HttpServletResponse response) throws IOException {
        OptInfo optInfo = optInfoManager.getObjectById(optId);
        JsonResultUtils.writeOriginalObject(null == optInfo, response);
    }

    /**
     * 更新
     *
     * @param optId    主键
     * @param optInfo  OptInfo
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/{optId}", method = {RequestMethod.PUT})
    public void edit(@PathVariable String optId, @Valid OptInfo optInfo,
            HttpServletRequest request,HttpServletResponse response) {
        OptInfo dbOptInfo = optInfoManager.getObjectById(optId);
        if (null == dbOptInfo) {
            JsonResultUtils.writeErrorMessageJson("当前对象不存在", response);
            return;
        } else {
        	if(!StringUtils.equals(dbOptInfo.getPreOptId(), optInfo.getPreOptId())){
        		 OptInfo parentOpt = optInfoManager.getOptInfoById(optInfo.getPreOptId());
        	     if(parentOpt==null)
        	        	optInfo.setPreOptId(dbOptInfo.getPreOptId());
        	}        	
            /*********log*********/
        	OptInfo oldValue= new OptInfo();
        	BeanUtils.copyProperties(dbOptInfo,oldValue);
            /*********log*********/
  
            for (OptMethod optDef : optInfo.getOptMethods()) {
                if (StringUtils.isBlank(optDef.getOptCode())) {
                    optDef.setOptCode(optMethodManager.getNextOptCode());
                }                
             }
            BeanUtils.copyProperties(optInfo, dbOptInfo, new String[]{"optMethods","dataScopes"});
            
            dbOptInfo.addAllOptMethods(optInfo.getOptMethods());
            dbOptInfo.addAllDataScopes(optInfo.getDataScopes());
            optInfoManager.updateOptInfo(dbOptInfo);
            //刷新缓存
            sysRoleManager.loadRoleSecurityMetadata();
            /*********log*********/
            String optUser = this.getLoginUser(request).getUserCode();
            //(userCode, optId, optTag, optMethod, optContent, newObject, oldObject);
            OperationLogCenter.logUpdateObject(optUser,this.optId,dbOptInfo.getOptId(),
            		OperationLog.P_OPT_LOG_METHOD_U, "更新业务菜单"+ dbOptInfo.getOptId(), oldValue, dbOptInfo);
            /*********log*********/
        }
        
        JsonResultUtils.writeSuccessJson(response);
    }
    
     /**
     * 删除系统业务
     *
     * @param optId
     * @param response
     */
    @RequestMapping(value = "/{optId}", method = {RequestMethod.DELETE})
    public void delete(@PathVariable String optId, HttpServletRequest request, HttpServletResponse response) {
       OptInfo dboptInfo=optInfoManager.getObjectById(optId);

        optInfoManager.deleteOptInfo(dboptInfo);
        //刷新缓存
        sysRoleManager.loadRoleSecurityMetadata();
        JsonResultUtils.writeBlankJson(response);
        /*********log*********/
        String optUser = this.getLoginUser(request).getUserCode();
        OperationLogCenter.logDeleteObject(optUser,this.optId,dboptInfo.getId() ,
        		OperationLog.P_OPT_LOG_METHOD_D,  "已删除",dboptInfo);
        /*********log*********/
    }

    /**
     * 查询单条数据
     *
     * @param optId
     * @param response
     */
    @RequestMapping(value = "/{optId}", method = {RequestMethod.GET})
    public void getOptInfoById(@PathVariable String optId, HttpServletResponse response) {
        OptInfo dbOptInfo = optInfoManager.getOptInfoById(optId);

        JsonResultUtils.writeSingleDataJson(dbOptInfo, response);
    }

    /**
     * 新增页面时获取OptDef主键
     *
     * @param response
     */
    @RequestMapping(value = "/nextOptCode", method = RequestMethod.GET)
    public void getNextOptCode(HttpServletResponse response) {
        String optCode = optMethodManager.getNextOptCode();

        ResponseData responseData = new ResponseData();
        responseData.addResponseData("optCode", optCode);

        JsonResultUtils.writeResponseDataAsJson(responseData, response);
    }

    /**
     * 新建或更新业务操作
     * @param optId
     * @param optCode
     * @param optDef
     * @param response
     */
    @RequestMapping(value = "/{optId}/{optCode}", method = {RequestMethod.POST, RequestMethod.PUT})
    public void optDefEdit(@PathVariable String optId, @PathVariable String optCode, @Valid OptMethod optDef,
                           HttpServletResponse response) {
        OptInfo optInfo = optInfoManager.getObjectById(optId);
        if (null == optInfo) {
            JsonResultUtils.writeSingleErrorDataJson(
            		ResponseData.ERROR_INTERNAL_SERVER_ERROR,
            		"数据库不匹配", "数据库中不存在optId为"+optId+"的业务信息。", response);
            return;
        }

        OptMethod dbOptDef = optMethodManager.getObjectById(optCode);
        if (null == dbOptDef) {
            optDef.setOptId(optId);
            optMethodManager.mergeObject(optDef);
        } else {
            BeanUtils.copyProperties(optInfo, dbOptDef, new String[]{"optInfo"});
            optMethodManager.mergeObject(dbOptDef);
        }

        JsonResultUtils.writeSuccessJson(response);
    }
    
    @RequestMapping(value = "/allOptInfo", method = RequestMethod.GET)
    public void loadAllOptInfo(HttpServletResponse response) { 
        List<OptInfo> optInfos = optInfoManager.listObjects();
        JsonResultUtils.writeSingleDataJson(optInfos,response);
    }
    
    @RequestMapping(value = "/allOptMethod", method = RequestMethod.GET)
    public void loadAllOptMethod(HttpServletResponse response) { 
        List<OptMethod> optDefs = optMethodManager.listObjects();
        JsonResultUtils.writeSingleDataJson(optDefs,response);
    }
}
