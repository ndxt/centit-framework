/*
 * Copyright 2003 Jayson Falkner (jayson@jspinsider.com)
 * This code is from "Servlets and JavaServer pages; the J2EE Web Tier",
 * http://www.jspbook.com. You may freely use the code both commercially
 * and non-commercially. If you like the code, please pick up a copy of
 * the book and help support the authors, development of more free code,
 * and the JSP/Servlet/J2EE community.
 */
package com.centit.framework.filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheFilter implements Filter {
	protected Logger logger = LoggerFactory.getLogger(CacheFilter.class);
    ServletContext sc;
    FilterConfig fc;
    long cacheTimeout = Long.MAX_VALUE;

    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // check if was a resource that shouldn't be cached.
        String r = sc.getRealPath("");
        String path = fc.getInitParameter(request.getRequestURI());
        if (path != null && path.equals("nocache")) {
            //if (path == null || path.equals("nocache")) { //update by codefan@hotmail.com at 2010-7-16
            chain.doFilter(request, response);
            return;
        }
        if (path != null)
            path = r + path;
        else
            path = r;

        // customize to match parameters
        String id = request.getRequestURI() + request.getQueryString();
        // optionally append i18n sensitivity
        String localeSensitive = fc.getInitParameter("locale-sensitive");
        if (localeSensitive != null) {
            StringWriter ldata = new StringWriter();
            Enumeration<?> locales = request.getLocales();
            while (locales.hasMoreElements()) {
                Locale locale = (Locale) locales.nextElement();
                ldata.write(locale.getISO3Language());
            }
            id = id + ldata.toString();
        }
        File tempDir = (File) sc.getAttribute(
                "javax.servlet.context.tempdir");

        // get possible cache
        String temp = tempDir.getAbsolutePath();
        File file = new File(temp + id);

        // get current resource
        if (path == null) {
            path = sc.getRealPath(request.getRequestURI());
        }
        File current = new File(path);
        FileOutputStream fos = null;
        try {
            long now = Calendar.getInstance().getTimeInMillis();
            //set timestamp check
            if (!file.exists() || (file.exists() &&
                    current.lastModified() > file.lastModified()) ||
                    cacheTimeout < now - file.lastModified()) {
                String name = file.getAbsolutePath();
                name = name.substring(0, name.lastIndexOf("/") == -1 ? 0 : name.lastIndexOf("/"));
                new File(name).mkdirs();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                CacheResponseWrapper wrappedResponse =
                        new CacheResponseWrapper(response, baos);
                chain.doFilter(req, wrappedResponse);

                fos = new FileOutputStream(file);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            }
        } catch (ServletException e) {
            if (!file.exists()) {
                throw new ServletException(e);
            }
        } catch (IOException e) {
            if (fos != null)
                fos.close();
            logger.error(e.getMessage(),e);//e.printStackTrace();
            if (!file.exists()) {
                throw e;
            }

        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            String mt = sc.getMimeType(request.getRequestURI());
            response.setContentType(mt);
            ServletOutputStream sos = res.getOutputStream();
            for (int i = fis.read(); i != -1; i = fis.read()) {
                sos.write((byte) i);
            }
            fis.close();
        } catch (IOException e) {
        	logger.error(e.getMessage(),e);
            if (fis != null)
                fis.close();
        }

    }

    public void init(FilterConfig filterConfig) {
        this.fc = filterConfig;
        String ct = fc.getInitParameter("cacheTimeout");
        if (ct != null) {
            cacheTimeout = 60 * 1000 * Long.parseLong(ct);
        }
        this.sc = filterConfig.getServletContext();
    }

    public void destroy() {
        this.sc = null;
        this.fc = null;
    }
}
