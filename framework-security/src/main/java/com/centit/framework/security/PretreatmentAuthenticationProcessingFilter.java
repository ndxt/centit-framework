package com.centit.framework.security;

import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PretreatmentAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

	private int checkCaptchaTime = 0 ; // 0 不验证, 1 登陆失败后 再次登陆验证, 2 始终验证
    private int checkCaptchaType = 0;  // 0 不验证, 1 一起验证, 2 ajax 验证

	public void setCheckCaptchaTime(int checkCaptchaTime) {
		this.checkCaptchaTime = checkCaptchaTime;
	}

	public void setCheckCaptchaType(int checkCaptchaType) {
        this.checkCaptchaType = checkCaptchaType;
    }

    public void setRetryMaxTryTimes(int maxTryTimes) {
        CheckFailLogs.setMaxTryTimes(maxTryTimes);
    }

    public void setRetryCheckType(String checkType) {
        CheckFailLogs.setCheckType(checkType);
    }

    public void setRetryLockMinites(int lockMinites) {
        CheckFailLogs.setLockMinites(lockMinites);
    }

    public void setRetryCheckTimeTnterval(int checkTimeTnterval) {
        CheckFailLogs.setCheckTimeTnterval(checkTimeTnterval);
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

	    if (CheckFailLogs.getMaxTryTimes() > 0 && CheckFailLogs.isLocked(request)) {
		    throw new AuthenticationServiceException("User is locked, please try late!");
	    }

		int tryTimes = CheckFailLogs.getHasTriedTimes(request);
        if(checkCaptchaType == 1 && ( checkCaptchaTime == 2 ||
                (checkCaptchaTime == 1
                        //&& CheckFailLogs.getMaxTryTimes() >= 0
                        && tryTimes > 0 ))){
            String requestCheckcode = request.getParameter(CaptchaImageUtil.REQUESTCHECKCODE);

            String sessionCheckcode = StringBaseOpt.castObjectToString(
                    request.getSession().getAttribute(CaptchaImageUtil.SESSIONCHECKCODE));

            request.getSession().removeAttribute(CaptchaImageUtil.SESSIONCHECKCODE);  
            
            if(! "nocheckcode".equals(requestCheckcode)){
                if(!CaptchaImageUtil.checkcodeMatch(sessionCheckcode, requestCheckcode))
                //if(request_checkcode==null || ! request_checkcode.equalsIgnoreCase(session_checkcode)  )
                    throw new AuthenticationServiceException("bad checkcode");   
            }
        }

	    if(checkCaptchaType == 2 && ( checkCaptchaTime == 2 ||
			    (checkCaptchaTime == 1
					    //&& CheckFailLogs.getMaxTryTimes() >= 0
					    && tryTimes > 0 ))) {
		    if (!BooleanBaseOpt.castObjectToBoolean(
				    request.getSession().getAttribute(
				            SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT),
				    false)) {
			    throw new AuthenticationServiceException(
					    "Captcha input is error, please try late!");
		    }
		    request.getSession().setAttribute(
                    SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT, false);
	    }
        Authentication auth = null;
        //if(!onlyPretreat || writeLog || CheckFailLogs.getMaxTryTimes() > 0){
        try{
            auth = super.attemptAuthentication(request, response);
            //if(CheckFailLogs.getMaxTryTimes() >= 0){
                CheckFailLogs.removeCheckFail(request);
            //}
        }catch (AuthenticationException failed) {
            //System.err.println(failed.getMessage());
            //if(CheckFailLogs.getMaxTryTimes() >= 0){
                CheckFailLogs.plusCheckFail(request);
            //}
            throw failed;
        }
        //}
        return auth;
    }
  
}
