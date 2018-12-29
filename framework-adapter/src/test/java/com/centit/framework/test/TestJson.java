package com.centit.framework.test;

import com.alibaba.fastjson.JSON;
import com.centit.framework.appclient.HttpReceiveJSON;

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
        String jsonStr = "[1,2,3,4,5]";
        HttpReceiveJSON json = HttpReceiveJSON.valueOfJson(jsonStr);
        //Integer ints = json.getDataAsObject(Integer.class);
        System.out.println(json.getDataAsString());

        json = HttpReceiveJSON.valueOfJson("102");
        System.out.println(json.getDataAsString());

        json = HttpReceiveJSON.valueOfJson("true");
        //Integer ints = json.getDataAsObject(Integer.class);
        System.out.println(json.getData());
        //System.out.println(StringBaseOpt.castObjectToString(ints));

    }
}
