package com.centit.framework.security;

import com.centit.framework.common.WebOptUtils;
import com.centit.support.algorithm.BooleanBaseOpt;
import com.centit.support.algorithm.StringBaseOpt;
import com.centit.support.image.CaptchaImageUtil;
import com.centit.support.security.SecurityOptUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PretreatmentAuthenticationProcessingFilter extends UsernamePasswordAuthenticationFilter {

    private int checkCaptchaTime = 0; // 0 不验证, 1 登陆失败后 再次登陆验证, 2 始终验证
    private int checkCaptchaType = 0;  // 0 不验证、 1 一起验证, 2 ajax 验证

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
        CheckFailLogs.setLockMinutes(lockMinites);
    }

    public void setRetryCheckTimeTnterval(int checkTimeTnterval) {
        CheckFailLogs.setCheckTimeInterval(checkTimeTnterval);
    }

    protected MessageSource messageSource;

    public PretreatmentAuthenticationProcessingFilter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (CheckFailLogs.getMaxTryTimes() > 0 && CheckFailLogs.isLocked(request)) {
            throw new AuthenticationServiceException(
                messageSource.getMessage("error.302.user_is_locked", null,
                    "User is locked, please try late!", WebOptUtils.getCurrentLocale(request)));
        }

        int tryTimes = CheckFailLogs.getHasTriedTimes(request);
        if (checkCaptchaType != 0 &&
            (checkCaptchaTime == 2 || (checkCaptchaTime == 1 && tryTimes > 0))) {
            //判断是否通过ajax方式已经验证过
            if (!BooleanBaseOpt.castObjectToBoolean(
                request.getSession().getAttribute(
                    SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT),
                false)) {
                String requestCheckcode = request.getParameter(CaptchaImageUtil.REQUESTCHECKCODE);
                String sessionCheckcode = StringBaseOpt.castObjectToString(
                    request.getSession().getAttribute(CaptchaImageUtil.SESSIONCHECKCODE));
                if (!CaptchaImageUtil.checkcodeMatch(sessionCheckcode, requestCheckcode)) {
                    CheckFailLogs.plusCheckFail(request);
                    throw new AuthenticationServiceException(
                        messageSource.getMessage("error.701.invalid_check_code", null,
                            "Invalid check code.", WebOptUtils.getCurrentLocale(request)));
                }
            }
            //清除临时验证码，避免多次重复验证
            request.getSession().setAttribute(
                CaptchaImageUtil.SESSIONCHECKCODE, CaptchaImageUtil.getRandomString(6));
            request.getSession().setAttribute(
                SecurityContextUtils.AJAX_CHECK_CAPTCHA_RESULT, false);
        }
        //if(!onlyPretreat || writeLog || CheckFailLogs.getMaxTryTimes() > 0){
        try {
            String username = obtainUsername(request);
            username = SecurityOptUtils.decodeSecurityString(username);
            String password = obtainPassword(request);
            password = SecurityOptUtils.decodeSecurityString(password);

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            // Allow subclasses to set the "details" property
            setDetails(request, authRequest);
            Authentication auth = this.getAuthenticationManager().authenticate(authRequest);
            CheckFailLogs.removeCheckFail(request);
            return auth;
        } catch (AuthenticationException failed) {
            CheckFailLogs.plusCheckFail(request);
            throw failed;
        }
    }

}
