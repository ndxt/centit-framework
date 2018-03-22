package com.centit.framework.system.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/environment")
public class EnvironmentController extends BaseController {

    private String optId = "environment";

    @Resource
    protected PlatformEnvironment platformEnvironment;


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


    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    @ResponseBody
    public ResponseSingleData environmentRefreshAll(
            HttpServletRequest request,HttpServletResponse response) {
        boolean reloadDc = platformEnvironment.reloadDictionary();
        boolean reloadSm = platformEnvironment.reloadSecurityMetadata();
        if(reloadDc && reloadSm)
            return new ResponseSingleData();
            //JsonResultUtils.writeSuccessJson(response);
        else
            return new ResponseSingleData( 500, "environmentRefreshAll failed！");
        //JsonResultUtils.writeErrorMessageJson("environmentRefreshAll failed！", response);
    }

}
