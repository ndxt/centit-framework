package com.centit.framework.security.test;

import com.centit.framework.security.utils.SM4Util;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class TestSM4 {
    public static void main(String[] args) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        byte[] cipherText = SM4Util.encryptEcbPadding("encrypt-password".getBytes(StandardCharsets.UTF_8),
           "Hello SM4!".getBytes(StandardCharsets.UTF_8) );
        System.out.println(
            Hex.encodeHex(cipherText));

        byte[] decodeText = SM4Util.decryptEcbPadding("encrypt-password".getBytes(StandardCharsets.UTF_8), cipherText);
        System.out.println(new String(decodeText));
    }
}

