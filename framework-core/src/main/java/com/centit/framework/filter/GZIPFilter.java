/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
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

/**
 * 网站常使用GZIP压缩算法对网页内容进行压缩，然后传给浏览器，以减小数据传输量，提高响应速度。浏览器接收到GZIP压缩数据后会自动解压并正确显示。
 * GZIP加速常用于解决网速慢的瓶颈。
 * 
 * 压缩Filter中需要先判断客户浏览器时候支持GZip自动解压，如果支持，则进行GZIP压缩，否则不压缩。判断的依据是浏览器提供的Header信息
 *  
 * 
 * @author hx
 * @create 2015-8-4
 * @version
 */
public class GZIPFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain chain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
          //支持的编码方式 
            String ae = request.getHeader("accept-encoding");
          //如果客户浏览器支持GZIP格式，则使用GZIP压缩数据
            if (ae != null && ae.indexOf("gzip") != -1) {
                // System.out.println("GZIP supported, compressing.");
                GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(
                        response);
                chain.doFilter(req, wrappedResponse);//doFilter,使用自定义的response
                wrappedResponse.finishResponse();//输出压缩数据
                return;
            }
            chain.doFilter(req, res);//否则不压缩
        }
    }

    public void init(FilterConfig filterConfig) {
        // noop
    }

    public void destroy() {
        // noop
    }
}
