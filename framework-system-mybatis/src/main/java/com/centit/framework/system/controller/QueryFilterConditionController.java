package com.centit.framework.system.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.core.dao.PageDesc;
import com.centit.framework.system.po.QueryFilterCondition;
import com.centit.framework.system.service.QueryFilterConditionManager;
/**
 * QueryFilterCondition  Controller.
 * create by scaffold 2016-03-01 
 * @author codefan@sina.com
 * 系统内置查询方式null   
*/


@Controller
@RequestMapping("/queryfiltercondition")
public class QueryFilterConditionController  extends BaseController {
	//private static final Logger logger = LoggerFactory.getLogger(QueryFilterConditionController.class);
	
	@Resource
	private QueryFilterConditionManager queryFilterConditionMag;	
	/*public void setQueryFilterConditionMag(QueryFilterConditionManager basemgr)
	{
		queryFilterConditionMag = basemgr;
		//this.setBaseEntityManager(queryFilterConditionMag);
	}*/

    /**
     * 查询所有   系统内置查询方式  列表
     *
     * @param field    json中只保存需要的属性名
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return {data:[]}
     */
    @RequestMapping(method = RequestMethod.GET)
    public void list(String[] field, PageDesc pageDesc, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> searchColumn = convertSearchColumn(request);        
        
        JSONArray listObjects = queryFilterConditionMag.listQueryFilterConditionsAsJson(field,searchColumn, pageDesc);

        if (null == pageDesc) {
            JsonResultUtils.writeSingleDataJson(listObjects, response);
            return;
        }
        
        ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, listObjects);
        resData.addResponseData(PAGE_DESC, pageDesc);

        JsonResultUtils.writeResponseDataAsJson(resData, response);
    }
    
    /**
     * 查询单个  系统内置查询方式 
	
	 * @param conditionNo  CONDITION_NO
     * @param catalogCode 主键
     * 
     * @param response    {@link HttpServletResponse}
     * @return {data:{}}
     */
    @RequestMapping(value = "/{conditionNo}", method = {RequestMethod.GET})
    public void getQueryFilterCondition(@PathVariable Long conditionNo, HttpServletResponse response) {
    	
    	QueryFilterCondition queryFilterCondition =     			
    			queryFilterConditionMag.getObjectById( conditionNo);
        
        JsonResultUtils.writeSingleDataJson(queryFilterCondition, response);
    }
    
    /**
     * 新增 系统内置查询方式
     *
     * @param queryFilterCondition  {@link QueryFilterCondition}
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST})
    public void createQueryFilterCondition(@Valid QueryFilterCondition queryFilterCondition, HttpServletResponse response) {
    	Serializable pk = queryFilterConditionMag.saveNewObject(queryFilterCondition);
        JsonResultUtils.writeSingleDataJson(pk,response);
    }

    /**
     * 删除单个  系统内置查询方式 
	
	 * @param conditionNo  CONDITION_NO
     */
    @RequestMapping(value = "/{conditionNo}", method = {RequestMethod.DELETE})
    public void deleteQueryFilterCondition(@PathVariable Long conditionNo, HttpServletResponse response) {
    	
    	queryFilterConditionMag.deleteObjectById( conditionNo);
        
        JsonResultUtils.writeBlankJson(response);
    } 
    
    /**
     * 新增或保存 系统内置查询方式 
    
	 * @param conditionNo  CONDITION_NO
	 * @param queryFilterCondition  {@link QueryFilterCondition}
     * @param response    {@link HttpServletResponse}
     */
    @RequestMapping(value = "/{conditionNo}", method = {RequestMethod.PUT})
    public void updateQueryFilterCondition(@PathVariable Long conditionNo, 
    	@Valid QueryFilterCondition queryFilterCondition, HttpServletResponse response) {
    	
    	
    	QueryFilterCondition dbQueryFilterCondition  =     			
    			queryFilterConditionMag.getObjectById( conditionNo);
        
        

        if (null != queryFilterCondition) {
        	dbQueryFilterCondition.copy(queryFilterCondition);
        	queryFilterConditionMag.mergeObject(dbQueryFilterCondition);
        } else {
            JsonResultUtils.writeErrorMessageJson("当前对象不存在", response);
            return;
        }

        JsonResultUtils.writeBlankJson(response);
    }
}
