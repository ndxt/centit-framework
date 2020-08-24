package com.centit.framework.security.test;

import com.centit.framework.security.utils.SM3Util;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class TestSM3 {
    public static void main(String[] args) {
        System.out.println(
            Hex.encodeHex(SM3Util.hash(
                Base64.encodeBase64("2nsykc".getBytes()))));
    }
}

