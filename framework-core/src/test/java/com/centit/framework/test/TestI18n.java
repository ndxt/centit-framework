package com.centit.framework.test;
import java.util.Locale;
import java.util.ResourceBundle;

public class TestI18n {

    private static void printMessage(String msg, Object... args){
        System.out.println(msg);
        if(args!=null){
            for(Object obj : args){
                System.out.println(obj);
            }
        }
    }

    public static void main(String[] args) {
        Object obj = new Object[]{"begin", "end"};
        printMessage("hello", (Object[])obj);
        ResourceBundle zhMessages = ResourceBundle.getBundle("i18n/messages", Locale.SIMPLIFIED_CHINESE);
        System.out.println(zhMessages.getString("greeting"));
        ResourceBundle enMessages = ResourceBundle.getBundle("i18n/messages", Locale.US);
        System.out.println(enMessages.getString("greeting"));
    }

}
