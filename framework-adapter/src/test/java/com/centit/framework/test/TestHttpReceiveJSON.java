package com.centit.framework.test;

import com.centit.framework.appclient.HttpReceiveJSON;
import com.centit.support.json.JSONOpt;

public class TestHttpReceiveJSON {
    public static void main(String[] args) {
        JSONOpt.fastjsonGlobalConfig();
        String obj = "{code:200,message:\"Hello World!\", callback:\"recall\", data: 200}";
        HttpReceiveJSON receiveJSON = HttpReceiveJSON.valueOfJson("500");
        System.out.println(receiveJSON.toResponseData().toJSONString());
        receiveJSON = HttpReceiveJSON.dataOfJson("Hello World!");
        System.out.println(receiveJSON.toResponseData().toJSONString());
        receiveJSON = HttpReceiveJSON.valueOfJson(obj);
        System.out.println(receiveJSON.toResponseData().toJSONString());
        receiveJSON = HttpReceiveJSON.dataOfJson(obj);
        System.out.println(receiveJSON.toResponseData().toJSONString());
    }
}
