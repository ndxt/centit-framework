package com.centit.framework.system.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseSingleData;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.controller.BaseController;
import com.centit.framework.model.adapter.PlatformEnvironment;
import com.centit.framework.security.model.CentitSecurityMetadata;
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
    public void reloadDictionary(HttpServletResponse response) {
        CodeRepositoryCache.evictAllCache();
        JsonResultUtils.writeMessageJson("系统数据字典全部失效！", response);
    }

    @RequestMapping(value ="/reload/securitymetadata",method = RequestMethod.GET)
    public void reloadSecurityMetadata(HttpServletResponse response) {
        CentitSecurityMetadata.evictAllCache();
        JsonResultUtils.writeMessageJson("权限相关缓存失效！", response);
    }

    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    public void environmentRefreshAll(HttpServletResponse response) {
        CodeRepositoryCache.evictAllCache();
        CentitSecurityMetadata.evictAllCache();
        JsonResultUtils.writeMessageJson("缓存全部失效！", response);
    }

}
