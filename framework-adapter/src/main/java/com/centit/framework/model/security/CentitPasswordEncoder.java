package com.centit.framework.model.security;


import org.apache.commons.lang3.StringUtils;

/**
 * Created by codefan on 17-1-20.
 */
@SuppressWarnings("deprecation")
public interface CentitPasswordEncoder {

    /**
     *
     * @param password 密码
     * @param minLength 最小长度
     * @return 返回值 &lt; 0 不符合要求， 1～4 之间为密码强度
     */
    static int checkPasswordStrength(String password, int minLength){
        if(StringUtils.isBlank(password)){
            return -1;
        }
        int passwrodLen = password.length();
        int mathLength = passwrodLen>=minLength? 1 : -1;
        int hasDigit = 0, hasLowLetter = 0, hasUpLetter = 0, hasOtherLetter = 0;
        for(int i=0; i<passwrodLen; i++){
            char c = password.charAt(i);
            if(c>='0' && c<='9'){
                hasDigit = 1;
            } else if(c>='a' && c<='z'){
                hasLowLetter = 1;
            } else if(c>='A' && c<='Z'){
                hasUpLetter = 1;
            } else {
                hasOtherLetter = 1;
            }
        }
        return mathLength * (hasDigit + hasLowLetter + hasUpLetter + hasOtherLetter);
    }

    static int checkPasswordStrength(String password){
        return checkPasswordStrength(password, 8);
    }

    String encodePassword(String rawPass, Object salt);
    /**
     * 在 encodePassword 前疊加 预处理
     * @param rawPass 明文原始密码
     * @param salt 盐
     * @return 密文
     */
    String createPassword(String rawPass, Object salt);

    boolean isPasswordValid(String encodedPassword, String rawPass, Object salt);

    default boolean isCorrectPasswordFormat(String password){
        return true;
    }

}
