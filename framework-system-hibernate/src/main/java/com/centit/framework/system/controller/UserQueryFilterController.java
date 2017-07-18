package com.centit.framework.system.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.UserQueryFilter;
import com.centit.framework.system.service.UserQueryFilterManager;
import com.centit.support.algorithm.DatetimeOpt;
import com.centit.support.algorithm.StringBaseOpt;
/**
 * UserQueryFilter  Controller.
 * create by scaffold 2016-02-29 
 * @author codefan@sina.com
 * 用户自定义过滤条件表null   
*/


@Controller
@RequestMapping("/userqueryfilter")
public class UserQueryFilterController  extends BaseController {
	//private static final Logger logger = LoggerFactory.getLogger(UserQueryFilterController.class);
	
	@Resource
	private UserQueryFilterManager userQueryFilterMag;	
	/*public void setUserQueryFilterMag(UserQueryFilterManager basemgr)
	{
		userQueryFilterMag = basemgr;
		//this.setBaseEntityManager(userQueryFilterMag);
	}*/

    /**
     * 查询所有   用户自定义过滤条件表  列表
     *
     * @param field    json中只保存需要的属性名
     * @param pageDesc  分页信息
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);        
        List<UserQueryFilter> listObjects = userQueryFilterMag.listObjects(searchColumn, pageDesc);

        SimplePropertyPreFilter simplePropertyPreFilter = null;
        if (ArrayUtils.isNotEmpty(field)) {
            simplePropertyPreFilter = new SimplePropertyPreFilter(UserQueryFilter.class, field);
        }

        if (null == pageDesc) {
            JsonResultUtils.writeSingleDataJson(listObjects, response, simplePropertyPreFilter);
            return;
        }
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    /**
     * 查找用户某个模块的所有过滤器
     * @param modelCode 按照模块列出用户所有的过滤器
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/list/{modelCode}", method = {RequestMethod.GET})
    public void listUserQueryFilter(@PathVariable String modelCode, HttpServletRequest request, HttpServletResponse response) {
        
    	List<UserQueryFilter> userFilters = 
    			userQueryFilterMag.listUserQueryFilterByModle(
    					super.getLoginUserCode(request), modelCode);

        JsonResultUtils.writeSingleDataJson(userFilters, response);
    }
    /**
     * 查询单个  用户自定义过滤条件表 
	
	 * @param filterNo  FILTER_NO
     *
     * @param response    {@link HttpServletResponse}
     */
    @RequestMapping(value = "/{filterNo}", method = {RequestMethod.GET})
    public void getUserQueryFilter(@PathVariable Long filterNo, HttpServletResponse response) {
    	
    	UserQueryFilter userQueryFilter =     			
    			userQueryFilterMag.getUserQueryFilter( filterNo);
        
        JsonResultUtils.writeSingleDataJson(userQueryFilter, response);
    }
    
    /**
     * 新增 用户自定义过滤条件表
     *
     * @param userQueryFilter  {@link UserQueryFilter}
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     */
    @RequestMapping(method = {RequestMethod.POST})
    public void createUserQueryFilter(
    		@Valid UserQueryFilter userQueryFilter, 
    		HttpServletRequest request,HttpServletResponse response) {
    	
    	userQueryFilter.setFilterNo(userQueryFilterMag.getNextFilterKey());
    	userQueryFilter.setCreateDate(DatetimeOpt.currentUtilDate());
    	userQueryFilter.setIsDefault("F");    	
    	
    	if(StringBaseOpt.isNvl(userQueryFilter.getUserCode()))
    		userQueryFilter.setUserCode(super.getLoginUserCode(request));
    	Serializable pk = userQueryFilterMag.saveNewObject(userQueryFilter);
        JsonResultUtils.writeSingleDataJson(pk,response);
    }
    
    /**
    * 保存用户最新查看筛选器
    * @param modelCode  modelCode
    * @param userQueryFilter  {@link UserQueryFilter}
    * @param request  {@link HttpServletRequest}
    * @param response {@link HttpServletResponse}
    */
   @RequestMapping(value = "/default/{modelCode}", method = {RequestMethod.POST,RequestMethod.PUT})
   public void createUserDefaultFilter(@PathVariable String modelCode,
		   @Valid UserQueryFilter userQueryFilter, 
   		HttpServletRequest request,HttpServletResponse response) {
   	
   	userQueryFilter.setCreateDate(DatetimeOpt.currentUtilDate());
   	userQueryFilter.setIsDefault("T");
   	userQueryFilter.setModleCode(modelCode);   	
   	if(StringBaseOpt.isNvl(userQueryFilter.getUserCode()))
   		userQueryFilter.setUserCode(super.getLoginUserCode(request));
   	
   	Serializable pk = userQueryFilterMag.saveUserDefaultFilter(userQueryFilter);
       JsonResultUtils.writeSingleDataJson(pk,response);
   }
   
   /**
    * 保存用户最新查看筛选器
    * @param modelCode  modelCode
    * @param request  {@link HttpServletRequest}
    * @param response {@link HttpServletResponse}
    */
   @RequestMapping(value = "/default/{modelCode}", method = {RequestMethod.GET})
   public void getUserDefaultFilter(@PathVariable String modelCode,
   		HttpServletRequest request,HttpServletResponse response) {
   	
	   UserQueryFilter userQueryFilter = 
			   userQueryFilterMag.getUserDefaultFilter(super.getLoginUserCode(request), modelCode);
       JsonResultUtils.writeSingleDataJson(userQueryFilter,response);
   }


    /**
     * 删除单个  用户自定义过滤条件表 
	
	 * @param filterNo  FILTER_NO
     * @param response  HttpServletResponse
     */
    @RequestMapping(value = "/{filterNo}", method = {RequestMethod.DELETE})
    public void deleteUserQueryFilter(@PathVariable Long filterNo, HttpServletResponse response) {
    	
    	boolean b = userQueryFilterMag.deleteUserQueryFilter(filterNo);
        if(b)
        	JsonResultUtils.writeBlankJson(response);
        else
        	JsonResultUtils.writeErrorMessageJson("不能删除默认过滤条件！", response);
    } 
    
    /**
     * 新增或保存 用户自定义过滤条件表 
    
	 * @param filterNo  FILTER_NO
	 * @param userQueryFilter  {@link UserQueryFilter}
     * @param response    {@link HttpServletResponse}
     */
    @RequestMapping(value = "/{filterNo}", method = {RequestMethod.PUT})
    public void updateUserQueryFilter(@PathVariable Long filterNo, 
    	@Valid UserQueryFilter userQueryFilter, HttpServletResponse response) {
    	
    	
    	UserQueryFilter dbUserQueryFilter  =     			
    			userQueryFilterMag.getObjectById( filterNo);
        
        if (null != userQueryFilter) {
        	dbUserQueryFilter .copy(userQueryFilter);
        	dbUserQueryFilter.setCreateDate(DatetimeOpt.currentUtilDate());
        	userQueryFilterMag.mergeObject(dbUserQueryFilter);
        } else {
            JsonResultUtils.writeErrorMessageJson("当前对象不存在", response);
            return;
        }

        JsonResultUtils.writeBlankJson(response);
    }
}
