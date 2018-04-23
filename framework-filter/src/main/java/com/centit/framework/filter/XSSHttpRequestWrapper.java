package com.centit.framework.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;


/**
 * @author hx
 * request信息封装类，用于判断、处理request请求中特殊字符
 */
public class XSSHttpRequestWrapper extends HttpServletRequestWrapper {
    private final String [] ignoreUrls = new String[]{
            "login","publicinfo","index.do","showMain.do","init.jsp",
            "download","listSelectOrg","selectList"};
    /**
     * 封装http请求
     * @param request request
     */
    public XSSHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        // 若开启特殊字符替换，对特殊字符进行替换
        XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
        if(cfg.isReplace()){
            XSSSecurityManager.securityReplace(name);
        }
        return value;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
        // 若开启特殊字符替换，对特殊字符进行替换
        if(cfg.isReplace()){
            XSSSecurityManager.securityReplace(name);
        }
        return value;
    }

    /**
     * 没有违规的数据，就返回false;
     *
     * @return
     */
    private boolean checkHeader(){
        Enumeration<String> headerParams = this.getHeaderNames();
        //ADD BY LAY 检验Referer
        String contextPathDefault = this.getScheme()+"://"+this.getServerName()
                + ("80".equals(this.getServerPort())? "" : (":" + this.getServerPort()))
                + this.getContextPath();
        String referer = null;
        //END ADD
        while(headerParams.hasMoreElements()){
            String headerName = headerParams.nextElement();
            String headerValue = this.getHeader(headerName);
            if(XSSSecurityManager.matches(headerValue)){
                return true;
            }
            //ADD BY LAY 检验Referer
            if("referer".equals(headerName)){
                referer = headerValue;
            }
            //END ADD
        }
        //ADD BY LAY 检验Referer,首次打开的时候正常操作这个参数会为空
        XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
        if((referer==null && !allowRefererEmpty()) ||
                (referer!=null && !referer.contains(contextPathDefault)
                && !referer.contains(cfg.getRefererAllowUrlExtra()))){
            return true;
        }
        //END ADD
        return false;
    }
    /**
     * 当用户没有登录的时候我们允许referer为空
     * @return
     */
    private boolean allowRefererEmpty(){
        SecurityContext scontext = SecurityContextHolder.getContext();
        Authentication auth = scontext.getAuthentication();
      
        Object o = null;
        //如果无法获取用户登录身份信息，直接让他过,不进行后处理
        if(auth != null ){
            o = auth.getPrincipal();
            if(o==null || !(o instanceof UserDetails)){
                return true;
            }
        }else{
            return true;
        }
        
        String reqUrl = this.getServletPath();
        for(String url:ignoreUrls){
            if(reqUrl.indexOf(url) > -1){
                return true;
            }
        }
        return false;
    }

    /**
     * 没有违规的数据，就返回false;
     *
     * @return
     */
    private boolean checkParameter(){
        Map<String,String[]> submitParams = this.getParameterMap();
        if(submitParams==null)
            return false;
        //Set<String> submitNames = submitParams.keySet();
        for(Map.Entry<String,String[]> ent : submitParams.entrySet()){
            for(String submitValue : ent.getValue()){
                if(XSSSecurityManager.matches(submitValue)){
                    return true;
                }
            }
        }
        return false;
    }

   
    /**
     * 没有违规的数据，就返回false;
     * 若存在违规数据，根据配置信息判断是否跳转到错误页面
     * @param response response
     * @return 是否有违规的数据
     * @throws IOException  IOException
     * @throws ServletException  ServletException
     */
    public boolean validateParameter(HttpServletResponse response) throws ServletException, IOException{
        XSSSecurityConfig cfg = XSSSecurityConfig.getConfig();
        // 开始header校验，对header信息进行校验
        if(cfg.isCheckHeader()){
            if(this.checkHeader()){
                return true;
            }
        }
        // 开始parameter校验，对parameter信息进行校验
        if(cfg.isCheckParameter()){
            if(this.checkParameter()){
                return true;
            }
        }
        return false;
    }

}
