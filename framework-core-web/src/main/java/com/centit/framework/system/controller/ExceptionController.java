package com.centit.framework.system.controller;

import com.centit.framework.common.JsonResultUtils;
import com.centit.framework.common.ResponseData;
import com.centit.framework.common.WebOptUtils;
import com.centit.framework.core.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通用的处理控制器，主要用于处理异常情况，
 *
 * @author codefan
 * 2014年10月24日
 */
@SuppressWarnings("unused")
@Controller
@RequestMapping("/exception")
public class ExceptionController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);
    public String getOptId (){
        return "exception";
    }

    /**
     * 访问当前无权限URL请求后返回地址
     * @return 访问当前无权限URL请求后返回地址
     */
    @RequestMapping(value = "/accessDenied")
    public String accessDenied() {
        return "forward:/system/exception/error/403";
    }

    /**
     * web.xml中配置的异常捕捉
     * CentitMappingExceptionResolver类中异常捕捉也会跳转过来
     * web.xml中捕捉的异常不能得到异常的具体内容，CentitMappingExceptionResolver可以将异常信息传递过来
     *
     * @param code     错误码
     * @param request request
     * @param response response
     * @throws IOException IOException
     * @return 异常页面
     */
    @RequestMapping(value = "/error/{code}")
    public String errorPage(@PathVariable int code, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (WebOptUtils.isAjax(request)) {
            //框架前端AngularJs均由Ajax请求接收数据
            String errorMessage;

            switch (code) {
                case 404:
                    errorMessage = getI18nMessage("error.404.resource_not_found", request);
                    break;
                case 403:
                    {
                        errorMessage =(String) request.getAttribute("CENTIT_SYSTEM_ERROR_MSG");
                        if(StringUtils.isBlank(errorMessage)){
                            Exception ex = null;
                            Object accessException = request.getSession().getAttribute(WebAttributes.ACCESS_DENIED_403);
                            if (accessException instanceof Exception) {
                                ex = (Exception) accessException;
                            } else if (null != accessException){
                                ex = new Exception(accessException.toString());
                            }
                            if (ex == null) {
                                Object authenticationException = request.getSession().getAttribute(
                                    WebAttributes.AUTHENTICATION_EXCEPTION);
                                if (authenticationException instanceof Exception) {
                                    ex = (Exception) authenticationException;
                                } else if (null != authenticationException){
                                    ex = new Exception(authenticationException.toString());
                                }
                            }
                            //触发异常的类
                            if (null != ex) {
                                errorMessage = ex.getMessage();
                            }else
                                errorMessage = getI18nMessage("error.403.access_forbidden", request);
                        }
                    }
                    break;
                case 302:
                case 401:
                    errorMessage = getI18nMessage("error.302.user_not_login", request);
                    break;
                default:
                    errorMessage = getI18nMessage("error.500.inner_error_1", request, "exception-"+code);
                    Exception ex = (Exception) request.getAttribute("CENTIT_SYSTEM_ERROR_EXCEPTION");
                    //触发异常的类
                    if (null != ex) {
                        errorMessage = ex.getMessage();
                        HandlerMethod handler = (HandlerMethod) request.getAttribute("CENTIT_SYSTEM_ERROR_HANDLER");
                        if (null != handler) {
                            errorMessage = getI18nMessage("error.500.exception_4_cmem", request,
                                handler.getBean().getClass().getName(), handler.getMethod().getName(),
                                ex.getClass().getName(), ex.getMessage());
                            logger.error(errorMessage);
                        }
                    }
                    break;
            }
            JsonResultUtils.writeErrorMessageJson(code, errorMessage, response);
            return null;
            //throw new ObjectException(errorMessage, ObjectException.ExceptionType.SYSTEM);
        } else {
            return "exception/" + code;
        }
    }

    /**
     * 校验session是否超时
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return 超时跳转异常
     */
    //@ApiOperation(value = "校验session是否超时", notes = "校验session是否超时")
    @RequestMapping(value = "/timeout" , method = RequestMethod.GET)
    public String sessionExpired(
        HttpServletRequest request,HttpServletResponse response) {
        if (WebOptUtils.isAjax(request)) {
            JsonResultUtils.writeErrorMessageJson(ResponseData.ERROR_SESSION_TIMEOUT,
                getI18nMessage("error.709.session_timeout", request), response);
            return null;
        }else{
            return "exception/timeout";
        }
    }
}
