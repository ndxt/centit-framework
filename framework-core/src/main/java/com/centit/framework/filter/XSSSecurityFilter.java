/**
 * 
 */
package com.centit.framework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.centit.framework.core.common.JsonResultUtils;
import com.centit.framework.core.common.ResponseData;
import com.centit.framework.common.WebOptUtils;

/**
 * @author hx
 * 安全信息审核类
 * <!--   解决xss漏洞   (新) beg-->
 *	<filter>
 *		<filter-name>XSSFiler</filter-name>
 *		<filter-class>com.centit.framework.filter.XSSSecurityFilter</filter-class>
 *		<init-param>
 *			<param-name>securityconfig</param-name>
 *			<param-value>
 *			    /securityconfig/xss_security_config.xml
 *			</param-value>
 *		</init-param>
 *	</filter>
 *	<filter-mapping>
 *		<filter-name>XSSFiler</filter-name>
 *		<url-pattern>/*</url-pattern>
 *	</filter-mapping>
 *   <!--   解决xss漏洞   (新) end-->
 *
 */
public class XSSSecurityFilter implements Filter{

	private static Logger logger = LoggerFactory.getLogger(XSSSecurityFilter.class);
	
	/**
	 * 销毁操作
	 */
	public void destroy() {
		logger.info("XSSSecurityFilter destroy() begin");
		XSSSecurityManager.destroy();
		logger.info("XSSSecurityFilter destroy() end");
	}

	/**
	 * 安全审核
	 * 读取配置信息
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// 判断是否使用HTTP
        checkRequestResponse(request, response);
        // 转型
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // http信息封装类
		XSSHttpRequestWrapper xssRequest = new XSSHttpRequestWrapper(httpRequest);
		XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
        // 对request信息进行封装并进行校验工作，若校验失败（含非法字符），根据配置信息进行日志记录和请求中断处理
        if(xssRequest.validateParameter(httpResponse)){
        	if(cfg.isLog()){
        	    //InetAddress inet = null;
                String ip = request.getRemoteAddr();
                String url = httpRequest.getRequestURI();
        	    logger.error("XSS IP:"+ip +" URL:" + url);
        		// 记录攻击访问日志
        		// 可使用数据库、日志、文件等方式
        	}
        	if(WebOptUtils.isAjax(httpRequest)){
        		String ip = request.getRemoteAddr();
                String url = httpRequest.getRequestURI();
        		JsonResultUtils.writeAjaxErrorMessage(ResponseData.ERROR_NOT_ACCEPTABLE,
        				"XSS IP:"+ip +" URL:" + url, httpResponse);
        		return;
        	}
        	
        	if(cfg.isChain()){
        		httpRequest.getRequestDispatcher(XSSSecurityConfig.FILTER_ERROR_PAGE).forward( httpRequest, httpResponse);
        		return;
    		}
        }
        httpResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        chain.doFilter(xssRequest, httpResponse);
	}

	/**
	 * 初始化操作
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		XSSSecurityManager.init(filterConfig);
	}

	/**
     * 判断Request ,Response 类型
     * @param request
     *            ServletRequest
     * @param response
     *            ServletResponse
     * @throws ServletException 
     */
    private void checkRequestResponse(ServletRequest request,
            ServletResponse response) throws ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Can only process HttpServletRequest");

        }
        if (!(response instanceof HttpServletResponse)) {
            throw new ServletException("Can only process HttpServletResponse");
        }
        //add by lay 限制请求方法
        HttpServletRequest httpReq = (HttpServletRequest)request;
        if(!httpReq.getMethod().equals("POST") && !httpReq.getMethod().equals("GET")){
            throw new ServletException("Can only process POST or GET Method");
        }
        //end add
    }
}
