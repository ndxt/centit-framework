package com.centit.framework.test;

import com.centit.framework.security.model.CentitPasswordEncoder;
import com.centit.framework.security.model.CentitPasswordEncoderImpl;
import com.centit.framework.security.model.StandardPasswordEncoderImpl;
import org.junit.Test;

/**
 * Created by codefan on 17-6-29.
 */
public class TestPasswordEncoder {
    @Test
    public void testCentitCrypt() {
        //093ce7e6c0476c39ce4a46ce53dbcc98b53c30c6baf92e98
        System.out.println(StandardPasswordEncoderImpl.createPassword("000000", "salt", 11));
        //System.out.println(CentitCrypt.cryptPassword2("000000", "salt", 11));
    }

    public void testPE() {
        CentitPasswordEncoder pe = new CentitPasswordEncoderImpl();
        String password = pe.createPassword("000000",null);
        System.out.println(password);

        password = pe.createPassword("000000",null);
        System.out.println(password);
        password = pe.createPassword("000000",null);
        System.out.println(password);
        password = pe.createPassword("000000",null);
        System.out.println(password);
        password = pe.createPassword("000000",null);
        System.out.println(password);
        password = pe.createPassword("000000",null);
        System.out.println(password);

        System.out.println(
                pe.matches("000000","$2a$11$xLOqxWXU6laDFfbiHP/vmOCEHGXzawFJ5ZSRARTvA1ipUwS5m9lPS"));
        System.out.println(
                pe.matches("000000","$2a$11$DbyFNhHeCES5CKoMuM5sXepY7GM35sZkUSqQbjYJnFTzJ2GDIYGLK"));
        System.out.println(
                pe.matches("000000","$2a$11$u8YDC0UY.kfnpBanUOQaRO01bxFgOkvK.82QhMCaGJSsakrf3On9G"));
        System.out.println(
                pe.matches("000000","$2a$11$BXXUjbJqkhqcu4CvBRljhOwws/85qgGUqKrvZPsVnKCsrsemaKmEG"));
        System.out.println(
                pe.matches("000000","$2a$11$1vXImmiBQaiMss9UeHp8JeCKKwnqu5A0TfezczPomAtW6YeLylUKy"));
        System.out.println(
                pe.matches("000000","$2a$11$BM5bvHpT56ekl/i/Qw7v1u70a4hdJh9xCrn7.8bg.yF1vQRSFSLO."));
    }
}
/*
$2a$11$xLOqxWXU6laDFfbiHP/vmOCEHGXzawFJ5ZSRARTvA1ipUwS5m9lPS
$2a$11$DbyFNhHeCES5CKoMuM5sXepY7GM35sZkUSqQbjYJnFTzJ2GDIYGLK
$2a$11$u8YDC0UY.kfnpBanUOQaRO01bxFgOkvK.82QhMCaGJSsakrf3On9G
$2a$11$BXXUjbJqkhqcu4CvBRljhOwws/85qgGUqKrvZPsVnKCsrsemaKmEG
$2a$11$1vXImmiBQaiMss9UeHp8JeCKKwnqu5A0TfezczPomAtW6YeLylUKy
$2a$11$BM5bvHpT56ekl/i/Qw7v1u70a4hdJh9xCrn7.8bg.yF1vQRSFSLO.
*/
