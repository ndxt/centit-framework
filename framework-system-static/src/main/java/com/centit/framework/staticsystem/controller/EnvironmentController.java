package com.centit.framework.staticsystem.controller;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.staticsystem.service.IntegrationEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/environment")
public class EnvironmentController extends BaseController {

	@Resource
    protected PlatformEnvironment platformEnvironment;

	@Resource
	protected IntegrationEnvironment integrationEnvironment;
	
	@RequestMapping(value ="/test",method = RequestMethod.GET)
    public void test(
            HttpServletRequest request,HttpServletResponse response) {
		String testSessionString = new String("hello");
		request.getSession().setAttribute("test", (String) testSessionString);
		JsonResultUtils.writeSingleDataJson("test=zouwuyang", response);
	}
	@RequestMapping(value ="/reload/dictionary",method = RequestMethod.GET)
    public void reloadDictionary(
            HttpServletRequest request,HttpServletResponse response) {
		if(platformEnvironment.reloadDictionary())
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("reloadDictionary failed！", response);
	}

	@RequestMapping(value ="/reload/securitymetadata",method = RequestMethod.GET)
    public void reloadSecurityMetadata(
            HttpServletRequest request,HttpServletResponse response) {
		if(platformEnvironment.reloadSecurityMetadata())
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("reloadSecurityMetadata failed！", response);
	}
	
	@RequestMapping(value ="/reload/ipenvironment",method = RequestMethod.GET)
    public void reloadIPEnvironment(
            HttpServletRequest request,HttpServletResponse response) {
		if(integrationEnvironment.reloadIPEnvironmen())
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("reloadIPEnvironmen failed！", response);
	}

	@RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
	public void environmentRefreshAll(
			HttpServletRequest request,HttpServletResponse response) {
		boolean reloadEv = integrationEnvironment.reloadIPEnvironmen();
		boolean reloadDc = platformEnvironment.reloadDictionary();
		boolean reloadSm = platformEnvironment.reloadSecurityMetadata();
		if(reloadEv && reloadDc && reloadSm)
			JsonResultUtils.writeSuccessJson(response);
		else
			JsonResultUtils.writeErrorMessageJson("environmentRefreshAll failed！", response);
	}


	@RequestMapping(value ="/osinfos",method = RequestMethod.GET)
    public void listOsInfos(
            HttpServletRequest request,HttpServletResponse response) {
		
		ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listOsInfos());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
	}
	
	@RequestMapping(value ="/databaseinfos",method = RequestMethod.GET)
    public void listDatabaseInfos(
            HttpServletRequest request,HttpServletResponse response) {
		
		ResponseData resData = new ResponseData();
        resData.addResponseData(OBJLIST, integrationEnvironment.listDatabaseInfo());
		JsonResultUtils.writeResponseDataAsJson(resData, response);
	}
}
