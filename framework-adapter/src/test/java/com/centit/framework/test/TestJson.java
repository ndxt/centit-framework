package com.centit.framework.test;

import com.alibaba.fastjson.JSON;
import com.centit.framework.core.common.ResponseMapData;
import com.centit.framework.core.common.ResponseSingleData;

/**
 * Created by codefan on 17-8-4.
 */
public class TestJson {
    public static void main(String[] args) {
        ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("hello","world");
        resData.addResponseData("hello2","world");
        System.out.println(JSON.toJSONString(resData));
    }
}
