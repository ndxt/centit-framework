package com.centit.framework.system.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.components.CodeRepositoryCache;
import com.centit.framework.core.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/environment")
@Api(tags= "框架系统刷新缓存接口")
public class EnvironmentController extends BaseController {

    public String getOptId (){
        return "environment";
    }

    /**
     * 系统心跳，有回复说明系统没有死机
     * @param response 输出流
     */
    @ApiOperation(value="系统心跳",notes="系统心跳，有回复说明系统没有死机")
    @RequestMapping(value ="/heartbeat",method = RequestMethod.GET)
    public void heartbeat(HttpServletResponse response) {
        JsonResultUtils.writeSingleDataJson("heartbeat Ok!", response);
    }

    /**
     * 刷新框架中的所有缓存信息
     * @param response 输出流
     */
    @ApiOperation(value="刷新框架中的所有缓存信息",notes="刷新框架中的所有缓存信息")
    //@PreAuthorize("hasPermission(#contact, 'admin')")
    @RequestMapping(value ="/reload/refreshall",method = RequestMethod.GET)
    public void environmentRefreshAll(HttpServletResponse response) {
        CodeRepositoryCache.evictAllCache();
        JsonResultUtils.writeMessageJson("缓存全部失效！", response);
    }

    /**
     * 刷新指定的缓存
     * @param cacheCode 缓存名称
     * @param response 输出流
     */
    @ApiOperation(value = "刷新指定的缓存", notes = "根据缓存名称刷新指定的缓存")
    @ApiImplicitParam(
        name = "cacheCode", value="缓存名称",
        required= true, paramType = "path", dataType= "String"
    )
    @RequestMapping(value ="/reload/refresh/{cacheCode}",method = RequestMethod.GET)
    public void environmentRefresh(@PathVariable String cacheCode, HttpServletResponse response) {
        CodeRepositoryCache.evictCache(cacheCode);
        JsonResultUtils.writeMessageJson("缓存"+cacheCode+"已失效！", response);
    }

    /**
     * 刷新框架中和权限有关的缓存
     * @param response 输出流
     */
    @ApiOperation(value = "刷新框架中和权限有关的缓存", notes = "刷新框架中和权限有关的缓存")
    @RequestMapping(value ="/reload/refreshpower",method = RequestMethod.GET)
    public void environmentRefreshPower(HttpServletResponse response) {
        CodeRepositoryCache.evictCache("OptInfo");
        CodeRepositoryCache.evictCache("OptMethod");
        CodeRepositoryCache.evictCache("RoleInfo");
        CodeRepositoryCache.evictCache("RolePower");
        CodeRepositoryCache.evictCache("UserRoles");
        CodeRepositoryCache.evictCache("RoleUsers");
        CodeRepositoryCache.evictCache("UnitRoles");
        CodeRepositoryCache.evictCache("RoleUnits");

        JsonResultUtils.writeMessageJson("和权限有关的缓存全部失效！", response);
    }
}
