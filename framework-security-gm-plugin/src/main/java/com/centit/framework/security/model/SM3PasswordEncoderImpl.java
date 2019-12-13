package com.centit.framework.security.model;


import com.centit.framework.security.utils.SM3Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by codefan on 17-1-20.
 * 采用Spring 推荐的 BCryptPasswordEncoder 加密方式
 */
public class SM3PasswordEncoderImpl
        implements CentitPasswordEncoder, PasswordEncoder {

    @Override
    public String createPassword(String rawPass, Object salt) {
        return new String(Base64.encodeBase64URLSafe(
            SM3Util.hash(rawPass.getBytes())));
    }


    @Override
    public boolean isPasswordValid(String encodedPassword, String rawPass, Object salt) {
        return StringUtils.equals(
            encodedPassword, createPassword(rawPass, salt));
    }

    /**
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
     * greater hash combined with an 8-byte or greater randomly generated salt.
     *
     * @param rawPassword 明文密码
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return createPassword(String.valueOf(rawPassword), null);
    }

    /**
     * Verify the encoded password obtained from storage matches the submitted raw
     * password after it too is encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param rawPassword     明文密码 the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from
     * storage
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return isPasswordValid(String.valueOf(encodedPassword),
            String.valueOf(rawPassword), null);
    }
}
