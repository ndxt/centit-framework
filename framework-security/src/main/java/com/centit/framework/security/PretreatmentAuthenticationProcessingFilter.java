package com.centit.framework.security;

import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import com.centit.support.security.SecurityOptUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PretreatmentAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    private int checkCaptchaTime = 0 ; // 0 不验证, 1 登陆失败后 再次登陆验证, 2 始终验证
    private int checkCaptchaType = 0;  // 0 不验证, 1 一起验证, 2 ajax 验证

    /*private SessionRegistry sessionRegistry;

    public void setSessionRegistry(SessionRegistry sessionManger) {
        this.sessionRegistry = sessionManger;
    }*/

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

            if(! "nocheckcode".equals(requestCheckcode) && !StringBaseOpt.isNvl(sessionCheckcode)){
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

        //if(!onlyPretreat || writeLog || CheckFailLogs.getMaxTryTimes() > 0){
        try{

            String username = obtainUsername(request);
            username = SecurityOptUtils.decodeSecurityString(username);
            String password = obtainPassword(request);
            password = SecurityOptUtils.decodeSecurityString(password);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            Authentication auth = this.getAuthenticationManager().authenticate(authRequest);

            //Authentication auth = super.attemptAuthentication(request, response);
//            if(request.getSession(false)!=null) {
//                request.changeSessionId();
//            }
            //if(CheckFailLogs.getMaxTryTimes() >= 0){
                CheckFailLogs.removeCheckFail(request);
            //}
            return auth;
        }catch (AuthenticationException failed) {
            //System.err.println(failed.getMessage());
            //if(CheckFailLogs.getMaxTryTimes() >= 0){
                CheckFailLogs.plusCheckFail(request);
            //}
            throw failed;
        }
        //}
    }

    // 新建session
    // 代替 AjaxAuthenticationSuccessHandler 中的
    /*@Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);

        CentitUserDetails ud = (CentitUserDetails) authResult.getPrincipal();
        //tokenKey = UuidOpt.getUuidAsString();
        // 这个代码应该迁移到 AuthenticationProcessingFilter 的 successfulAuthentication 方法中
        ud.setLoginIp(WebOptUtils.getRequestAddr(request));
        sessionRegistry.registerNewSession(request.getSession().getId() ,ud);
    }*/
}
