package com.centit.framework.test;
import java.util.Locale;
import java.util.ResourceBundle;

public class TestI18n {
    public static void main(String[] args) {
        ResourceBundle zhMessages = ResourceBundle.getBundle("i18n/messages", Locale.SIMPLIFIED_CHINESE);
        System.out.println(zhMessages.getString("greeting"));
        ResourceBundle enMessages = ResourceBundle.getBundle("i18n/messages", Locale.US);
        System.out.println(enMessages.getString("greeting"));
    }

}
