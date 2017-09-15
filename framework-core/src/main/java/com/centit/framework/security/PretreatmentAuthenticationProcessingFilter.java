package com.centit.framework.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.algorithm.StringRegularOpt;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.centit.framework.security.model.CheckFailLogs;
import com.centit.support.image.CaptchaImageUtil;

public class PretreatmentAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {
    public static final String AJAX_CHECK_CAPTCHA_RESULT = "ajaxCheckCaptchaResult";
    //是否有验证码
    private boolean checkCaptcha=false;

    private int checkCaptchaType=0;// 0 不验证，1 一起验证， 2 ajax 验证

    public void setCheckCaptchaType(int checkCaptchaType) {
        this.checkCaptchaType = checkCaptchaType;
    }

    public void setCheckCaptcha(boolean checkCaptcha) {
        this.checkCaptcha = checkCaptcha;
    }

    public void setMaxTryTimes(int maxTryTimes) {
        CheckFailLogs.setMaxTryTimes(maxTryTimes);
    }

    public void setCheckType(String checkType) {
        CheckFailLogs.setCheckType(checkType);
    }

    public void setLockMinites(int lockMinites) {
        CheckFailLogs.setLockMinites(lockMinites);
    }

    public void setCheckTimeTnterval(int checkTimeTnterval) {
        CheckFailLogs.setCheckTimeTnterval(checkTimeTnterval);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        int tryTimes = CheckFailLogs.getHasTriedTimes(request);
        if(checkCaptcha ||
                (checkCaptchaType==1
                        && CheckFailLogs.getMaxTryTimes() >= 0
                        && tryTimes >= CheckFailLogs.getMaxTryTimes())){
            String request_checkcode = request.getParameter(CaptchaImageUtil.REQUESTCHECKCODE);
            
            String session_checkcode = null;
            Object obj = request.getSession().getAttribute(CaptchaImageUtil.SESSIONCHECKCODE);  
            if (obj!=null)
                session_checkcode = obj.toString();
            request.getSession().removeAttribute(CaptchaImageUtil.SESSIONCHECKCODE);  
            
            if(! "nocheckcode".equals(request_checkcode)){          
                if(!CaptchaImageUtil.checkcodeMatch(session_checkcode, request_checkcode))
                //if(request_checkcode==null || ! request_checkcode.equalsIgnoreCase(session_checkcode)  )
                    throw new AuthenticationServiceException("bad checkcode");   
            }
        }
        
        if(CheckFailLogs.getMaxTryTimes() >= 0){
            if(checkCaptchaType==2 && tryTimes >= CheckFailLogs.getMaxTryTimes()){
                if(!StringRegularOpt.isTrue(
                        StringBaseOpt.objectToString(
                                request.getSession().getAttribute(AJAX_CHECK_CAPTCHA_RESULT)))){
                    throw new AuthenticationServiceException(
                            "Captcha input is error, please try late!");
                }
                request.getSession().setAttribute(AJAX_CHECK_CAPTCHA_RESULT,false);
            }else{
                if (CheckFailLogs.getMaxTryTimes() > 0 && CheckFailLogs.isLocked(request)) {
                    throw new AuthenticationServiceException("User is locked, please try late!");
                }
            }
        }
        
        Authentication auth = null;
                
        //if(!onlyPretreat || writeLog || CheckFailLogs.getMaxTryTimes() > 0){
            try{
                
                auth = super.attemptAuthentication(request, response);
                
                if(CheckFailLogs.getMaxTryTimes() >= 0){
                    CheckFailLogs.removeCheckFail(request);
                }
                
            }catch (AuthenticationException failed) {
                //System.err.println(failed.getMessage());
                if(CheckFailLogs.getMaxTryTimes() >= 0){
                    CheckFailLogs.plusCheckFail(request);
                }
                throw failed;
            }
        //}
        
        return auth;
    }
  
}
