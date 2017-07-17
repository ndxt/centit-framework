package com.centit.framework.security.model;


import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by codefan on 17-1-20.
 */
@SuppressWarnings("deprecation")
public interface CentitPasswordEncoder extends PasswordEncoder {
    String pretreatPassword(String rawPass);

    String pretreatPassword(String rawPass, Object salt);

    String createPassword(String rawPass, Object salt);

    String encodePassword(String rawPass, Object salt);

    boolean isPasswordValid(String encodedPassword, String rawPass, Object salt);

}
