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

    public String getOptId (){
        return "environment";
    }



    @RequestMapping(value ="/test",method = RequestMethod.GET)
    public void test(
            HttpServletRequest request,HttpServletResponse response) {
        String testSessionString = new String("hello");
        request.getSession().setAttribute("test", (String) testSessionString);
        JsonResultUtils.writeSingleDataJson("test=zouwuyang", response);
    }

    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    public void environmentRefreshAll(HttpServletResponse response) {
        CodeRepositoryCache.evictAllCache();
        JsonResultUtils.writeMessageJson("缓存全部失效！", response);
    }

}
