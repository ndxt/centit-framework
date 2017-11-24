package com.centit.framework.test;

import com.alibaba.fastjson.JSON;
import com.centit.framework.common.ResponseJSON;
import com.centit.framework.common.ResponseMapData;
import com.centit.support.algorithm.StringBaseOpt;

import java.util.List;

/**
 * Created by codefan on 17-8-4.
 */
public class TestJson {
    public static void main(String[] args) {
        /*ResponseMapData resData = new ResponseMapData();
        resData.addResponseData("hello","world");
        resData.addResponseData("hello2","world");
        System.out.println(resData.toJSONString());
        System.out.println(JSON.toJSONString(true));
*/
        String jsonStr = "{\"code\":0,\"message\":\"ok\"," +
                "\"data\":[1,2,3,4,5]}";
        ResponseJSON json = ResponseJSON.valueOfJson(jsonStr);
        //Integer ints = json.getDataAsObject(Integer.class);
        System.out.println(json.getDataAsString());
        //System.out.println(StringBaseOpt.castObjectToString(ints));

    }
}
