package com.centit.framework.security.model;


/**
 * Created by codefan on 17-1-20.
 */
@SuppressWarnings("deprecation")
public interface CentitPasswordEncoder {

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
